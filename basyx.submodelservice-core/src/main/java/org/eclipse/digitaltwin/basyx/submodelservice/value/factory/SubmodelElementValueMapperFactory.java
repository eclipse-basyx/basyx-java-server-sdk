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
package org.eclipse.digitaltwin.basyx.submodelservice.value.factory;

import org.eclipse.digitaltwin.aas4j.v3.model.AnnotatedRelationshipElement;
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
import org.eclipse.digitaltwin.basyx.submodelservice.value.exception.ValueMapperNotFoundException;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.AnnotatedRelationshipElementValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.BlobValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.EntityValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.FileValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.MultiLanguagePropertyValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.PropertyValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.RangeValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.ReferenceElementValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.RelationshipElementValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.SubmodelElementCollectionValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.SubmodelElementListValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.ValueMapper;

/**
 * Factory class to create {@link ValueMapper} based on the provided
 * {@link SubmodelElement}
 * 
 * @author danish
 *
 */
public class SubmodelElementValueMapperFactory {

	public ValueMapper create(SubmodelElement submodelElement) {
		if (submodelElement instanceof Property) {
			return new PropertyValueMapper((Property) submodelElement);
		} else if (submodelElement instanceof Range) {
			return new RangeValueMapper((Range) submodelElement);
		} else if (submodelElement instanceof MultiLanguageProperty) {
			return new MultiLanguagePropertyValueMapper((MultiLanguageProperty) submodelElement);
		} else if (submodelElement instanceof File) {
			return new FileValueMapper((File) submodelElement);
		} else if (submodelElement instanceof Blob) {
			return new BlobValueMapper((Blob) submodelElement);
		} else if (submodelElement instanceof Entity) {
			return new EntityValueMapper((Entity) submodelElement);
		} else if (submodelElement instanceof ReferenceElement) {
			return new ReferenceElementValueMapper((ReferenceElement) submodelElement);
		} else if (submodelElement instanceof AnnotatedRelationshipElement) {
			return new AnnotatedRelationshipElementValueMapper((AnnotatedRelationshipElement) submodelElement);
		} else if (submodelElement instanceof RelationshipElement) {
			return new RelationshipElementValueMapper((RelationshipElement) submodelElement);
		} else if (submodelElement instanceof SubmodelElementCollection) {
			return new SubmodelElementCollectionValueMapper((SubmodelElementCollection) submodelElement);
		} else if (submodelElement instanceof SubmodelElementList) {
			return new SubmodelElementListValueMapper((SubmodelElementList) submodelElement);
		} else {
			throw new ValueMapperNotFoundException(submodelElement.getIdShort());
		}
	}
}
