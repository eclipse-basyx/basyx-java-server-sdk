/*******************************************************************************
 * Copyright (C) 2024 DFKI GmbH (https://www.dfki.de/en/web)
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
 * 
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.submodelrepository.feature.kafka;

import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.SubmodelEventHandler;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;

/**
 * @author geso02 (Sonnenberg DFKI GmbH)
 */
public class KafkaSubmodelRepository implements SubmodelRepository {
	
	private final SubmodelRepository decorated;
	private final SubmodelEventHandler eventHandler;
	
	public KafkaSubmodelRepository(SubmodelRepository decorated, SubmodelEventHandler eventHandler) {
		this.decorated = decorated;
		this.eventHandler = eventHandler;
	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(PaginationInfo pInfo) {
		return decorated.getAllSubmodels(pInfo);
	}

	@Override
	public Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException {
		return decorated.getSubmodel(submodelId);
	}

	@Override
	public void updateSubmodel(String submodelId, Submodel submodel) throws ElementDoesNotExistException {
		decorated.updateSubmodel(submodelId, submodel);
		eventHandler.onSubmodelUpdated(submodel);
	}

	@Override
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException {
		decorated.createSubmodel(submodel);
		eventHandler.onSubmodelCreated(submodel);
	}

	@Override
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
		decorated.deleteSubmodel(submodelId);
		eventHandler.onSubmodelDeleted(submodelId);
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo)
			throws ElementDoesNotExistException {
		return decorated.getSubmodelElements(submodelId, pInfo);
	}

	@Override
	public SubmodelElement getSubmodelElement(String submodelId, String smeIdShort)
			throws ElementDoesNotExistException {
		return decorated.getSubmodelElement(submodelId, smeIdShort);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort)
			throws ElementDoesNotExistException {
		return decorated.getSubmodelElementValue(submodelId, smeIdShort);
	}

	@Override
	public void setSubmodelElementValue(String submodelId, String idShortPath, SubmodelElementValue value)
			throws ElementDoesNotExistException {
		decorated.setSubmodelElementValue(submodelId, idShortPath, value);
		SubmodelElement submodelElement = decorated.getSubmodelElement(submodelId, idShortPath);
		eventHandler.onSubmodelElementUpdated(submodelElement, submodelId, idShortPath);
	}
	
	@Override
	public void createSubmodelElement(String submodelId, SubmodelElement submodelElement) {
		decorated.createSubmodelElement(submodelId, submodelElement);
		eventHandler.onSubmodelElementCreated(submodelElement, submodelId, submodelElement.getIdShort());
	}

	@Override
	public void createSubmodelElement(String submodelId, String parentPath, SubmodelElement submodelElement)
			throws ElementDoesNotExistException {
		decorated.createSubmodelElement(submodelId, parentPath, submodelElement);
		String path = computePath(submodelId, parentPath, submodelElement.getIdShort());
		eventHandler.onSubmodelElementCreated(submodelElement, submodelId, path);
	}

	private String computePath(String submodelId, String parentPath, String smeIdShort) {
		SubmodelElement parent = getSubmodelElement(submodelId, parentPath);
			if (parent instanceof SubmodelElementList) {
				SubmodelElementList parentList = (SubmodelElementList) parent;
				int listSize = parentList.getValue().size();
				int pos = listSize -1; // already added starting with zero
				// new element is appended
				return parentPath + "[" + pos + "]";
			} else {
				return parentPath + "." + smeIdShort;
			}
	}

	@Override
	public void updateSubmodelElement(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement)
			throws ElementDoesNotExistException {
		decorated.updateSubmodelElement(submodelIdentifier, idShortPath, submodelElement);
		eventHandler.onSubmodelElementUpdated(submodelElement, submodelIdentifier, idShortPath);
	}

	@Override
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
		decorated.deleteSubmodelElement(submodelId, idShortPath);
		eventHandler.onSubmodelElementDeleted(submodelId, idShortPath);
	}

	@Override
	public String getName() {
		return decorated.getName();
	}

	@Override
	public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) {
		return decorated.getSubmodelByIdValueOnly(submodelId);
	}

	@Override
	public Submodel getSubmodelByIdMetadata(String submodelId) {
		return decorated.getSubmodelByIdMetadata(submodelId);
	}

	@Override
	public OperationVariable[] invokeOperation(String submodelId, String idShortPath, OperationVariable[] input)
			throws ElementDoesNotExistException {
		return decorated.invokeOperation(submodelId, idShortPath, input);
	}

	@Override
	public java.io.File getFileByPathSubmodel(String submodelId, String idShortPath) {
		return decorated.getFileByPathSubmodel(submodelId, idShortPath);
	}

	@Override
	public void deleteFileValue(String identifier, String idShortPath) {
		decorated.deleteFileValue(identifier, idShortPath);
	}

	@Override
	public void setFileValue(String submodelId, String idShortPath, String fileName, String contentType, InputStream inputStream) {
		decorated.setFileValue(submodelId, idShortPath, fileName, contentType, inputStream);
	}

	@Override
	public void patchSubmodelElements(String submodelId, List<SubmodelElement> submodelElementList) {
		decorated.patchSubmodelElements(submodelId, submodelElementList);
		Submodel submodel = getSubmodel(submodelId);
		eventHandler.onSubmodelUpdated(submodel);
	}

	@Override
	public InputStream getFileByFilePath(String submodelId, String filePath) {
		return decorated.getFileByFilePath(submodelId, filePath);
	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(String semanticId, PaginationInfo pInfo) {
		return decorated.getAllSubmodels(semanticId, pInfo);
	}
}
