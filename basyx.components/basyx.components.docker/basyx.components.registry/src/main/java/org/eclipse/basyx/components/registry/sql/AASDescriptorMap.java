/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.registry.sql;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;

/**
 * This is a map implementation for a {@literal <String, AASDescriptor>} map which is needed
 * by map registries. It is based on an arbitrary {@literal <String, Object>} map and provides a proxy
 * access to that map by assuming AASDescriptor entries.
 * 
 * @author espen
 *
 */
public class AASDescriptorMap implements Map<String, AASDescriptor> {
	/**
	 * The map all operations of this map are based on
	 */
	private Map<String, Object> baseMap;

	/**
	 * Default constructor taking the base map
	 * 
	 * @param baseMap
	 */
	public AASDescriptorMap(Map<String, Object> baseMap) {
		this.baseMap = baseMap;
	}

	@Override
	public int size() {
		return baseMap.size();
	}

	@Override
	public boolean isEmpty() {
		return baseMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return baseMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return baseMap.containsValue(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AASDescriptor get(Object key) {
		Map<String, Object> mapEntry = (Map<String, Object>) baseMap.get(key);
		if (mapEntry == null) {
			return null;
		} else {
			return new AASDescriptor(mapEntry);
		}
	}

	@Override
	public AASDescriptor put(String key, AASDescriptor value) {
		return (AASDescriptor) baseMap.put(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AASDescriptor remove(Object key) {
		Map<String, Object> result = (Map<String, Object>) baseMap.remove(key);
		if (result == null) {
			return null;
		} else {
			return new AASDescriptor(result);
		}
	}

	@Override
	public void putAll(Map<? extends String, ? extends AASDescriptor> m) {
		baseMap.putAll(m);
	}

	@Override
	public void clear() {
		baseMap.clear();
	}

	@Override
	public Set<String> keySet() {
		return baseMap.keySet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<AASDescriptor> values() {
		return baseMap.values().stream()
				.map(o -> new AASDescriptor((Map<String, Object>) o))
				.collect(Collectors.toList());
	}

	@Override
	public Set<Entry<String, AASDescriptor>> entrySet() {
		return baseMap.entrySet().stream()
				.map(e -> new Entry<String, AASDescriptor>() {
					@Override
					public AASDescriptor setValue(AASDescriptor value) {
						return (AASDescriptor) e.setValue(value);
					}

					@SuppressWarnings("unchecked")
					@Override
					public AASDescriptor getValue() {
						if (e.getValue() == null) {
							return null;
						} else {
							return new AASDescriptor((Map<String, Object>) e.getValue());
						}
					}

					@Override
					public String getKey() {
						return e.getKey();
					}
				}).collect(Collectors.toSet());
	}

}
