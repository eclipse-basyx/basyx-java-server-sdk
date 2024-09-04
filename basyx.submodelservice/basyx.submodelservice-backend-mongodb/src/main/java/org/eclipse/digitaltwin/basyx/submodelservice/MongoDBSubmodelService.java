/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FeatureNotSupportedException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.springframework.data.repository.CrudRepository;

/**
 * Implements the SubmodelService as MongoDB variant
 * 
 * @author zhangzai, mateusmolina
 * 
 */
public class MongoDBSubmodelService implements SubmodelService {

	private final FileRepository fileRepository;

	private CrudRepository<Submodel, String> crudRepository;

	private Submodel submodel;

	/**
	 * Creates the MongoDB SubmodelService containing the passed Submodel
	 * 
	 * @param submodel
	 */

	public MongoDBSubmodelService(Submodel submodel, FileRepository fileRepository, CrudRepository<Submodel, String> crudRepository) {
		this.submodel = submodel;
		this.fileRepository = fileRepository;
		this.crudRepository = crudRepository;
		crudRepository.save(submodel);
	}

	private InMemorySubmodelService getInMemorySubmodelService() {
		return new InMemorySubmodelService(getSubmodel(), fileRepository);
	}

	@Override
	public Submodel getSubmodel() {
		return crudRepository.findById(submodel.getId()).get();
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(PaginationInfo pInfo) {
		return getInMemorySubmodelService().getSubmodelElements(pInfo);
	}

	@Override
	public SubmodelElement getSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		return getInMemorySubmodelService().getSubmodelElement(idShortPath);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String idShortPath) throws ElementDoesNotExistException {
		return getInMemorySubmodelService().getSubmodelElementValue(idShortPath);
	}

	@Override
	public void setSubmodelElementValue(String idShortPath, SubmodelElementValue value) throws ElementDoesNotExistException {
		InMemorySubmodelService inMemorySubmodelService = getInMemorySubmodelService();
		inMemorySubmodelService.setSubmodelElementValue(idShortPath, value);
		Submodel submodel = inMemorySubmodelService.getSubmodel();
		crudRepository.save(submodel);

	}

	@Override
	public void createSubmodelElement(SubmodelElement submodelElement) {
		InMemorySubmodelService inMemorySubmodelService = getInMemorySubmodelService();
		inMemorySubmodelService.createSubmodelElement(submodelElement);
		Submodel submodel = inMemorySubmodelService.getSubmodel();
		crudRepository.save(submodel);
	}

	@Override
	public void createSubmodelElement(String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		InMemorySubmodelService inMemorySubmodelService = getInMemorySubmodelService();
		inMemorySubmodelService.createSubmodelElement(idShortPath, submodelElement);
		Submodel submodel = inMemorySubmodelService.getSubmodel();
		crudRepository.save(submodel);

	}

	@Override
	public void updateSubmodelElement(String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		InMemorySubmodelService inMemorySubmodelService = getInMemorySubmodelService();
		inMemorySubmodelService.updateSubmodelElement(idShortPath, submodelElement);
		Submodel submodel = inMemorySubmodelService.getSubmodel();
		crudRepository.save(submodel);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		InMemorySubmodelService inMemorySubmodelService = getInMemorySubmodelService();
		inMemorySubmodelService.deleteSubmodelElement(idShortPath);
		Submodel submodel = inMemorySubmodelService.getSubmodel();
		crudRepository.save(submodel);

	}

	@Override
	public void patchSubmodelElements(List<SubmodelElement> submodelElementList) {
		InMemorySubmodelService inMemorySubmodelService = getInMemorySubmodelService();
		inMemorySubmodelService.patchSubmodelElements(submodelElementList);
		Submodel submodel = inMemorySubmodelService.getSubmodel();
		crudRepository.save(submodel);
	}

	@Override
	public OperationVariable[] invokeOperation(String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
		throw new FeatureNotSupportedException("invokeOperation");
	}

	@Override
	public File getFileByPath(String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		return getInMemorySubmodelService().getFileByPath(idShortPath);
	}

	@Override
	public void setFileValue(String idShortPath, String fileName, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
		InMemorySubmodelService inMemorySubmodelService = getInMemorySubmodelService();
		inMemorySubmodelService.setFileValue(idShortPath, fileName, inputStream);
		Submodel submodel = inMemorySubmodelService.getSubmodel();
		crudRepository.save(submodel);
	}

	@Override
	public void deleteFileValue(String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		InMemorySubmodelService inMemorySubmodelService = getInMemorySubmodelService();
		inMemorySubmodelService.deleteFileValue(idShortPath);
		Submodel submodel = inMemorySubmodelService.getSubmodel();
		crudRepository.save(submodel);

	}

	@Override
	public InputStream getFileByFilePath(String filePath) {
		return getInMemorySubmodelService().getFileByFilePath(filePath);
	}

}
