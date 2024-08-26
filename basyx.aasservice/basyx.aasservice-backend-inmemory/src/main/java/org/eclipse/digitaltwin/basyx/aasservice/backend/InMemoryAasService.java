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
package org.eclipse.digitaltwin.basyx.aasservice.backend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Resource;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultResource;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.core.exceptions.*;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileMetadata;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;

/**
 * Implements the AasService as in-memory variant
 * 
 * @author schnicke, mateusmolina
 * 
 */
public class InMemoryAasService implements AasService {
	private AssetAdministrationShell aas;

	private final FileRepository fileRepository;

	/**
	 * Creates the InMemory AasService containing the passed AAS
	 * 
	 * @param aas
	 */
	public InMemoryAasService(AssetAdministrationShell aas, FileRepository fileRepository) {
		this.aas = aas;
		this.fileRepository = fileRepository;
	}

	@Override
	public AssetAdministrationShell getAAS() {
		return aas;
	}

	@Override
	public CursorResult<List<Reference>> getSubmodelReferences(PaginationInfo pInfo) {
		List<Reference> submodelReferences = aas.getSubmodels();

		Function<Reference, String> idResolver = extractSubmodelID();

		TreeMap<String, Reference> submodelRefMap = convertToTreeMap(submodelReferences, idResolver);

		PaginationSupport<Reference> paginationSupport = new PaginationSupport<>(submodelRefMap, idResolver);
		CursorResult<List<Reference>> paginatedSubmodelReference = paginationSupport.getPaged(pInfo);

		return paginatedSubmodelReference;
	}


	@Override
	public void addSubmodelReference(Reference submodelReference) {
		throwExceptionIfReferenceIsAlreadyPresent(submodelReference);

		aas.getSubmodels().add(submodelReference);
	}

	@Override
	public void removeSubmodelReference(String submodelId) {
		Reference specificSubmodelReference = getSubmodelReferenceById(submodelId);

		aas.getSubmodels().remove(specificSubmodelReference);
	}

	@Override
	public void setAssetInformation(AssetInformation aasInfo) {
		aas.setAssetInformation(aasInfo);		
	}
	
	@Override
	public AssetInformation getAssetInformation() {		
		return aas.getAssetInformation();
	}

	private Reference getSubmodelReferenceById(String submodelId) {
		List<Reference> submodelReferences = aas.getSubmodels();

		Reference specificSubmodelReference = submodelReferences.stream().filter(reference -> {
			List<Key> keys = reference.getKeys();
			Key foundKey = keys.stream().filter(key -> key.getType().equals(KeyTypes.SUBMODEL)).findFirst().get();
			return foundKey.getValue().equals(submodelId);
		}).findFirst().orElseThrow(() -> new ElementDoesNotExistException(submodelId));

		return specificSubmodelReference;
	}

	private TreeMap<String, Reference> convertToTreeMap(List<Reference> submodelReferences,
			Function<Reference, String> idResolver) {
		return submodelReferences.stream().collect(Collectors
				.toMap(reference -> idResolver.apply(reference), ref -> ref, (ref1, ref2) -> ref1, TreeMap::new));
	}

	private Function<Reference, String> extractSubmodelID() {
		return reference -> {
			List<Key> keys = reference.getKeys();
			for (Key key : keys) {
				if (key.getType() == KeyTypes.SUBMODEL) {
					return key.getValue();
				}
			}
			return ""; // Return an empty string if no ID is found
		};
	}

	@Override
	public File getThumbnail() {
		Resource resource = getAssetInformation().getDefaultThumbnail();

		try {
			return getResourceContent(resource);
		} catch (NullPointerException e) {
			throw new FileDoesNotExistException();
		} catch (IOException e) {
			throw new FileHandlingException("Exception occurred while creating file from the InputStream." + e.getMessage());
		}
	}

	@Override
	public void setThumbnail(String fileName, String contentType, InputStream inputStream) {
		FileMetadata thumbnailMetadata = new FileMetadata(fileName, contentType, inputStream);

		if(fileRepository.exists(thumbnailMetadata.getFileName()))
			fileRepository.delete(thumbnailMetadata.getFileName());
		
		String filePath = fileRepository.save(thumbnailMetadata);

		setAssetInformation(configureAssetInformationThumbnail(getAssetInformation(), contentType, filePath));
	}

	@Override
	public void deleteThumbnail() {
		try {
			String thumbnailPath = getAssetInformation().getDefaultThumbnail().getPath();
			fileRepository.delete(thumbnailPath);
		} catch (NullPointerException e) {
			throw new FileDoesNotExistException();
		} finally {
			setAssetInformation(configureAssetInformationThumbnail(getAssetInformation(), "", ""));
		}

	}

	private void createOutputStream(String filePath, byte[] content) throws IOException {

		try (OutputStream outputStream = new FileOutputStream(filePath)) {
			outputStream.write(content);
		} catch (IOException e) {
			throw new FileHandlingException("Exception occurred while creating OutputStream from byte[]." + e.getMessage());
		}

	}

	private static AssetInformation configureAssetInformationThumbnail(AssetInformation assetInformation, String contentType, String filePath) {
		Resource resource = new DefaultResource();
		resource.setContentType(contentType);
		resource.setPath(filePath);
		assetInformation.setDefaultThumbnail(resource);
		return assetInformation;
	}

	private File getResourceContent(Resource resource) throws IOException {
		String filePath = resource.getPath();

		InputStream fileIs = fileRepository.find(filePath);
		byte[] content = fileIs.readAllBytes();
		fileIs.close();

		createOutputStream(filePath, content);

		return new java.io.File(filePath);
	}

	private void throwExceptionIfReferenceIsAlreadyPresent(Reference submodelReference) {
		Optional<Key> submodelIdKey = getSubmodelTypeKey(submodelReference);
		if(submodelIdKey.isEmpty())
			return;
		String submodelId = submodelIdKey.get().getValue();
		if (isSubmodelIdAlreadyReferenced(submodelId)) {
			throw new CollidingSubmodelReferenceException(submodelId);
		}
	}

	private boolean isSubmodelIdAlreadyReferenced(String submodelId) {
		return aas.getSubmodels().stream().anyMatch(ref -> ref.getKeys().stream().anyMatch(key -> key.getValue().equals(submodelId)));
	}

	private static Optional<Key> getSubmodelTypeKey(Reference submodelReference) {
		Optional<Key> submodelIdKey = submodelReference.getKeys().stream().filter(key -> {
			KeyTypes type = key.getType();
			if(type == null)
				throw new MissingKeyTypeException();
			return type.equals(KeyTypes.SUBMODEL);
		}).findFirst();
		return submodelIdKey;
	}

}
