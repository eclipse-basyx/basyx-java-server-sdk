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

package org.eclipse.digitaltwin.basyx.aasxfileserver;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.digitaltwin.basyx.aasxfileserver.model.Package;
import org.springframework.data.repository.CrudRepository;

public class AASXFileServerInMemoryCrudRepository implements CrudRepository<Package, String> {

	private Map<String, Package> packageMap = new LinkedHashMap<>();

	@Override
	public <S extends Package> S save(S entity) {
		packageMap.put(entity.getPackageId(), entity);
		return entity;
	}

	@Override
	public <S extends Package> Iterable<S> saveAll(Iterable<S> entities) {
		for (Package entity : entities) {
			save(entity);
		}
		return entities;
	}

	@Override
	public Optional<Package> findById(String id) {
		return Optional.ofNullable(packageMap.get(id));
	}

	@Override
	public boolean existsById(String id) {
		return packageMap.containsKey(id);
	}

	@Override
	public Iterable<Package> findAll() {
		return packageMap.values();
	}

	@Override
	public Iterable<Package> findAllById(Iterable<String> ids) {
		List<String> idList = StreamSupport.stream(ids.spliterator(), false).collect(Collectors.toList());
		return packageMap.entrySet().stream().filter(entry -> idList.contains(entry.getKey())).map(entry -> entry.getValue()).collect(Collectors.toList());
	}

	@Override
	public long count() {
		return packageMap.size();
	}

	@Override
	public void deleteById(String id) {
		packageMap.remove(id);
	}

	@Override
	public void delete(Package entity) {
		packageMap.remove(entity.getPackageId());
	}

	@Override
	public void deleteAllById(Iterable<? extends String> ids) {
		List<String> idList = StreamSupport.stream(ids.spliterator(), false).collect(Collectors.toList());
		packageMap.keySet().removeAll(idList);
	}

	@Override
	public void deleteAll(Iterable<? extends Package> entities) {
		List<String> idList = StreamSupport.stream(entities.spliterator(), false).map(Package::getPackageId).collect(Collectors.toList());
		packageMap.keySet().removeAll(idList);
	}

	@Override
	public void deleteAll() {
		packageMap.clear();
	}
}
