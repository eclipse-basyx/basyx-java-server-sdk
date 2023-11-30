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
package org.eclipse.digitaltwin.basyx.aasrepository.backend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.io.IOUtils;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Resource;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultResource;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceFactory;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.CrudRepository;



/**
 * Default Implementation for the {@link AasRepository} based on Spring
 * {@link CrudRepository}
 * 
 * @author zhangzai
 *
 */
public class CrudAasRepository implements AasRepository {

	private CrudRepository<AssetAdministrationShell, String> aasBackend;

	private AasServiceFactory aasServiceFactory;

	private String aasRepositoryName = null;
	
	private String tmpDirectory = getTemporaryDirectoryPath();

	public CrudAasRepository(AasBackendProvider aasBackendProvider, AasServiceFactory aasServiceFactory) {
		this.aasBackend = aasBackendProvider.getCrudRepository();
		this.aasServiceFactory = aasServiceFactory;
	}

	public CrudAasRepository(AasBackendProvider aasBackendProvider, AasServiceFactory aasServiceFactory, @Value("${basyx.aasrepo.name:aas-repo}") String aasRepositoryName) {
		this(aasBackendProvider, aasServiceFactory);
		
		this.aasRepositoryName = aasRepositoryName;
	}

	@Override
	public CursorResult<List<AssetAdministrationShell>> getAllAas(PaginationInfo pInfo) {

		Iterable<AssetAdministrationShell> iterable = aasBackend.findAll();
		List<AssetAdministrationShell> allAas = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());

		TreeMap<String, AssetAdministrationShell> aasMap = allAas.stream().collect(Collectors.toMap(AssetAdministrationShell::getId, aas -> aas, (a, b) -> a, TreeMap::new));

		PaginationSupport<AssetAdministrationShell> paginationSupport = new PaginationSupport<>(aasMap, AssetAdministrationShell::getId);

		return paginationSupport.getPaged(pInfo);
	}

	@Override
	public AssetAdministrationShell getAas(String aasId) throws ElementDoesNotExistException {
		return aasBackend.findById(aasId).orElseThrow(() -> new ElementDoesNotExistException(aasId));
	}

	@Override
	public void createAas(AssetAdministrationShell aas) throws CollidingIdentifierException {
		throwIfAasExists(aas);

		aasBackend.save(aas);
	}

	@Override
	public void deleteAas(String aasId) {
		throwIfAasDoesNotExist(aasId);

		aasBackend.deleteById(aasId);
	}

	@Override
	public void updateAas(String aasId, AssetAdministrationShell aas) {
		throwIfAasDoesNotExist(aasId);

		throwIfMismatchingIds(aasId, aas);

		aasBackend.save(aas);
	}

	@Override
	public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
		return getAasServiceOrThrow(aasId).getSubmodelReferences(pInfo);
	}

	@Override
	public void addSubmodelReference(String aasId, Reference submodelReference) {
		AasService aasService = getAasServiceOrThrow(aasId);

		aasService.addSubmodelReference(submodelReference);

		updateAas(aasId, aasService.getAAS());
	}

	@Override
	public void removeSubmodelReference(String aasId, String submodelId) {
		AasService aasService = getAasServiceOrThrow(aasId);

		aasService.removeSubmodelReference(submodelId);

		updateAas(aasId, aasService.getAAS());
	}

	@Override
	public void setAssetInformation(String aasId, AssetInformation aasInfo) throws ElementDoesNotExistException {
		AasService aasService = getAasServiceOrThrow(aasId);

		aasService.setAssetInformation(aasInfo);

		updateAas(aasId, aasService.getAAS());
	}

	@Override
	public AssetInformation getAssetInformation(String aasId) throws ElementDoesNotExistException {
		return getAasServiceOrThrow(aasId).getAssetInformation();
	}

	private AasService getAasServiceOrThrow(String aasId) {
		AssetAdministrationShell aas = aasBackend.findById(aasId).orElseThrow(() -> new ElementDoesNotExistException(aasId));

		return aasServiceFactory.create(aas);
	}

	private void throwIfMismatchingIds(String aasId, AssetAdministrationShell newAas) {
		String newAasId = newAas.getId();

		if (!aasId.equals(newAasId))
			throw new IdentificationMismatchException();
	}

	private void throwIfAasExists(AssetAdministrationShell aas) {
		if (aasBackend.existsById(aas.getId()))
			throw new CollidingIdentifierException();
	}

	private void throwIfAasDoesNotExist(String aasId) {
		if (!aasBackend.existsById(aasId))
			throw new ElementDoesNotExistException(aasId);
	}

	@Override
	public String getName() {
		return aasRepositoryName == null ? AasRepository.super.getName() : aasRepositoryName;
	}

	@Override
	public File getThumbnail(String aasId) {
		Resource resource = getAssetInformation(aasId).getDefaultThumbnail();

		throwIfFileDoesNotExist(aasId, resource);
		String filePath = resource.getPath();
		return new File(filePath);
	}

	@Override
	public void setThumbnail(String aasId, String fileName, String contentType, InputStream inputStream) {
		Resource thumbnail = getAssetInformation(aasId).getDefaultThumbnail();
		if (thumbnail != null) {
			String path = thumbnail.getPath();

			deleteExistingFile(path);
		}

		String filePath = createFilePath(aasId, fileName);

		createFileAtSpecifiedPath(fileName, inputStream, filePath);

		if (thumbnail != null) {
			updateThumbnail(aasId, contentType, filePath);

			return;
		}

		setNewThumbnail(aasId, contentType, filePath);
	}

	private void updateThumbnail(String aasId, String contentType, String filePath) {
		AssetInformation assetInfor = getAssetInformation(aasId);
		assetInfor.getDefaultThumbnail().setContentType(contentType);
		assetInfor.getDefaultThumbnail().setPath(filePath);
		setAssetInformation(aasId, assetInfor);
	}

	private void setNewThumbnail(String aasId, String contentType, String filePath) {
		Resource resource = new DefaultResource();
		resource.setContentType(contentType);
		resource.setPath(filePath);
		AssetInformation assetInfor = getAssetInformation(aasId);
		assetInfor.setDefaultThumbnail(resource);
		setAssetInformation(aasId, assetInfor);
	}

	@Override
	public void deleteThumbnail(String aasId) {
		Resource thumbnail = getAssetInformation(aasId).getDefaultThumbnail();

		throwIfFileDoesNotExist(aasId, thumbnail);

		String filePath = thumbnail.getPath();
		java.io.File tmpFile = new java.io.File(filePath);
		tmpFile.delete();

		AssetInformation assetInfor = getAssetInformation(aasId);
		assetInfor.getDefaultThumbnail().setContentType("");
		assetInfor.getDefaultThumbnail().setPath("");
		setAssetInformation(aasId, assetInfor);

	}

	private void throwIfFileDoesNotExist(String aasId, Resource resource) {
		if (resource == null)
			throw new FileDoesNotExistException(aasId);

		String filePath = resource.getPath();
		if (!isFilePathValid(filePath))
			throw new FileDoesNotExistException(aasId);
	}

	private boolean isFilePathValid(String filePath) {
		if (filePath.isEmpty())
			return false;
		try {
			Paths.get(filePath);
		} catch (InvalidPathException | NullPointerException ex) {
			return false;
		}
		return true;
	}

	private String createFilePath(String aasId, String fileName) {
		return tmpDirectory + "/" + aasId + "-" + "Thumbnail" + "-" + fileName;
	}

	private void createFileAtSpecifiedPath(String fileName, InputStream inputStream, String filePath) {
		java.io.File targetFile = new java.io.File(filePath);

		try (FileOutputStream outStream = new FileOutputStream(targetFile)) {
			IOUtils.copy(inputStream, outStream);
		} catch (IOException e) {
			throw new FileHandlingException(fileName);
		}
	}

	private void deleteExistingFile(String path) {
		if (path == null || path.isEmpty())
			return;

		try {
			Files.deleteIfExists(Paths.get(path, ""));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getTemporaryDirectoryPath() {
		String tempDirectoryPath = "";
		try {
			tempDirectoryPath = Files.createTempDirectory("basyx-temp-thumbnail").toAbsolutePath().toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempDirectoryPath;
	}

}
