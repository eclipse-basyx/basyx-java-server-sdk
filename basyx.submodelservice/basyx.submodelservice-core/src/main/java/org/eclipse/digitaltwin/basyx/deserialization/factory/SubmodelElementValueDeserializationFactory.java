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

package org.eclipse.digitaltwin.basyx.deserialization.factory;

import static org.eclipse.digitaltwin.basyx.deserialization.util.SubmodelElementValueDeserializationUtil.isTypeOfAnnotatedRelationshipElementValue;
import static org.eclipse.digitaltwin.basyx.deserialization.util.SubmodelElementValueDeserializationUtil.isTypeOfBasicEventValue;
import static org.eclipse.digitaltwin.basyx.deserialization.util.SubmodelElementValueDeserializationUtil.isTypeOfEntityValue;
import static org.eclipse.digitaltwin.basyx.deserialization.util.SubmodelElementValueDeserializationUtil.isTypeOfFileBlobValue;
import static org.eclipse.digitaltwin.basyx.deserialization.util.SubmodelElementValueDeserializationUtil.isTypeOfMultiLanguagePropertyValue;
import static org.eclipse.digitaltwin.basyx.deserialization.util.SubmodelElementValueDeserializationUtil.isTypeOfPropertyValue;
import static org.eclipse.digitaltwin.basyx.deserialization.util.SubmodelElementValueDeserializationUtil.isTypeOfRangeValue;
import static org.eclipse.digitaltwin.basyx.deserialization.util.SubmodelElementValueDeserializationUtil.isTypeOfReferenceElementValue;
import static org.eclipse.digitaltwin.basyx.deserialization.util.SubmodelElementValueDeserializationUtil.isTypeOfRelationshipElementValue;
import static org.eclipse.digitaltwin.basyx.deserialization.util.SubmodelElementValueDeserializationUtil.isTypeOfSubmodelElementCollectionValue;
import static org.eclipse.digitaltwin.basyx.deserialization.util.SubmodelElementValueDeserializationUtil.isTypeOfSubmodelElementListValue;

import org.eclipse.digitaltwin.basyx.deserialization.exception.SubmodelElementValueDeserializationException;
import org.eclipse.digitaltwin.basyx.submodelservice.value.AnnotatedRelationshipElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.BasicEventValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.EntityValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.FileBlobValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.PropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.RangeValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ReferenceElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ReferenceValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.RelationshipElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Factory class to create deserialized {@link SubmodelElementValue} based on
 * the content
 * 
 * @author danish
 *
 */
public class SubmodelElementValueDeserializationFactory {

	/**
	 * Deserializes the corresponding {@link SubmodelElementValue} based on the
	 * JSON content
	 * 
	 * @return SubmodelELementValue
	 * 
	 * @throws JsonProcessingException 
	 * @throws SubmodelElementValueDeserializationException
	 */
	public SubmodelElementValue create(ObjectMapper mapper, JsonNode node) throws JsonProcessingException {
		if (isTypeOfRangeValue(node)) {
			return mapper.convertValue(node, RangeValue.class);
		} else if (isTypeOfMultiLanguagePropertyValue(node)) {
			return new MultiLanguagePropertyValueDeserializationFactory(node).create();
		} else if (isTypeOfFileBlobValue(node)) {
			return mapper.convertValue(node, FileBlobValue.class);
		} else if (isTypeOfPropertyValue(node)) {
			return mapper.convertValue(node, PropertyValue.class);
		} else if (isTypeOfEntityValue(node)) {
			return mapper.convertValue(node, EntityValue.class);
		} else if (isTypeOfReferenceElementValue(node)) {
			return new ReferenceElementValue(mapper.convertValue(node, ReferenceValue.class));
		} else if (isTypeOfRelationshipElementValue(node)) {
			return mapper.convertValue(node, RelationshipElementValue.class);
		} else if (isTypeOfAnnotatedRelationshipElementValue(node)) {
			return mapper.convertValue(node, AnnotatedRelationshipElementValue.class);
		} else if (isTypeOfBasicEventValue(node)) {
			return mapper.convertValue(node, BasicEventValue.class);
		} else if (isTypeOfSubmodelElementListValue(node)) {
			return new SubmodelElementListValueDeserializationFactory(mapper, node).create();
		} else if (isTypeOfSubmodelElementCollectionValue(node)) {
			return new SubmodelElementCollectionValueDeserializationFactory(mapper, node).create();
		}

		throw new SubmodelElementValueDeserializationException();
	}
}
