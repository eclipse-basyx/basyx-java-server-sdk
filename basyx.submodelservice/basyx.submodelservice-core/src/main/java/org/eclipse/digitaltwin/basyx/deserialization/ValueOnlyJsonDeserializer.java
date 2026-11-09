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


package org.eclipse.digitaltwin.basyx.deserialization;


import org.eclipse.digitaltwin.basyx.deserialization.factory.SubmodelElementValueDeserializationFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ValueOnly;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.JsonNode;

/**
 * Deserializes the ValueOnly as described in DotAAS Part 2
 * 
 * @author danish
 *
 */
public class ValueOnlyJsonDeserializer extends ValueDeserializer<ValueOnly> {
	
	private SubmodelElementValueDeserializationFactory submodelElementValueDeserializationFactory = new SubmodelElementValueDeserializationFactory();

	@Override
	public ValueOnly deserialize(JsonParser p, DeserializationContext ctxt) {
		JsonNode node = ctxt.readTree(p);
		
		String idShort = node.propertyNames().iterator().next();
		
		return new ValueOnly(idShort, submodelElementValueDeserializationFactory.create(ctxt, node.get(idShort)));
	}
}