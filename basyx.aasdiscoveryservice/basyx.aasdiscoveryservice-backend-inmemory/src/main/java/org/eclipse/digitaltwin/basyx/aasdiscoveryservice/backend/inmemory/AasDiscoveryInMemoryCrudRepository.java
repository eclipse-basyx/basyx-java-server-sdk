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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.inmemory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.StreamSupport;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AasDiscoveryDocument;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

/**
 * In-memory implementation of the {@link CrudRepository} for the AAS Discovery
 * 
 * @author zielstor, fried, mateusmolina
 */
public class AasDiscoveryInMemoryCrudRepository implements CrudRepository<AasDiscoveryDocument, String> {

	private final ConcurrentMap<String, Set<AssetLink>> assetLinks = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, List<SpecificAssetId>> assetIds = new ConcurrentHashMap<>();

	@Override
	public synchronized @NonNull <S extends AasDiscoveryDocument> S save(@NonNull S entity) {
		String shellId = entity.getShellIdentifier();

		this.assetLinks.put(shellId, entity.getAssetLinks());
		this.assetIds.put(shellId, entity.getSpecificAssetIds());

		return entity;
	}

	@Override
	public @NonNull <S extends AasDiscoveryDocument> Iterable<S> saveAll(@NonNull Iterable<S> entities) {
		entities.forEach(this::save);
		return entities;
	}

	@Override
	public @NonNull Optional<AasDiscoveryDocument> findById(@NonNull String id) {
		return Optional.ofNullable(buildAasDiscoveryDocument(id));
	}

	@Override
	public boolean existsById(@NonNull String id) {
		return this.assetLinks.containsKey(id);
	}

	@Override
	public @NonNull Iterable<AasDiscoveryDocument> findAll() {
		return assetLinks.keySet().stream().map(this::buildAasDiscoveryDocument).toList();
	}

	@Override
	public @NonNull Iterable<AasDiscoveryDocument> findAllById(@NonNull Iterable<String> ids) {
		return StreamSupport.stream(ids.spliterator(), false).map(this::buildAasDiscoveryDocument).toList();
	}

	@Override
	public long count() {
		return this.assetLinks.size();
	}

	@Override
	public synchronized void deleteById(@NonNull String id) {
		this.assetLinks.remove(id);
		this.assetIds.remove(id);
	}

	@Override
	public void delete(@NonNull AasDiscoveryDocument entity) {
		this.deleteById(entity.getShellIdentifier());
	}

	@Override
	public void deleteAllById(@NonNull Iterable<? extends String> ids) {
		for (String id : ids) {
			this.deleteById(id);
		}
	}

	@Override
	public void deleteAll(@NonNull Iterable<? extends AasDiscoveryDocument> entities) {
		for (AasDiscoveryDocument entity : entities) {
			this.deleteById(entity.getShellIdentifier());
		}
	}

	@Override
	public synchronized void deleteAll() {
		this.assetLinks.clear();
		this.assetIds.clear();
	}

	private synchronized AasDiscoveryDocument buildAasDiscoveryDocument(String shellId) {
		Set<AssetLink> assetLinksSet = assetLinks.get(shellId);
		List<SpecificAssetId> assetIdsList = assetIds.get(shellId);

		if (assetIdsList == null)
			assetIdsList = new ArrayList<>();

		if (assetLinksSet == null)
			assetLinksSet = new HashSet<>();

		return new AasDiscoveryDocument(shellId, assetLinksSet, assetIdsList);
	}

}
