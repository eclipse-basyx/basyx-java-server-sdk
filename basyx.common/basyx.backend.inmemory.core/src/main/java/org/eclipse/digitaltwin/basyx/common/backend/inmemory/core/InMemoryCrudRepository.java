package org.eclipse.digitaltwin.basyx.common.backend.inmemory.core;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

public class InMemoryCrudRepository<T> implements CrudRepository<T, String> {
	
	private final ConcurrentMap<String, T> inMemoryStore = new ConcurrentHashMap<>();
	private Function<T, String> idGetter;
	
	public InMemoryCrudRepository(Function<T, String> idGetter) {
		this.idGetter = idGetter;
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
