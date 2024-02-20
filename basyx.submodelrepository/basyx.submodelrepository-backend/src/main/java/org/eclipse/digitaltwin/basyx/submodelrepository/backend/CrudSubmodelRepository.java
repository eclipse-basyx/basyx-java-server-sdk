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

package org.eclipse.digitaltwin.basyx.submodelrepository.backend;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.file.FileMetadata;
import org.eclipse.digitaltwin.basyx.core.file.FileRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.value.FileBlobValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;

/**
 * Default Implementation for the {@link SubmodelRepository} based on Spring
 * {@link CrudRepository}
 * 
 * @author danish
 *
 */
public class CrudSubmodelRepository implements SubmodelRepository {

	private Logger logger = LoggerFactory.getLogger(CrudSubmodelRepository.class);
	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, null);
	private CrudRepository<Submodel, String> submodelBackend;

	private SubmodelServiceFactory submodelServiceFactory;
	private FileRepository fileHandlingBackend;

	private String submodelRepositoryName = null;

	public CrudSubmodelRepository(SubmodelBackendProvider submodelBackendProvider, SubmodelServiceFactory submodelServiceFactory) {
		this.submodelBackend = submodelBackendProvider.getCrudRepository();
		this.submodelServiceFactory = submodelServiceFactory;
		this.fileHandlingBackend = submodelBackendProvider.getFileRepository();
	}

	public CrudSubmodelRepository(SubmodelBackendProvider submodelBackendProvider, SubmodelServiceFactory submodelServiceFactory, String submodelRepositoryName) {
		this(submodelBackendProvider, submodelServiceFactory);

		this.submodelRepositoryName = submodelRepositoryName;
	}

	public CrudSubmodelRepository(SubmodelBackendProvider submodelBackendProvider, SubmodelServiceFactory submodelServiceFactory, Collection<Submodel> submodels) {
		this(submodelBackendProvider, submodelServiceFactory);

		throwIfMissingId(submodels);

		throwIfHasCollidingIds(submodels);

		initializeRemoteCollection(submodels);
	}

	public CrudSubmodelRepository(SubmodelBackendProvider submodelBackendProvider, SubmodelServiceFactory submodelServiceFactory, Collection<Submodel> submodels, String submodelRepositoryName) {
		this(submodelBackendProvider, submodelServiceFactory, submodels);

		this.submodelRepositoryName = submodelRepositoryName;
	}

	@Override
	public String getName() {
		return submodelRepositoryName == null ? SubmodelRepository.super.getName() : submodelRepositoryName;
	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(PaginationInfo pInfo) {
		Iterable<Submodel> iterable = submodelBackend.findAll();
		List<Submodel> submodels = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());

		TreeMap<String, Submodel> submodelMap = submodels.stream().collect(Collectors.toMap(Submodel::getId, submodel -> submodel, (a, b) -> a, TreeMap::new));

		PaginationSupport<Submodel> paginationSupport = new PaginationSupport<>(submodelMap, Submodel::getId);

		return paginationSupport.getPaged(pInfo);
	}

	@Override
	public Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException {
		return submodelBackend.findById(submodelId).orElseThrow(() -> new ElementDoesNotExistException(submodelId));
	}

	@Override
	public void updateSubmodel(String submodelId, Submodel submodel) throws ElementDoesNotExistException {
		throwIfSubmodelDoesNotExist(submodelId);

		throwIfMismatchingIds(submodelId, submodel.getId());

		submodelBackend.save(submodel);
	}

	@Override
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException, MissingIdentifierException {
		throwIfSubmodelIdEmptyOrNull(submodel.getId());

		throwIfSubmodelExists(submodel.getId());

		submodelBackend.save(submodel);
	}

	@Override
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
		throwIfSubmodelDoesNotExist(submodelId);

		submodelBackend.deleteById(submodelId);
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo) throws ElementDoesNotExistException {
		return getSubmodelServiceOrThrow(submodelId).getSubmodelElements(pInfo);
	}

	@Override
	public SubmodelElement getSubmodelElement(String submodelId, String smeIdShortPath) throws ElementDoesNotExistException {
		return getSubmodelServiceOrThrow(submodelId).getSubmodelElement(smeIdShortPath);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		return getSubmodelServiceOrThrow(submodelId).getSubmodelElementValue(smeIdShort);
	}

	@Override
	public void setSubmodelElementValue(String submodelId, String smeIdShort, SubmodelElementValue value) throws ElementDoesNotExistException {
		SubmodelService submodelService = getSubmodelServiceOrThrow(submodelId);

		submodelService.setSubmodelElementValue(smeIdShort, value);

		updateSubmodel(submodelId, submodelService.getSubmodel());
	}

	@Override
	public void createSubmodelElement(String submodelId, SubmodelElement smElement) {
		SubmodelService submodelService = getSubmodelServiceOrThrow(submodelId);

		submodelService.createSubmodelElement(smElement);

		updateSubmodel(submodelId, submodelService.getSubmodel());
	}

	@Override
	public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException {
		SubmodelService submodelService = getSubmodelServiceOrThrow(submodelId);

		submodelService.createSubmodelElement(idShortPath, smElement);

		updateSubmodel(submodelId, submodelService.getSubmodel());
	}

	@Override
	public void updateSubmodelElement(String submodelId, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {

		SubmodelService submodelService = getSubmodelServiceOrThrow(submodelId);

		SubmodelElement element = submodelService.getSubmodelElement(idShortPath);

		throwIfMismatchingIds(element.getIdShort(), submodelElement.getIdShort());

		if (isFileSubmodelElement(element) && !isFileSubmodelElement(submodelElement)) {

			try {
				deleteFileValue(submodelId, idShortPath);
			} catch (FileDoesNotExistException e) {
				logger.info("The Submodel Element with idShortPath '{}' is a File Submodel Element but there is no file attachment associated with this.", idShortPath);
			}

		}

		submodelService.updateSubmodelElement(idShortPath, submodelElement);

		updateSubmodel(submodelId, submodelService.getSubmodel());
	}

	@Override
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
		SubmodelService submodelService = getSubmodelServiceOrThrow(submodelId);

		deleteAssociatedFile(submodelId, idShortPath);

		submodelService.deleteSubmodelElement(idShortPath);

		updateSubmodel(submodelId, submodelService.getSubmodel());
	}

	@Override
	public OperationVariable[] invokeOperation(String submodelId, String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
		return getSubmodelServiceOrThrow(submodelId).invokeOperation(idShortPath, input);
	}

	@Override
	public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) throws ElementDoesNotExistException {
		return new SubmodelValueOnly(getSubmodelElements(submodelId, NO_LIMIT_PAGINATION_INFO).getResult());
	}

	@Override
	public Submodel getSubmodelByIdMetadata(String submodelId) throws ElementDoesNotExistException {

		Submodel submodel = getSubmodel(submodelId);

		return getSubmodelDeepCopy(submodel);
	}

	@Override
	public java.io.File getFileByPathSubmodel(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {

		SubmodelElement submodelElement = getSubmodelElement(submodelId, idShortPath);

		throwIfSmElementIsNotAFile(submodelElement);

		File fileSmElement = (File) submodelElement;
		String filePath = getFilePath(fileSmElement);

		InputStream fileContent = getFileInputStream(filePath);

		return createFile(filePath, fileContent);
	}

	@Override
	public void setFileValue(String submodelId, String idShortPath, String fileName, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
		SubmodelElement submodelElement = getSubmodelElement(submodelId, idShortPath);

		throwIfSmElementIsNotAFile(submodelElement);

		File fileSmElement = (File) submodelElement;

		if (fileHandlingBackend.exists(fileSmElement.getValue()))
			fileHandlingBackend.delete(fileSmElement.getValue());

		String uniqueFileName = createUniqueFileName(submodelId, idShortPath, fileName);

		FileMetadata fileMetadata = new FileMetadata(uniqueFileName, fileSmElement.getContentType(), inputStream);

		String filePath = fileHandlingBackend.save(fileMetadata);

		FileBlobValue fileValue = new FileBlobValue(fileSmElement.getContentType(), filePath);

		setSubmodelElementValue(submodelId, idShortPath, fileValue);
	}

	@Override
	public void deleteFileValue(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		SubmodelElement submodelElement = getSubmodelElement(submodelId, idShortPath);

		throwIfSmElementIsNotAFile(submodelElement);

		File fileSubmodelElement = (File) submodelElement;
		String filePath = fileSubmodelElement.getValue();

		fileHandlingBackend.delete(filePath);

		FileBlobValue fileValue = new FileBlobValue(" ", " ");

		setSubmodelElementValue(submodelId, idShortPath, fileValue);
	}
	
	private void deleteAssociatedFile(String submodelId, String idShortPath) {
		try {
			deleteFileValue(submodelId, idShortPath);
		} catch (Exception e) {
			return;
		}
	}

	private boolean isFileSubmodelElement(SubmodelElement submodelElement) {
		return submodelElement instanceof File;
	}

	private InputStream getFileInputStream(String filePath) {
		InputStream fileContent;

		try {
			fileContent = fileHandlingBackend.find(filePath);
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

	private void createOutputStream(String filePath, byte[] content) throws IOException {

		try (OutputStream outputStream = new FileOutputStream(filePath)) {
			outputStream.write(content);
		} catch (IOException e) {
			throw new FileHandlingException("Exception occurred while creating OutputStream from byte[]." + e.getMessage());
		}

	}

	private void initializeRemoteCollection(Collection<Submodel> submodels) {
		if (submodels == null || submodels.isEmpty())
			return;

		submodels.stream().forEach(this::createSubmodel);
	}

	private void throwIfHasCollidingIds(Collection<Submodel> submodelsToCheck) {
		Set<String> ids = new HashSet<>();

		submodelsToCheck.stream().map(Submodel::getId).filter(id -> !ids.add(id)).findAny().ifPresent(id -> {
			throw new CollidingIdentifierException(id);
		});
	}

	private void throwIfMissingId(Collection<Submodel> submodels) {
		submodels.stream().map(Submodel::getId).forEach(this::throwIfSubmodelIdEmptyOrNull);
	}

	private Submodel getSubmodelDeepCopy(Submodel submodel) {

		try {
			String submodelAsJSON = new JsonSerializer().write(submodel);

			Submodel submodelDeepCopy = new JsonDeserializer().read(submodelAsJSON, Submodel.class);

			submodelDeepCopy.setSubmodelElements(null);

			return submodelDeepCopy;
		} catch (DeserializationException e) {
			throw new RuntimeException("Unable to deserialize the Submodel", e);
		} catch (SerializationException e) {
			throw new RuntimeException("Unable to serialize the Submodel", e);
		}
	}

	private void throwIfSmElementIsNotAFile(SubmodelElement submodelElement) {

		if (!isFileSubmodelElement(submodelElement))
			throw new ElementNotAFileException(submodelElement.getIdShort());
	}

	private String getFilePath(File fileSubmodelElement) {
		return fileSubmodelElement.getValue();
	}

	private String createUniqueFileName(String submodelId, String idShortPath, String fileName) {
		return Base64UrlEncodedIdentifier.encodeIdentifier(submodelId) + "-" + idShortPath.replace("/", "-") + "-" + fileName;
	}

	private SubmodelService getSubmodelServiceOrThrow(String submodelId) {
		Submodel submodel = submodelBackend.findById(submodelId).orElseThrow(() -> new ElementDoesNotExistException(submodelId));

		return submodelServiceFactory.create(submodel);
	}

	private void throwIfMismatchingIds(String existingId, String idToBeUpdated) {

		if (!existingId.equals(idToBeUpdated))
			throw new IdentificationMismatchException();
	}

	private void throwIfSubmodelExists(String submodelId) {

		if (submodelBackend.existsById(submodelId))
			throw new CollidingIdentifierException(submodelId);
	}

	private void throwIfSubmodelIdEmptyOrNull(String submodelId) {

		if (submodelId == null || submodelId.isBlank())
			throw new MissingIdentifierException(submodelId);
	}

	private void throwIfSubmodelDoesNotExist(String submodelId) {

		if (!submodelBackend.existsById(submodelId))
			throw new ElementDoesNotExistException(submodelId);
	}

}
