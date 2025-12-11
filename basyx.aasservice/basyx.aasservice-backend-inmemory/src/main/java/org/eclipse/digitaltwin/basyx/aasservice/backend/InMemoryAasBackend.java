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
package org.eclipse.digitaltwin.basyx.aasservice.backend;

import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.basyx.common.backend.inmemory.core.InMemoryCrudRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingSubmodelReferenceException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingKeyTypeException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Implements the AasService as in-memory variant
 * 
 * @author schnicke, mateusmolina
 * 
 */
@ConditionalOnExpression("'${basyx.aasservice.backend}'.equals('InMemory') or '${basyx.backend}'.equals('InMemory')")
@Component
public class InMemoryAasBackend extends InMemoryCrudRepository<AssetAdministrationShell> implements AasBackend {

	public InMemoryAasBackend() {
		super(AssetAdministrationShell::getId);
	}

	@Override
	public CursorResult<List<AssetAdministrationShell>> getShells(List<SpecificAssetId> assetIds, String idShort, PaginationInfo pInfo) {
		Iterable<AssetAdministrationShell> iterable = getAllAas(assetIds, idShort);
		List<AssetAdministrationShell> allAas = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());

		TreeMap<String, AssetAdministrationShell> aasMap = allAas.stream().collect(Collectors.toMap(AssetAdministrationShell::getId, aas -> aas, (a, b) -> a, TreeMap::new));

		PaginationSupport<AssetAdministrationShell> paginationSupport = new PaginationSupport<>(aasMap, AssetAdministrationShell::getId);

		return paginationSupport.getPaged(pInfo);
	}

	@Override
	public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
		List<Reference> submodelReferences = getAas(aasId).getSubmodels();

		Function<Reference, String> idResolver = extractSubmodelID();

		TreeMap<String, Reference> submodelRefMap = convertToTreeMap(submodelReferences, idResolver);

		PaginationSupport<Reference> paginationSupport = new PaginationSupport<>(submodelRefMap, idResolver);

        return paginationSupport.getPaged(pInfo);
	}


	@Override
	public void addSubmodelReference(String aasId, Reference submodelReference) {
		AssetAdministrationShell aas = getAas(aasId);
		List<Reference> submodelsRefs = aas.getSubmodels();
		synchronized (submodelsRefs) {
			throwExceptionIfReferenceIsAlreadyPresent(aas, submodelReference);
			submodelsRefs.add(submodelReference);
		}
	}

	@Override
	public void removeSubmodelReference(String aasId, String submodelId) {
		AssetAdministrationShell aas = getAas(aasId);
		List<Reference> submodelsRefs = aas.getSubmodels();
		synchronized (submodelsRefs) {
			submodelsRefs.remove(getSubmodelReferenceById(aas, submodelId));
		}
	}

	@Override
	public void setAssetInformation(String aasId, AssetInformation aasInfo) {
		getAas(aasId).setAssetInformation(aasInfo);
	}
	
	@Override
	public AssetInformation getAssetInformation(String aasId) {
		return getAas(aasId).getAssetInformation();
	}

	@Override
	public Iterable<AssetAdministrationShell> getAllAas(List<SpecificAssetId> assetIds, String idShort) {
		Iterable<AssetAdministrationShell> allAas = findAll();
		List<AssetAdministrationShell> filteredAas = new java.util.ArrayList<>();
		List<SpecificAssetId> globalAssetIds = new ArrayList<>();
		try {
			globalAssetIds = assetIds.stream().filter(assetId -> assetId.getName().equals("globalAssetId")).toList();
			assetIds = assetIds.stream().filter(assetId -> !assetId.getName().equals("globalAssetId")).collect(Collectors.toList());
		} catch (Exception e) {}
		for (AssetAdministrationShell aas : allAas){
			boolean matchesAssetIds;
			try {
				matchesAssetIds = (assetIds == null || assetIds.stream().allMatch(assetId -> aas.getAssetInformation().getSpecificAssetIds().contains(assetId)));
			}catch (NullPointerException e) {
				// If AssetInformation is null, we cannot match specific asset IDs
				matchesAssetIds = false;
			}
			boolean matchesIdShort = (idShort == null || aas.getIdShort().equals(idShort));
			boolean matchesGlobalAssetId = globalAssetIds.isEmpty();
			for (SpecificAssetId globalAssetId : globalAssetIds){
				String id = globalAssetId.getValue();
				if (aas.getAssetInformation() == null || aas.getAssetInformation().getGlobalAssetId() == null || !aas.getAssetInformation().getGlobalAssetId().equals(id)) {
					matchesGlobalAssetId = false;
					break;
				}
				matchesGlobalAssetId = true;
			}
			if (matchesAssetIds && matchesIdShort && matchesGlobalAssetId) {
				filteredAas.add(aas);
			}
		}
		return filteredAas;
	}

	private static Reference getSubmodelReferenceById(AssetAdministrationShell aas, String submodelId) {
		List<Reference> submodelReferences = aas.getSubmodels();

		return submodelReferences.stream().filter(reference -> {
			List<Key> keys = reference.getKeys();
			Key foundKey = keys.stream().filter(key -> key.getType().equals(KeyTypes.SUBMODEL)).findFirst().get();
			return foundKey.getValue().equals(submodelId);
		}).findFirst().orElseThrow(() -> new ElementDoesNotExistException(submodelId));
	}

	private static TreeMap<String, Reference> convertToTreeMap(List<Reference> submodelReferences,
			Function<Reference, String> idResolver) {
		return submodelReferences.stream().collect(Collectors
				.toMap(idResolver, ref -> ref, (ref1, ref2) -> ref1, TreeMap::new));
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

	private static void throwExceptionIfReferenceIsAlreadyPresent(AssetAdministrationShell aas, Reference submodelReference) {
		Optional<Key> submodelIdKey = getSubmodelTypeKey(submodelReference);
		if(submodelIdKey.isEmpty())
			return;
		String submodelId = submodelIdKey.get().getValue();
		if (isSubmodelIdAlreadyReferenced(aas, submodelId)) {
			throw new CollidingSubmodelReferenceException(submodelId);
		}
	}

	private static boolean isSubmodelIdAlreadyReferenced(AssetAdministrationShell aas, String submodelId) {
		return aas.getSubmodels().stream().anyMatch(ref -> ref.getKeys().stream().anyMatch(key -> key.getValue().equals(submodelId)));
	}

	private static Optional<Key> getSubmodelTypeKey(Reference submodelReference) {
		return submodelReference.getKeys().stream().filter(key -> {
			KeyTypes type = key.getType();
			if(type == null)
				throw new MissingKeyTypeException();
			return type.equals(KeyTypes.SUBMODEL);
		}).findFirst();
	}

	private AssetAdministrationShell getAas(String aasId) {
		return findById(aasId).orElseThrow(() -> new ElementDoesNotExistException(aasId));
	}

}
