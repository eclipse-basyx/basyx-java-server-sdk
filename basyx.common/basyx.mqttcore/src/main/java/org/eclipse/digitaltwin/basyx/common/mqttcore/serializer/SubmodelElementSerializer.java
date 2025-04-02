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

package org.eclipse.digitaltwin.basyx.common.mqttcore.serializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Blob;
import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.MultiLanguageProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifier;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;

/**
 * Serializer for the submodel element.
 * 
 * @author fischer
 */
public class SubmodelElementSerializer {
	public static final String EMPTYVALUEUPDATE_TYPE = "emptyValueUpdateEvent";

	private SubmodelElementSerializer() {
	}

	/**
	 * Serializer to create a JSON String for the given submodel element.
	 * 
	 * @param submodelElement
	 * @return serialized submodelElement as JSON String
	 */
	public static String serializeSubmodelElement(SubmodelElement submodelElement) {
		try {
			SubmodelElement localElement;
			if (shouldSendEmptyValueEvent(submodelElement)) {
				localElement = getSubmodelElementWithoutValue(submodelElement);
			} else {
				localElement = submodelElement;
			}

			return new JsonSerializer().write(localElement);
		} catch (SerializationException | DeserializationException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Serializer to create a JSON String for the given submodel elements.
	 * 
	 * @param submodelElements
	 * @return serialized list of submodelElements as JSON String
	 */
	public static String serializeSubmodelElements(List<SubmodelElement> submodelElements) {
		try {
			List<SubmodelElement> updatedSubmodelElements = new ArrayList<>();
			
			for(int i = 0; i < submodelElements.size(); i++) {
				SubmodelElement elem = submodelElements.get(i);
				SubmodelElement localElement;
				if (shouldSendEmptyValueEvent(elem)) {
					localElement = getSubmodelElementWithoutValue(elem);
				} else {
					localElement = elem;
				}
				
				updatedSubmodelElements.add(localElement);
			}
			
			return new JsonSerializer().writeList(updatedSubmodelElements);
		} catch (SerializationException | DeserializationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Generator to create a copy of a submodelElement without its value.
	 * 
	 * @param submodelElement
	 * @return submodelElement without value
	 * @throws SerializationException
	 * @throws DeserializationException
	 */
	private static SubmodelElement getSubmodelElementWithoutValue(SubmodelElement submodelElement) throws SerializationException, DeserializationException {
		// Copy the SubmodelElement to not modify the original.
		String jsonToCopy = new JsonSerializer().write(submodelElement);
		SubmodelElement localElement = new JsonDeserializer().read(jsonToCopy, SubmodelElement.class);

		if (submodelElement instanceof Blob) {
			((Blob) localElement).setValue(null);
		} else if (submodelElement instanceof File) {
			((File) localElement).setValue(null);
		} else if (submodelElement instanceof MultiLanguageProperty) {
			((MultiLanguageProperty) localElement).setValue(null);
		} else if (submodelElement instanceof Property) {
			((Property) localElement).setValue(null);
		} else if (submodelElement instanceof ReferenceElement) {
			((ReferenceElement) localElement).setValue(null);
		} else if (submodelElement instanceof SubmodelElementCollection) {
			((SubmodelElementCollection) localElement).setValue(null);
		} else if (submodelElement instanceof SubmodelElementList) {
			((SubmodelElementList) localElement).setValue(null);
		}

		return localElement;
	}

	/**
	 * Returns true if the submodelElement has an EmptyValueUpdateEvent with a value
	 * of true.
	 */
	private static boolean shouldSendEmptyValueEvent(SubmodelElement submodelElement) {
		Optional<Qualifier> qualifier = submodelElement.getQualifiers().stream().filter(c -> c instanceof Qualifier).map(Qualifier.class::cast).filter(q -> q.getType().equals(EMPTYVALUEUPDATE_TYPE)).findAny();
		if (qualifier.isEmpty()) {
			return false;
		}

		return Boolean.parseBoolean(qualifier.get().getValue());
	}
}
