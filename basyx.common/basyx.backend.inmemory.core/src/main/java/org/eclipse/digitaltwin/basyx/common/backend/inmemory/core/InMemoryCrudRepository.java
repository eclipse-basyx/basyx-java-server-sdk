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

package org.eclipse.digitaltwin.basyx.common.backend.inmemory.core;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.digitaltwin.basyx.core.BaSyxCrudRepository;
import org.eclipse.digitaltwin.basyx.core.FilterResolution;
import org.eclipse.digitaltwin.basyx.core.Filter;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.lang.NonNull;

/**
 * CrudRepository implementation for InMemory backends
 * 
 * @author danish
 */
public class InMemoryCrudRepository<T> implements BaSyxCrudRepository<T> {

	private final ConcurrentMap<String, T> inMemoryStore = new ConcurrentHashMap<>();
	private Function<T, String> idGetter;
	private FilterResolution<Predicate<T>> filterResolution;

	public InMemoryCrudRepository(Function<T, String> idGetter) {
		this.idGetter = idGetter;
	}
	
	public InMemoryCrudRepository(Function<T, String> idGetter, FilterResolution<Predicate<T>> filterResolution) {
		this(idGetter);
		
		this.filterResolution = filterResolution;
	}

	@Override
	public @NonNull <S extends T> S save(@NonNull S entity) {
		String id = idGetter.apply(entity);

		inMemoryStore.put(id, entity);

		return entity;
	}

	@Override
	public @NonNull <S extends T> Iterable<S> saveAll(@NonNull Iterable<S> entities) {
		entities.forEach(this::save);

		return entities;
	}

	@Override
	public @NonNull Optional<T> findById(@NonNull String id) {
		return Optional.ofNullable(inMemoryStore.get(id));
	}

	@Override
	public boolean existsById(@NonNull String id) {
		return inMemoryStore.containsKey(id);
	}

	@Override
	public @NonNull Iterable<T> findAll() {
		return inMemoryStore.values();
	}

	@Override
	public @NonNull Iterable<T> findAll(PaginationInfo paginationInfo, Filter filter) {
		
		Stream<T> resultStream = inMemoryStore.values().stream();
		
		if (filterResolution != null && filter != null)
			resultStream = resultStream.filter(filterResolution.applyFilter(filter));

		resultStream = applySorting(resultStream);

		if (paginationInfo.getCursor() != null) {
			resultStream = resultStream.filter(asset -> idGetter.apply(asset).compareTo(paginationInfo.getCursor()) > 0);
		}

		if (paginationInfo.getLimit() != null) {
			resultStream = resultStream.limit(paginationInfo.getLimit());
		}

		return resultStream.collect(Collectors.toList());
	}

	private Stream<T> applySorting(Stream<T> stream) {
		return stream
                .sorted(Comparator.comparing(idGetter));
	}

	@Override
	public @NonNull Iterable<T> findAllById(@NonNull Iterable<String> ids) {
		return StreamSupport.stream(ids.spliterator(), false).map(inMemoryStore::get).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public long count() {
		return inMemoryStore.size();
	}

	@Override
	public void deleteById(@NonNull String id) {
		inMemoryStore.remove(id);
	}

	@Override
	public void delete(@NonNull T entity) {
		String id = idGetter.apply(entity);

		inMemoryStore.remove(id);
	}

	@Override
	public void deleteAllById(@NonNull Iterable<? extends String> ids) {
		for (String id : ids)
			inMemoryStore.remove(id);
	}

	@Override
	public void deleteAll(@NonNull Iterable<? extends T> entities) {

		for (T entity : entities)
			inMemoryStore.remove(idGetter.apply(entity));
	}

	@Override
	public void deleteAll() {
		inMemoryStore.clear();
	}

}
