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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.inmemory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AssetLinksWithShellIdentifier;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.springframework.data.repository.CrudRepository;

/**
 * InMemory implementation for the AAS backend
 * 
 * @author mateusmolina
 * 
 */
public class AasDiscoveryInMemoryBackendAssetLink implements CrudRepository<AssetLinksWithShellIdentifier, String> {

	private final Map<String, Set<AssetLink>> assetLinks = new LinkedHashMap<>();

	@Override
	public <S extends AssetLinksWithShellIdentifier> S save(S entity) {
		assetLinks.put(entity.getShellId(), entity.getAssetLinks());
		return entity;
	}

	@Override
	public <S extends AssetLinksWithShellIdentifier> Iterable<S> saveAll(Iterable<S> entities) {
		for (S entity : entities)
			assetLinks.put(entity.getShellId(), entity.getAssetLinks());

		return entities;
	}

	@Override
	public Optional<AssetLinksWithShellIdentifier> findById(String id) {
		return assetLinks.entrySet().stream().filter(entry -> entry.getKey().equals(id))
				.map(entry -> new AssetLinksWithShellIdentifier(entry.getKey(), entry.getValue())).findFirst();
	}

	@Override
	public boolean existsById(String id) {
		return assetLinks.containsKey(id);
	}

	@Override
	public Iterable<AssetLinksWithShellIdentifier> findAll() {
		return assetLinks.entrySet().stream()
				.map(entry -> new AssetLinksWithShellIdentifier(entry.getKey(), entry.getValue())).toList();
	}

	@Override
	public Iterable<AssetLinksWithShellIdentifier> findAllById(Iterable<String> ids) {
		List<String> idList = StreamSupport.stream(ids.spliterator(), false).collect(Collectors.toList());
		return assetLinks.entrySet().stream().filter(entry -> idList.contains(entry.getKey()))
				.map(entry -> new AssetLinksWithShellIdentifier(entry.getKey(), entry.getValue())).toList();
	}

	@Override
	public long count() {
		return assetLinks.size();
	}

	@Override
	public void deleteById(String id) {
		assetLinks.remove(id);
	}

	@Override
	public void delete(AssetLinksWithShellIdentifier entity) {
		assetLinks.remove(entity.getShellId());
	}

	@Override
	public void deleteAllById(Iterable<? extends String> ids) {
		for (String id : ids)
			assetLinks.remove(id);
	}

	@Override
	public void deleteAll(Iterable<? extends AssetLinksWithShellIdentifier> entities) {
		for (AssetLinksWithShellIdentifier entity : entities)
			assetLinks.remove(entity.getShellId());
	}

	@Override
	public void deleteAll() {
		assetLinks.clear();
	}

}

