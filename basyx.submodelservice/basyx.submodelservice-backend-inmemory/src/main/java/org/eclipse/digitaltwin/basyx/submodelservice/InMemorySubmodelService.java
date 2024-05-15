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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.basyx.InvokableOperation;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotInvokableException;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileMetadata;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.submodelservice.pathparsing.HierarchicalSubmodelElementParser;
import org.eclipse.digitaltwin.basyx.submodelservice.pathparsing.SubmodelElementIdShortHelper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.FileBlobValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.factory.SubmodelElementValueMapperFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.ValueMapper;

/**
 * Implements the SubmodelService as in-memory variant
 * 
 * @author schnicke, danish, mateusmolina
 * 
 */
public class InMemorySubmodelService implements SubmodelService {

	private final Submodel submodel;
	private HierarchicalSubmodelElementParser parser;
	private SubmodelElementIdShortHelper helper = new SubmodelElementIdShortHelper();

	private final FileRepository fileRepository;

	/**
	 * Creates the InMemory SubmodelService containing the passed Submodel
	 * 
	 * @param submodel
	 */
	public InMemorySubmodelService(Submodel submodel, FileRepository fileRepository) {
		this.submodel = submodel;
		this.fileRepository = fileRepository;
		parser = new HierarchicalSubmodelElementParser(submodel);
	}

	@Override
	public Submodel getSubmodel() {
		return submodel;
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(PaginationInfo pInfo) {
		List<SubmodelElement> allSubmodels = submodel.getSubmodelElements();

		TreeMap<String, SubmodelElement> submodelMap = allSubmodels.stream().collect(Collectors.toMap(SubmodelElement::getIdShort, aas -> aas, (a, b) -> a, TreeMap::new));

		PaginationSupport<SubmodelElement> paginationSupport = new PaginationSupport<>(submodelMap, SubmodelElement::getIdShort);
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
		} catch (ElementDoesNotExistException e) {
			return;
		}
	}

	@Override
	public void createSubmodelElement(String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException, CollidingIdentifierException {
		throwIfSubmodelElementExists(getFullIdShortPath(idShortPath, submodelElement.getIdShort()));

		SubmodelElement parentSme = parser.getSubmodelElementFromIdShortPath(idShortPath);
		if (parentSme instanceof SubmodelElementList) {
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
		if (idShortPath.equals(idShortPathParentSME)) {
			createSubmodelElement(submodelElement);
			return;
		}
		createSubmodelElement(idShortPathParentSME, submodelElement);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		deleteAssociatedFileIfAny(idShortPath);

		if (!helper.isNestedIdShortPath(idShortPath)) {
			deleteFlatSubmodelElement(idShortPath);
			return;
		}
		deleteNestedSubmodelElement(idShortPath);
	}

	private void deleteNestedSubmodelElement(String idShortPath) {
		SubmodelElement sm = parser.getSubmodelElementFromIdShortPath(idShortPath);
		if (helper.isDirectParentASubmodelElementList(idShortPath)) {
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
		SubmodelElementCollection collection = (SubmodelElementCollection) parser.getSubmodelElementFromIdShortPath(collectionId);
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

	@Override
	public void patchSubmodelElements(List<SubmodelElement> submodelElementList) {
		this.submodel.setSubmodelElements(submodelElementList);
	}

	@Override
	public java.io.File getFileByPath(String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		SubmodelElement submodelElement = getSubmodelElement(idShortPath);

		throwIfSmElementIsNotAFile(submodelElement);

		File fileSmElement = (File) submodelElement;
		String filePath = getFilePath(fileSmElement);

		InputStream fileContent = getFileInputStream(filePath);

		return createFile(filePath, fileContent);
	}

	@Override
	public void setFileValue(String idShortPath, String fileName, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
		SubmodelElement submodelElement = getSubmodelElement(idShortPath);

		throwIfSmElementIsNotAFile(submodelElement);

		File fileSmElement = (File) submodelElement;

		if (fileRepository.exists(fileSmElement.getValue()))
			fileRepository.delete(fileSmElement.getValue());

		String uniqueFileName = createUniqueFileName(idShortPath, fileName);

		FileMetadata fileMetadata = new FileMetadata(uniqueFileName, fileSmElement.getContentType(), inputStream);

		String filePath = fileRepository.save(fileMetadata);

		FileBlobValue fileValue = new FileBlobValue(fileSmElement.getContentType(), filePath);

		setSubmodelElementValue(idShortPath, fileValue);

	}

	@Override
	public void deleteFileValue(String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		SubmodelElement submodelElement = getSubmodelElement(idShortPath);

		throwIfSmElementIsNotAFile(submodelElement);

		File fileSubmodelElement = (File) submodelElement;
		String filePath = fileSubmodelElement.getValue();

		fileRepository.delete(filePath);

		FileBlobValue fileValue = new FileBlobValue(" ", " ");

		setSubmodelElementValue(idShortPath, fileValue);
	}

	private void deleteAssociatedFileIfAny(String idShortPath) {
		try {
			deleteFileValue(idShortPath);
		} catch (Exception e) {
		}
	}

	private boolean isFileSubmodelElement(SubmodelElement submodelElement) {
		return submodelElement instanceof File;
	}

	private InputStream getFileInputStream(String filePath) {
		InputStream fileContent;

		try {
			fileContent = fileRepository.find(filePath);
		} catch (FileDoesNotExistException e) {
			throw new FileDoesNotExistException(String.format("File at path '%s' could not be found.", filePath));
		}

		return fileContent;
	}

	private java.io.File createFile(String filePath, InputStream fileIs) {

		try {
			byte[] content = fileIs.readAllBytes();
			fileIs.close();

			createOutputStream(filePath, content);

			return new java.io.File(filePath);
		} catch (IOException e) {
			throw new FileHandlingException("Exception occurred while creating file from the InputStream." + e.getMessage());
		}

	}

	private String getFilePath(File fileSubmodelElement) {
		return fileSubmodelElement.getValue();
	}

	private String createUniqueFileName(String idShortPath, String fileName) {
		return Base64UrlEncodedIdentifier.encodeIdentifier(submodel.getId()) + "-" + idShortPath.replace("/", "-") + "-" + fileName;
	}

	private void throwIfSmElementIsNotAFile(SubmodelElement submodelElement) {

		if (!isFileSubmodelElement(submodelElement))
			throw new ElementNotAFileException(submodelElement.getIdShort());
	}

	private void createOutputStream(String filePath, byte[] content) throws IOException {

		try (OutputStream outputStream = new FileOutputStream(filePath)) {
			outputStream.write(content);
		} catch (IOException e) {
			throw new FileHandlingException("Exception occurred while creating OutputStream from byte[]." + e.getMessage());
		}

	}

}
