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

package org.eclipse.digitaltwin.basyx.submodelrepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.tika.utils.StringUtils;
import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.value.FileBlobValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;

/**
 * In-memory implementation of the SubmodelRepository
 *
 * @author schnicke, danish
 *
 */
public class InMemorySubmodelRepository implements SubmodelRepository {

	private Map<String, SubmodelService> submodelServices = new LinkedHashMap<>();
	private SubmodelServiceFactory submodelServiceFactory;
	private String tmpDirectory = getTemporaryDirectoryPath();

	/**
	 * Creates the InMemorySubmodelRepository utilizing the passed
	 * SubmodelServiceFactory for creating new SubmodelServices
	 * 
	 * @param submodelServiceFactory
	 */
	public InMemorySubmodelRepository(SubmodelServiceFactory submodelServiceFactory) {
		this.submodelServiceFactory = submodelServiceFactory;
	}

	/**
	 * Creates the InMemorySubmodelRepository utilizing the passed
	 * SubmodelServiceFactory for creating new SubmodelServices and preconfiguring
	 * it with the passed Submodels
	 * 
	 * @param submodelServiceFactory
	 * @param submodels
	 */
	public InMemorySubmodelRepository(SubmodelServiceFactory submodelServiceFactory, Collection<Submodel> submodels) {
		this(submodelServiceFactory);
		throwIfHasCollidingIds(submodels);

		submodelServices = createServices(submodels);
	}

	private void throwIfHasCollidingIds(Collection<Submodel> submodelsToCheck) {
		Set<String> ids = new HashSet<>();

		submodelsToCheck.stream().map(submodel -> submodel.getId()).filter(id -> !ids.add(id)).findAny().ifPresent(id -> {
			throw new CollidingIdentifierException(id);
		});
	}

	private Map<String, SubmodelService> createServices(Collection<Submodel> submodels) {
		Map<String, SubmodelService> map = new LinkedHashMap<>();
		submodels.forEach(submodel -> map.put(submodel.getId(), submodelServiceFactory.create(submodel)));

		return map;
	}

	@Override
	public Collection<Submodel> getAllSubmodels() {
		return submodelServices.values().stream().map(service -> service.getSubmodel()).collect(Collectors.toList());
	}

	@Override
	public Submodel getSubmodel(String id) throws ElementDoesNotExistException {
		throwIfSubmodelDoesNotExist(id);

		return submodelServices.get(id).getSubmodel();
	}

	@Override
	public void updateSubmodel(String id, Submodel submodel) throws ElementDoesNotExistException {
		throwIfSubmodelDoesNotExist(id);

		throwIfMismatchingIds(id, submodel);

		submodelServices.put(id, submodelServiceFactory.create(submodel));
	}

	@Override
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException {
		throwIfSubmodelExists(submodel.getId());

		submodelServices.put(submodel.getId(), submodelServiceFactory.create(submodel));
	}

	private void throwIfSubmodelExists(String id) {
		if (submodelServices.containsKey(id))
			throw new CollidingIdentifierException(id);
	}

	private void throwIfSubmodelDoesNotExist(String id) {
		if (!submodelServices.containsKey(id))
			throw new ElementDoesNotExistException(id);
	}

	@Override
	public Collection<SubmodelElement> getSubmodelElements(String submodelId) {
		throwIfSubmodelDoesNotExist(submodelId);

		return submodelServices.get(submodelId).getSubmodelElements();
	}

	@Override
	public SubmodelElement getSubmodelElement(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		throwIfSubmodelDoesNotExist(submodelId);

		return submodelServices.get(submodelId).getSubmodelElement(smeIdShort);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		throwIfSubmodelDoesNotExist(submodelId);

		return submodelServices.get(submodelId).getSubmodelElementValue(smeIdShort);
	}

	@Override
	public void setSubmodelElementValue(String submodelId, String smeIdShort, SubmodelElementValue value) throws ElementDoesNotExistException {
		throwIfSubmodelDoesNotExist(submodelId);

		submodelServices.get(submodelId).setSubmodelElementValue(smeIdShort, value);
	}

	@Override
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
		throwIfSubmodelDoesNotExist(submodelId);

		submodelServices.remove(submodelId);
	}

	@Override
	public void createSubmodelElement(String submodelId, SubmodelElement smElement) {
		throwIfSubmodelDoesNotExist(submodelId);

		submodelServices.get(submodelId).createSubmodelElement(smElement);
	}

	@Override
	public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException {
		throwIfSubmodelDoesNotExist(submodelId);

		submodelServices.get(submodelId).createSubmodelElement(idShortPath, smElement);
	}

	@Override
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
		throwIfSubmodelDoesNotExist(submodelId);

		submodelServices.get(submodelId).deleteSubmodelElement(idShortPath);
	}

	@Override
	public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) {
		return new SubmodelValueOnly(getSubmodelElements(submodelId));
	}

	@Override
	public Submodel getSubmodelByIdMetadata(String submodelId) {
		Submodel submodel = getSubmodel(submodelId);
		submodel.setSubmodelElements(null);
		return submodel;
	}

	@Override
	public java.io.File getFileByPathSubmodel(String submodelId, String idShortPath) {
		throwIfSubmodelDoesNotExist(submodelId);
		SubmodelElement submodelElement = submodelServices.get(submodelId).getSubmodelElement(idShortPath);

		throwIfSmElementIsNotAFile(submodelElement);

		File fileSmElement = (File) submodelElement;
		String filePath = fileSmElement.getValue();

		throwIfFileDoesNotExist(fileSmElement, filePath);

		return new java.io.File(filePath);

	}

	@Override
	public void setFileValue(String submodelId, String idShortPath, InputStream inputStream) throws IOException {
		throwIfSubmodelDoesNotExist(submodelId);

		SubmodelElement submodelElement = submodelServices.get(submodelId).getSubmodelElement(idShortPath);
		throwIfSmElementIsNotAFile(submodelElement, inputStream);

		File fileSmElement = (File) submodelElement;
		String filePath = getFilePath(submodelId, idShortPath, fileSmElement);
		java.io.File targetFile = new java.io.File(filePath);

		try (FileOutputStream outStream = new FileOutputStream(targetFile)) {
			IOUtils.copy(inputStream, outStream);
		}

		FileBlobValue fileValue = new FileBlobValue(fileSmElement.getContentType(), filePath);

		setSubmodelElementValue(submodelId, idShortPath, fileValue);

		inputStream.close();
	}

	private String getFilePath(String submodelId, String idShortPath, File file) {
		String fileName = submodelId + "-" + idShortPath.replaceAll("/", "-") + "-" + file.getValue();

		return tmpDirectory + "/" + fileName;
	}

	private void throwIfSmElementIsNotAFile(SubmodelElement submodelElement, InputStream inputStream) throws IOException {
		if (!(submodelElement instanceof File)) {
			inputStream.close();
			throw new ElementNotAFileException(submodelElement.getIdShort());
		}
	}

	private void throwIfSmElementIsNotAFile(SubmodelElement submodelElement) {
		if (!(submodelElement instanceof File))
			throw new ElementNotAFileException(submodelElement.getIdShort());
	}

	@Override
	public void deleteFileValue(String submodelId, String idShortPath) {
		throwIfSubmodelDoesNotExist(submodelId);
		SubmodelElement submodelElement = submodelServices.get(submodelId).getSubmodelElement(idShortPath);

		throwIfSmElementIsNotAFile(submodelElement);

		File fileSubmodelElement = (File) submodelElement;
		String filePath = fileSubmodelElement.getValue();

		throwIfFileDoesNotExist(fileSubmodelElement, filePath);

		java.io.File tmpFile = new java.io.File(filePath);
		tmpFile.delete();

		FileBlobValue fileValue = new FileBlobValue(StringUtils.EMPTY, StringUtils.EMPTY);

		setSubmodelElementValue(submodelId, idShortPath, fileValue);
	}

	private void throwIfMismatchingIds(String smId, Submodel newSubmodel) {
		String newSubmodelId = newSubmodel.getId();

		if (!smId.equals(newSubmodelId))
			throw new IdentificationMismatchException();
	}

	private void throwIfFileDoesNotExist(File fileSmElement, String filePath) {
		if (fileSmElement.getValue().isBlank() || !isFilePathValid(filePath))
			throw new FileDoesNotExistException(fileSmElement.getIdShort());
	}

	private boolean isFilePathValid(String filePath) {
		try {
			Paths.get(filePath);
		} catch (InvalidPathException | NullPointerException ex) {
			return false;
		}
		return true;
	}

	private String getTemporaryDirectoryPath() {

		String tempDirectoryPath = "";

		try {
			tempDirectoryPath = Files.createTempDirectory("basyx-temp").toAbsolutePath().toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tempDirectoryPath;
	}

}
