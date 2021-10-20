/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.registry.configuration;

import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnumHelper;

import com.google.common.base.Strings;

/**
 * Possible types for registry backends.
 * 
 * @author espen
 *
 */
public enum RegistryBackend {
	/**
	 * Enum values of KeyElements
	 */
	INMEMORY("InMemory"),
	SQL("SQL"),
	MONGODB("MongoDB");
	
	private String literal;

	private RegistryBackend(String literal) {
		this.literal = literal;
	}

	@Override
	public String toString() {
		return literal;
	}

	/**
	 * Method to transform string literal to RegistryBackend enum.
	 * 
	 * @see StandardizedLiteralEnumHelper StandardizedLiteralEnumHelper
	 * 
	 * @param literal
	 * @return
	 */
	public static RegistryBackend fromString(String literal) {
		if (Strings.isNullOrEmpty(literal)) {
			return null;
		}

		RegistryBackend[] enumConstants = RegistryBackend.class.getEnumConstants();
		for (RegistryBackend constant : enumConstants) {
			if (constant.toString().equals(literal)) {
				return constant;
			}
		}
		throw new IllegalArgumentException("The literal '" + literal + "' is not a valid RegistryBackend");
	}
}
