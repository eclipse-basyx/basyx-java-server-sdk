/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.basyx.InvokableOperation;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotInvokableException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.eclipse.digitaltwin.basyx.submodelservice.pathparsing.HierarchicalSubmodelElementParser;
import org.eclipse.digitaltwin.basyx.submodelservice.pathparsing.SubmodelElementIdShortHelper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.factory.SubmodelElementValueMapperFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.ValueMapper;


/**
 * Implements the SubmodelService as in-memory variant
 * 
 * @author schnicke, danish
 * 
 */
public class InMemorySubmodelService implements SubmodelService {

	private final Submodel submodel;
	private HierarchicalSubmodelElementParser parser;
	private SubmodelElementIdShortHelper helper = new SubmodelElementIdShortHelper();

	/**
	 * Creates the InMemory SubmodelService containing the passed Submodel
	 * 
	 * @param submodel
	 */
	public InMemorySubmodelService(Submodel submodel) {
		this.submodel = submodel;
		parser = new HierarchicalSubmodelElementParser(submodel);
	}

	@Override
	public Submodel getSubmodel() {
		return submodel;
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(PaginationInfo pInfo) {
		List<SubmodelElement> allSubmodels = submodel.getSubmodelElements();

		TreeMap<String, SubmodelElement> submodelMap = allSubmodels.stream()
				.collect(Collectors.toMap(SubmodelElement::getIdShort, aas -> aas, (a, b) -> a, TreeMap::new));

		PaginationSupport<SubmodelElement> paginationSupport = new PaginationSupport<>(submodelMap,
				SubmodelElement::getIdShort);
		CursorResult<List<SubmodelElement>> paginatedSubmodels = paginationSupport.getPaged(pInfo);
		return paginatedSubmodels;
	}

	@Override
	public SubmodelElement getSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		return parser.getSubmodelElementFromIdShortPath(idShortPath);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String idShort) throws ElementDoesNotExistException {
		SubmodelElementValueMapperFactory submodelElementValueFactory = new SubmodelElementValueMapperFactory();
		
		return submodelElementValueFactory.create(getSubmodelElement(idShort)).getValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSubmodelElementValue(String idShort, SubmodelElementValue value) throws ElementDoesNotExistException {
		SubmodelElementValueMapperFactory submodelElementValueFactory = new SubmodelElementValueMapperFactory();
		
		ValueMapper<SubmodelElementValue> valueMapper = submodelElementValueFactory.create(getSubmodelElement(idShort));
		
		valueMapper.setValue(value);	
	}

	@Override
	public void createSubmodelElement(SubmodelElement submodelElement) throws CollidingIdentifierException {
		throwIfSubmodelElementExists(submodelElement.getIdShort());
		
		List<SubmodelElement> smElements = submodel.getSubmodelElements();
		smElements.add(submodelElement);
		submodel.setSubmodelElements(smElements);
	}
	
	private void throwIfSubmodelElementExists(String submodelElementId) {
		try {
			getSubmodelElement(submodelElementId);
			throw new CollidingIdentifierException(submodelElementId); 
		}catch(ElementDoesNotExistException e) {
			return;
		}
	}

	@Override
	public void createSubmodelElement(String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException, CollidingIdentifierException {
		throwIfSubmodelElementExists(getFullIdShortPath(idShortPath, submodelElement.getIdShort()));
		
		SubmodelElement parentSme = parser.getSubmodelElementFromIdShortPath(idShortPath);
		if(parentSme instanceof SubmodelElementList) {
			SubmodelElementList list = (SubmodelElementList) parentSme;
			List<SubmodelElement> submodelElements = list.getValue();
			submodelElements.add(submodelElement);
			list.setValue(submodelElements);
			return;
		}
		if (parentSme instanceof SubmodelElementCollection) {
			SubmodelElementCollection collection = (SubmodelElementCollection) parentSme;
			List<SubmodelElement> submodelElements = collection.getValue();
			submodelElements.add(submodelElement);
			collection.setValue(submodelElements);
			return;
		}
	}
	
	@Override
	public void updateSubmodelElement(String idShortPath, SubmodelElement submodelElement) {
		deleteSubmodelElement(idShortPath);
		
		String idShortPathParentSME = parser.getIdShortPathOfParentElement(idShortPath);
		
		createSubmodelElement(idShortPathParentSME, submodelElement);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		if (!helper.isNestedIdShortPath(idShortPath)) {
			deleteFlatSubmodelElement(idShortPath);
			return;
		}
		deleteNestedSubmodelElement(idShortPath);
	}

	private void deleteNestedSubmodelElement(String idShortPath) {
		SubmodelElement sm = parser.getSubmodelElementFromIdShortPath(idShortPath);
		if(helper.isDirectParentASubmodelElementList(idShortPath)) {
			deleteNestedSubmodelElementFromList(idShortPath, sm);
		} else {
			deleteNestedSubmodelElementFromCollection(idShortPath, sm);
		}
	}

	private void deleteNestedSubmodelElementFromList(String idShortPath, SubmodelElement sm) {
		String collectionId = helper.extractDirectParentSubmodelElementListIdShort(idShortPath);
		SubmodelElementList list = (SubmodelElementList) parser.getSubmodelElementFromIdShortPath(collectionId);
		list.getValue().remove(sm);
	}

	private void deleteNestedSubmodelElementFromCollection(String idShortPath, SubmodelElement sm) {
		String collectionId = helper.extractDirectParentSubmodelElementCollectionIdShort(idShortPath);
		SubmodelElementCollection collection = (SubmodelElementCollection) parser
				.getSubmodelElementFromIdShortPath(collectionId);
		collection.getValue().remove(sm);
	}

	private void deleteFlatSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		int index = findIndexOfElementTobeDeleted(idShortPath);
		if (index >= 0) {
			submodel.getSubmodelElements().remove(index);
			return;
		}
		throw new ElementDoesNotExistException();
	}

	private int findIndexOfElementTobeDeleted(String idShortPath) {
		for (SubmodelElement sme : submodel.getSubmodelElements()) {
			if (sme.getIdShort().equals(idShortPath)) {
				return submodel.getSubmodelElements().indexOf(sme);
			}
		}
		return -1;
	}

	@Override
	public OperationVariable[] invokeOperation(String idShortPath, OperationVariable[] input) {
		SubmodelElement sme = getSubmodelElement(idShortPath);
		
		if (!(sme instanceof InvokableOperation))
			throw new NotInvokableException(idShortPath);
		
		InvokableOperation operation = (InvokableOperation) sme;
		return operation.invoke(input);
	}
	
	private String getFullIdShortPath(String idShortPath, String submodelElementId) {
		return idShortPath + "." + submodelElementId;
	}
}
