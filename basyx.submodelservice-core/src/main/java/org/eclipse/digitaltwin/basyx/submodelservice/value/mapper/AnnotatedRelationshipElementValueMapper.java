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
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.AnnotatedRelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.DataElement;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.submodelservice.value.AnnotatedRelationshipElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ReferenceValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ValueOnly;
import org.eclipse.digitaltwin.basyx.submodelservice.value.factory.SubmodelElementValueMapperFactory;

/**
 * Maps {@link AnnotatedRelationshipElement} value to {@link AnnotatedRelationshipElementValue}
 * 
 * @author danish
 *
 */
public class AnnotatedRelationshipElementValueMapper implements ValueMapper<AnnotatedRelationshipElementValue>{
	private AnnotatedRelationshipElement annotatedRelationshipElement;

	public AnnotatedRelationshipElementValueMapper(AnnotatedRelationshipElement relationshipElement) {
		this.annotatedRelationshipElement = relationshipElement;
	}

	@Override
	public AnnotatedRelationshipElementValue getValue() {
		return new AnnotatedRelationshipElementValue(createReferenceValue(annotatedRelationshipElement.getFirst()),
				createReferenceValue(annotatedRelationshipElement.getSecond()),
				createValueOnly(annotatedRelationshipElement.getAnnotations()));
	}

	@Override
	public void setValue(AnnotatedRelationshipElementValue annotatedRelationshipElementValue) {
		setReferenceValue(annotatedRelationshipElement.getFirst(), annotatedRelationshipElementValue.getFirst());
		setReferenceValue(annotatedRelationshipElement.getSecond(), annotatedRelationshipElementValue.getSecond());
		setDataElementValue(annotatedRelationshipElement.getAnnotations(), annotatedRelationshipElementValue.getAnnotation());
	}
	
	private void setDataElementValue(List<DataElement> annotations, List<ValueOnly> valueOnlies) {
		annotations.stream().forEach(annotation -> setValue(annotation, valueOnlies));
	}
	
	private void setValue(DataElement dataElement, List<ValueOnly> valueOnlies) {
		ValueMapper<SubmodelElementValue> valueMapper = new SubmodelElementValueMapperFactory().create(dataElement);
		
		valueMapper.setValue(ValueMapperUtil.getSubmodelElementValue(dataElement, valueOnlies));
	}

	private List<ValueOnly> createValueOnly(List<DataElement> annotations) {
		return annotations.stream().map(ValueMapperUtil::toValueOnly).collect(Collectors.toList());
	}

	private void setReferenceValue(Reference reference, ReferenceValue referenceValue) {
		reference.setType(referenceValue.getType());
		reference.setKeys(referenceValue.getKeys());
	}

	private ReferenceValue createReferenceValue(Reference reference) {
		return new ReferenceValue(reference.getType(), reference.getKeys());
	}
}
