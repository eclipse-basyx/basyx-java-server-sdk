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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.AnnotatedRelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.BasicEventElement;
import org.eclipse.digitaltwin.aas4j.v3.model.Blob;
import org.eclipse.digitaltwin.aas4j.v3.model.Entity;
import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.MultiLanguageProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Range;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceElement;
import org.eclipse.digitaltwin.aas4j.v3.model.RelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.basyx.submodelservice.value.AnnotatedRelationshipElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.BasicEventValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.EntityValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.FileBlobValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.MultiLanguagePropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.PropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.RangeValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ReferenceElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.RelationshipElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementCollectionValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementListValue;
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
	
	private static final Map<Class<? extends SubmodelElement>, Class<? extends SubmodelElementValue>> SUBMODEL_ELEMENT_VALUE_MAP = Map.ofEntries(
            Map.entry(Property.class, PropertyValue.class),
            Map.entry(Range.class, RangeValue.class),
            Map.entry(MultiLanguageProperty.class, MultiLanguagePropertyValue.class),
            Map.entry(File.class, FileBlobValue.class),
			Map.entry(BasicEventElement.class, BasicEventValue.class),
            Map.entry(Blob.class, FileBlobValue.class),
            Map.entry(Entity.class, EntityValue.class),
            Map.entry(ReferenceElement.class, ReferenceElementValue.class),
            Map.entry(AnnotatedRelationshipElement.class, AnnotatedRelationshipElementValue.class),
            Map.entry(RelationshipElement.class, RelationshipElementValue.class),
            Map.entry(SubmodelElementCollection.class, SubmodelElementCollectionValue.class),
            Map.entry(SubmodelElementList.class, SubmodelElementListValue.class)
    );

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
	
	/**
	 * Creates a {@link ValueOnly} collection from the corresponding collection of {@link SubmodelElement}
	 * 
	 * @param submodelElements       collection of submodel elements
	 * @return valueOnlies           the created collection of value only
	 * 
	 */
	public static List<ValueOnly> createValueOnlyCollection(Collection<SubmodelElement> submodelElements) {
		return submodelElements.stream().map(ValueMapperUtil::toValueOnly).collect(Collectors.toList());
	}
		
	/**
	 * Updates the value of {@link SubmodelElement} with its corresponding {@link ValueOnly}
	 * 
	 * @param submodelElements       collection of submodel elements
	 * @param valueOnlies            list of value onlies
	 * 
	 */
	public static void setValueOfSubmodelElementWithValueOnly(Collection<SubmodelElement> submodelElements, List<ValueOnly> valueOnlies) {
		submodelElements.stream().forEach(submodelElement -> setValue(submodelElement, ValueMapperUtil.getSubmodelElementValue(submodelElement, valueOnlies)));
	}

	public static void setValueOfSubmodelElementWithValueOnly(Collection<SubmodelElement> submodelElements, Map<String,SubmodelElementValue> valueOnlies) {
		submodelElements.stream().forEach(submodelElement -> setValue(submodelElement, valueOnlies.get(submodelElement.getIdShort())));
	}
	
	/**
	 * Updates the value of {@link SubmodelElement} with its corresponding {@link SubmodelElementValue}
	 * 
	 * @param submodelElements          list of submodel elements
	 * @param submodelElementValues     list of submodel element values
	 * 
	 */
	public static void setValueOfSubmodelElementWithSubmodelElementValue(List<SubmodelElement> submodelElements, List<SubmodelElementValue> submodelElementValues) {
		submodelElements.stream().forEach(submodelElement -> setSubmodelElementValue(submodelElement, submodelElementValues));
	}
	
	/**
	 * Maps the submodel elements ({@link SubmodelElement}) with its corresponding new instance of submodel element values ({@link SubmodelElementValue})
	 * 
	 * @param submodelElements                  list of submodel elements
	 * @return submodelElementValues            list of the mapped new instance of submodel element values
	 * 
	 */
	public static List<SubmodelElementValue> createSubmodelElementValues(List<SubmodelElement> submodelElements) {
		return submodelElements.stream().map(ValueMapperUtil::toSubmodelElementValue).collect(Collectors.toList());	
	}

	private static void setSubmodelElementValue(SubmodelElement submodelElement,
			List<SubmodelElementValue> submodelElementValues) {
		Optional<SubmodelElementValue> optionalSubmodelElementValue = submodelElementValues.stream().parallel().filter(submodelElementValue -> isRelatedToSubmodelElement(submodelElement, submodelElementValue)).findAny();
		
		if (!optionalSubmodelElementValue.isPresent())
			throw new SubmodelElementValueNotFoundException(submodelElement.getIdShort());

		setValue(submodelElement, optionalSubmodelElementValue.get());
	}
	
	private static boolean isRelatedToSubmodelElement(SubmodelElement submodelElement, SubmodelElementValue submodelElementValue) {
	    return SUBMODEL_ELEMENT_VALUE_MAP.entrySet().stream()
	            .anyMatch(pair -> pair.getKey().isInstance(submodelElement) && pair.getValue().isInstance(submodelElementValue));
	}

	private static void setValue(SubmodelElement submodelElement, SubmodelElementValue submodelElementValue) {
		ValueMapper<SubmodelElementValue> valueMapper = getValueMapper(submodelElement);

		valueMapper.setValue(submodelElementValue);
	}
	
	private static Predicate<? super ValueOnly> filterMatchingValueOnly(SubmodelElement submodelElement) {
		return valueOnly -> submodelElement.getIdShort().equals(valueOnly.getIdShort());
	}
	
	private static SubmodelElementValue toSubmodelElementValue(SubmodelElement submodelElement) {
		ValueMapper<SubmodelElementValue> valueMapper = getValueMapper(submodelElement);
		
		return valueMapper.getValue();
	}
	
	private static ValueMapper<SubmodelElementValue> getValueMapper(SubmodelElement submodelElement) {
		return new SubmodelElementValueMapperFactory().create(submodelElement);
	}

}
