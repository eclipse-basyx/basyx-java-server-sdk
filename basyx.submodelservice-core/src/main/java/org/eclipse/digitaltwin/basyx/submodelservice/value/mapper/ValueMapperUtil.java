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

package org.eclipse.digitaltwin.basyx.submodelservice.value.mapper;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ValueOnly;
import org.eclipse.digitaltwin.basyx.submodelservice.value.exception.SubmodelElementValueNotFoundException;
import org.eclipse.digitaltwin.basyx.submodelservice.value.factory.SubmodelElementValueMapperFactory;

/**
 * Helper class for ValueMapper
 * 
 * @author danish
 *
 */
public class ValueMapperUtil {

	private ValueMapperUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Transforms the {@link SubmodelElement} into a new instance of
	 * {@link ValueOnly}
	 * 
	 * @param submodelElement
	 * @return ValueOnly
	 */
	public static ValueOnly toValueOnly(SubmodelElement submodelElement) {
		String idShort = submodelElement.getIdShort();
		SubmodelElementValue submodelElementValue = new SubmodelElementValueMapperFactory().create(submodelElement)
				.getValue();

		return new ValueOnly(idShort, submodelElementValue);
	}

	/**
	 * Filters a {@link SubmodelElementValue} from {@link ValueOnly} that matches
	 * the corresponding {@link SubmodelElement}
	 * 
	 * @param submodelElement        
	 * @param valueOnlies            list of ValueOnly
	 * @return SubmodelElementValue  the matching submodel element value
	 * 
	 * @throws SubmodelElementValueNotFoundException
	 */
	public static SubmodelElementValue getSubmodelElementValue(SubmodelElement submodelElement,
			List<ValueOnly> valueOnlies) {
		Optional<ValueOnly> optionalValueOnly = valueOnlies.stream().parallel()
				.filter(filterMatchingValueOnly(submodelElement)).findAny();

		if (!optionalValueOnly.isPresent())
			throw new SubmodelElementValueNotFoundException(submodelElement.getIdShort());

		return optionalValueOnly.get().getSubmodelElementValue();
	}

	private static Predicate<? super ValueOnly> filterMatchingValueOnly(SubmodelElement submodelElement) {
		return valueOnly -> submodelElement.getIdShort().equals(valueOnly.getIdShort());
	}

}
