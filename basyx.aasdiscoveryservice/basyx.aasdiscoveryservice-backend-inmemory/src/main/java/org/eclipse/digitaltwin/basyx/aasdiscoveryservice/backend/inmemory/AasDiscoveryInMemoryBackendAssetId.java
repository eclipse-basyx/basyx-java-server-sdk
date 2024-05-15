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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AssetIdsWithShellIdentifier;
import org.springframework.data.repository.CrudRepository;

public class AasDiscoveryInMemoryBackendAssetId implements CrudRepository<AssetIdsWithShellIdentifier, String> {
	private final Map<String, List<SpecificAssetId>> assetIds = new LinkedHashMap<>();

	@Override
	public <S extends AssetIdsWithShellIdentifier> S save(S entity) {
		assetIds.put(entity.getShellId(), entity.getAssetIds());
		return entity;
	}

	@Override
	public <S extends AssetIdsWithShellIdentifier> Iterable<S> saveAll(Iterable<S> entities) {
		for (S entity : entities)
			assetIds.put(entity.getShellId(), entity.getAssetIds());

		return entities;
	}

	@Override
	public Optional<AssetIdsWithShellIdentifier> findById(String id) {
		return assetIds.entrySet().stream().filter(entry -> entry.getKey().equals(id))
				.map(entry -> new AssetIdsWithShellIdentifier(entry.getKey(), entry.getValue())).findFirst();
	}

	@Override
	public boolean existsById(String id) {
		return assetIds.containsKey(id);
	}

	@Override
	public Iterable<AssetIdsWithShellIdentifier> findAll() {
		return assetIds.entrySet().stream()
				.map(entry -> new AssetIdsWithShellIdentifier(entry.getKey(), entry.getValue())).toList();
	}

	@Override
	public Iterable<AssetIdsWithShellIdentifier> findAllById(Iterable<String> ids) {
		List<String> idList = StreamSupport.stream(ids.spliterator(), false).collect(Collectors.toList());
		return assetIds.entrySet().stream().filter(entry -> idList.contains(entry.getKey()))
				.map(entry -> new AssetIdsWithShellIdentifier(entry.getKey(), entry.getValue())).toList();
	}

	@Override
	public long count() {
		return assetIds.size();
	}

	@Override
	public void deleteById(String id) {
		assetIds.remove(id);
	}

	@Override
	public void delete(AssetIdsWithShellIdentifier entity) {
		assetIds.remove(entity.getShellId());
	}

	@Override
	public void deleteAllById(Iterable<? extends String> ids) {
		for (String id : ids)
			assetIds.remove(id);
	}

	@Override
	public void deleteAll(Iterable<? extends AssetIdsWithShellIdentifier> entities) {
		for (AssetIdsWithShellIdentifier entity : entities)
			assetIds.remove(entity.getShellId());
	}
	@Override
	public void deleteAll() {
		assetIds.clear();
	}


}
