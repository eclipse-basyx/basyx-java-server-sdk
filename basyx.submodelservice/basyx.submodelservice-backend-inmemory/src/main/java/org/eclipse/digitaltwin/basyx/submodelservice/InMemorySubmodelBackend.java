/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.submodelservice;

import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.basyx.common.backend.inmemory.core.InMemoryCrudRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.SubmodelBackend;
import org.eclipse.digitaltwin.basyx.submodelservice.pathparsing.HierarchicalSubmodelElementParser;
import org.eclipse.digitaltwin.basyx.submodelservice.pathparsing.SubmodelElementIdShortHelper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.factory.SubmodelElementValueMapperFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Implements the SubmodelService as in-memory variant
 *
 * @author schnicke, danish, mateusmolina
 */
@ConditionalOnExpression("'${basyx.submodelservice.backend}'.equals('InMemory') or '${basyx.backend}'.equals('InMemory')")
@Component
public class InMemorySubmodelBackend extends InMemoryCrudRepository<Submodel> implements SubmodelBackend {

    public InMemorySubmodelBackend() {
        super(Submodel::getId);
    }

    @Override
    public CursorResult<List<Submodel>> getSubmodels(String semanticId, PaginationInfo pInfo) {
        Iterable<Submodel> iterable = findAll();
		List<Submodel> submodels = StreamSupport.stream(iterable.spliterator(), false).toList();

	    List<Submodel> filteredSubmodels = submodels.stream()
	    		.filter((submodel) -> {
	    			return submodel.getSemanticId() != null &&
	    				submodel.getSemanticId().getKeys().stream().filter((key) -> {
	    					return key.getValue().equals(semanticId);
	    				}).findAny().isPresent();
	    		})
	    		.collect(Collectors.toList());

		TreeMap<String, Submodel> submodelMap = filteredSubmodels.stream().collect(Collectors.toMap(Submodel::getId, submodel -> submodel, (a, b) -> a, TreeMap::new));

		PaginationSupport<Submodel> paginationSupport = new PaginationSupport<>(submodelMap, Submodel::getId);

		return paginationSupport.getPaged(pInfo);
    }

    @Override
    public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo) {
        List<SubmodelElement> allSubmodels = getSubmodel(submodelId).getSubmodelElements();

        TreeMap<String, SubmodelElement> submodelMap = allSubmodels.stream().collect(Collectors.toMap(SubmodelElement::getIdShort, aas -> aas, (a, b) -> a, TreeMap::new));

        PaginationSupport<SubmodelElement> paginationSupport = new PaginationSupport<>(submodelMap, SubmodelElement::getIdShort);
        return paginationSupport.getPaged(pInfo);
    }

    @Override
    public SubmodelElement getSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
        return getParser(submodelId).getSubmodelElementFromIdShortPath(idShortPath);
    }

    @Override
    public SubmodelElementValue getSubmodelElementValue(String submodelId, String idShort) throws ElementDoesNotExistException {
        return SubmodelElementValueMapperFactory.create(getSubmodelElement(submodelId, idShort)).getValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized void setSubmodelElementValue(String submodelId, String idShort, SubmodelElementValue value) throws ElementDoesNotExistException {
        SubmodelElementValueMapperFactory.create(getSubmodelElement(submodelId, idShort)).setValue(value);
    }

    @Override
    public synchronized void createSubmodelElement(String submodelId, SubmodelElement submodelElement) throws CollidingIdentifierException {
        List<SubmodelElement> smElements = getSubmodel(submodelId).getSubmodelElements();
        throwIfSubmodelElementExists(submodelId, submodelElement.getIdShort());
        smElements.add(submodelElement);
    }

    @Override
    public synchronized void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException, CollidingIdentifierException {
        throwIfSubmodelElementExists(submodelId, getFullIdShortPath(idShortPath, submodelElement.getIdShort()));

        SubmodelElement parentSme = getParser(submodelId).getSubmodelElementFromIdShortPath(idShortPath);
        if (parentSme instanceof SubmodelElementList list) {
            List<SubmodelElement> submodelElements = list.getValue();
            submodelElements.add(submodelElement);
        } else if (parentSme instanceof SubmodelElementCollection collection) {
            List<SubmodelElement> submodelElements = collection.getValue();
            submodelElements.add(submodelElement);
        } else if (parentSme instanceof Entity entity) {
            List<SubmodelElement> submodelElements = entity.getStatements();
            submodelElements.add(submodelElement);
        }
    }

    @Override
    public synchronized void updateSubmodelElement(String submodelId, String idShortPath, SubmodelElement submodelElement) {
        deleteSubmodelElement(submodelId, idShortPath);

        String idShortPathParentSME = getParser(submodelId).getIdShortPathOfParentElement(idShortPath);
        if (idShortPath.equals(idShortPathParentSME)) {
            createSubmodelElement(submodelId, submodelElement);
            return;
        }

        createSubmodelElement(submodelId, idShortPathParentSME, submodelElement);
    }

    @Override
    public synchronized void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {

        Submodel submodel = getSubmodel(submodelId);
        if (!SubmodelElementIdShortHelper.isNestedIdShortPath(idShortPath)) {
            deleteFlatSubmodelElement(submodel, idShortPath);
            return;
        }
        deleteNestedSubmodelElement(submodel, idShortPath);

    }

    @Override
    public synchronized void patchSubmodelElements(String submodelId, List<SubmodelElement> submodelElementList) {
        getSubmodel(submodelId).setSubmodelElements(submodelElementList);
    }

    private static void deleteNestedSubmodelElement(Submodel submodel, String idShortPath) {
        HierarchicalSubmodelElementParser parser = new HierarchicalSubmodelElementParser(submodel);
        SubmodelElement sme = parser.getSubmodelElementFromIdShortPath(idShortPath);
        if (SubmodelElementIdShortHelper.isDirectParentASubmodelElementList(idShortPath)) {
            deleteNestedSubmodelElementFromList(parser, idShortPath, sme);
        } else {
            deleteNestedSubmodelElementFromCollectionOrEntity(parser, idShortPath, sme);
        }
    }

    private static void deleteNestedSubmodelElementFromList(HierarchicalSubmodelElementParser parser, String idShortPath, SubmodelElement sme) {
        String collectionId = SubmodelElementIdShortHelper.extractDirectParentSubmodelElementListIdShort(idShortPath);
        SubmodelElementList list = (SubmodelElementList) parser.getSubmodelElementFromIdShortPath(collectionId);
        list.getValue().remove(sme);
    }

    private static void deleteNestedSubmodelElementFromCollectionOrEntity(HierarchicalSubmodelElementParser parser, String idShortPath, SubmodelElement sme) {
        String collectionId = SubmodelElementIdShortHelper.extractDirectParentSubmodelElementCollectionIdShort(idShortPath);
        SubmodelElement parent = parser.getSubmodelElementFromIdShortPath(collectionId);
        if (parent instanceof SubmodelElementCollection collection) {
            collection.getValue().remove(sme);
        } else if (parent instanceof Entity entity) {
            entity.getStatements().remove(sme);
        }
    }

    private static void deleteFlatSubmodelElement(Submodel submodel, String idShortPath) throws ElementDoesNotExistException {
        int index = findIndexOfElementTobeDeleted(submodel, idShortPath);
        if (index >= 0) {
            submodel.getSubmodelElements().remove(index);
            return;
        }
        throw new ElementDoesNotExistException();
    }

    private static int findIndexOfElementTobeDeleted(Submodel submodel, String idShortPath) {
        for (SubmodelElement sme : submodel.getSubmodelElements()) {
            if (sme.getIdShort().equals(idShortPath)) {
                return submodel.getSubmodelElements().indexOf(sme);
            }
        }
        return -1;
    }

    private static String getFullIdShortPath(String idShortPath, String submodelElementId) {
        return idShortPath + "." + submodelElementId;
    }

    private void throwIfSubmodelElementExists(String submodelId, String submodelElementId) {
        try {
            getSubmodelElement(submodelId, submodelElementId);
            throw new CollidingIdentifierException(submodelElementId);
        } catch (ElementDoesNotExistException e) {
        }
    }

    private HierarchicalSubmodelElementParser getParser(String submodelId) {
        return new HierarchicalSubmodelElementParser(getSubmodel(submodelId));
    }

    private Submodel getSubmodel(String submodelId) {
        return findById(submodelId).orElseThrow(() -> new ElementDoesNotExistException(submodelId));
    }
}
