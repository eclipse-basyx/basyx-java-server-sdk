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

package org.eclipse.digitaltwin.basyx.submodelrepository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.springframework.data.repository.CrudRepository;

/**
 * InMemory implementation for the Submodel backend
 * 
 * @author mateusmolina, danish
 * 
 */
public class SubmodelInMemoryBackend implements CrudRepository<Submodel, String> {

	private Map<String, Submodel> inMemoryStore = new LinkedHashMap<>();
	
	@Override
	public <S extends Submodel> S save(S entity) {
		inMemoryStore.put(entity.getId(), entity);
		
		return entity;
	}

	@Override
	public <S extends Submodel> Iterable<S> saveAll(Iterable<S> entities) {
		for (S entity : entities)
			inMemoryStore.put(entity.getId(), entity);

		return entities;
	}

	@Override
	public Optional<Submodel> findById(String id) {
		return Optional.ofNullable(inMemoryStore.get(id));
	}

	@Override
	public boolean existsById(String id) {
		return inMemoryStore.containsKey(id);
	}

	@Override
	public Iterable<Submodel> findAll() {
		return inMemoryStore.values();
	}

	@Override
	public Iterable<Submodel> findAllById(Iterable<String> ids) {
		return StreamSupport.stream(ids.spliterator(), false).map(inMemoryStore::get).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public long count() {
		return inMemoryStore.size();
	}

	@Override
	public void deleteById(String id) {
		inMemoryStore.remove(id);
	}

	@Override
	public void delete(Submodel entity) {
		inMemoryStore.remove(entity.getId());
	}

	@Override
	public void deleteAllById(Iterable<? extends String> ids) {
		for (String id : ids)
			inMemoryStore.remove(id);
	}

	@Override
	public void deleteAll(Iterable<? extends Submodel> entities) {
		for (Submodel entity : entities)
			inMemoryStore.remove(entity.getId());
	}

	@Override
	public void deleteAll() {
		inMemoryStore.clear();
	}

}

