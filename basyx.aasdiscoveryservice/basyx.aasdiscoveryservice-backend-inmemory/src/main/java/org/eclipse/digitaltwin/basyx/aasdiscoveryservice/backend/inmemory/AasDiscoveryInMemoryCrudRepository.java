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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AasDiscoveryDocument;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.springframework.data.repository.CrudRepository;

/**
 * In-memory implementation of the {@link CrudRepository} for the AAS Discovery
 * 
 * @author zielstor, fried
 */
public class AasDiscoveryInMemoryCrudRepository implements CrudRepository<AasDiscoveryDocument, String> {

	private final Map<String, Set<AssetLink>> assetLinks = new LinkedHashMap<>();
	private final Map<String, List<SpecificAssetId>> assetIds = new LinkedHashMap<>();

	@Override
	public <S extends AasDiscoveryDocument> S save(S entity) {
		Set<AssetLink> assetLinks = entity.getAssetLinks();
		List<SpecificAssetId> assetIds = entity.getSpecificAssetIds();
		String shellId = entity.getShellIdentifier();

		this.assetLinks.put(shellId, assetLinks);
		this.assetIds.put(shellId, assetIds);
		return entity;
	}

	@Override
	public <S extends AasDiscoveryDocument> Iterable<S> saveAll(Iterable<S> entities) {
		for (S entity : entities) {
			this.save(entity);
		}
		return entities;
	}

	@Override
	public Optional<AasDiscoveryDocument> findById(String id) {
		Set<AssetLink> assetLinks = this.assetLinks.get(id);
		List<SpecificAssetId> assetIds = this.assetIds.get(id);
		if (assetIds == null) {
			assetIds = new ArrayList<>();
		}

		if (assetLinks == null) {
			assetLinks = new HashSet<>();
		}
		return Optional.of(new AasDiscoveryDocument(id, assetLinks, assetIds));
	}

	@Override
	public boolean existsById(String id) {
		return this.assetLinks.containsKey(id);
	}

	@Override
	public Iterable<AasDiscoveryDocument> findAll() {
		List<AasDiscoveryDocument> result = new ArrayList<>();
		for (String shellId : this.assetLinks.keySet()) {
			Set<AssetLink> assetLinks = this.assetLinks.get(shellId);
			List<SpecificAssetId> assetIds = this.assetIds.get(shellId);
			if (assetIds == null) {
				assetIds = new ArrayList<>();
			}

			if (assetLinks == null) {
				assetLinks = new HashSet<>();
			}
			result.add(new AasDiscoveryDocument(shellId, assetLinks, assetIds));
		}
		return result;
	}

	@Override
	public Iterable<AasDiscoveryDocument> findAllById(Iterable<String> ids) {
		List<AasDiscoveryDocument> result = new ArrayList<>();
		for (String id : ids) {
			Set<AssetLink> assetLinks = this.assetLinks.get(id);
			List<SpecificAssetId> assetIds = this.assetIds.get(id);
			if (assetIds == null) {
				assetIds = new ArrayList<>();
			}

			if (assetLinks == null) {
				assetLinks = new HashSet<>();
			}
			result.add(new AasDiscoveryDocument(id, assetLinks, assetIds));
		}
		return result;
	}

	@Override
	public long count() {
		return this.assetLinks.size();
	}

	@Override
	public void deleteById(String id) {
		this.assetLinks.remove(id);
		this.assetIds.remove(id);
	}

	@Override
	public void delete(AasDiscoveryDocument entity) {
		this.deleteById(entity.getShellIdentifier());
	}

	@Override
	public void deleteAllById(Iterable<? extends String> ids) {
		for (String id : ids) {
			this.deleteById(id);
		}
	}

	@Override
	public void deleteAll(Iterable<? extends AasDiscoveryDocument> entities) {
		for (AasDiscoveryDocument entity : entities) {
			this.deleteById(entity.getShellIdentifier());
		}
	}

	@Override
	public void deleteAll() {
		this.assetLinks.clear();
		this.assetIds.clear();
	}

}
