/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.configuration;

import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnumHelper;

import com.google.common.base.Strings;

/**
 * Possible types for mqtt message persistence.
 * 
 * @author espen
 *
 */
public enum MqttPersistence {
	/**
	 * Enum values of KeyElements
	 */
	INMEMORY("InMemory"),
	FILE("File");
	
	private String literal;

	private MqttPersistence(String literal) {
		this.literal = literal;
	}

	@Override
	public String toString() {
		return literal;
	}

	/**
	 * Method to transform string literal to MqttPersistence enum.
	 * 
	 * @see StandardizedLiteralEnumHelper StandardizedLiteralEnumHelper
	 * 
	 * @param literal
	 * @return
	 */
	public static MqttPersistence fromString(String literal) {
		if (Strings.isNullOrEmpty(literal)) {
			return null;
		}

		MqttPersistence[] enumConstants = MqttPersistence.class.getEnumConstants();
		for (MqttPersistence constant : enumConstants) {
			if (constant.toString().equals(literal)) {
				return constant;
			}
		}
		throw new IllegalArgumentException("The literal '" + literal + "' is not a valid MqttPersistence type");
	}
}
