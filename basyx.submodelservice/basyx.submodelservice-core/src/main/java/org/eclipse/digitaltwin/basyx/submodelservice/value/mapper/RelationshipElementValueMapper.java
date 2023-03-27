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

import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.RelationshipElement;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ReferenceValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.RelationshipElementValue;

/**
 * Maps {@link RelationshipElement} value to {@link RelationshipElementValue} 
 * 
 * @author danish
 *
 */
public class RelationshipElementValueMapper implements ValueMapper<RelationshipElementValue> {
	private RelationshipElement relationshipElement;
	
	public RelationshipElementValueMapper(RelationshipElement relationshipElement) {
		this.relationshipElement = relationshipElement;
	}

	@Override
	public RelationshipElementValue getValue() {
		return new RelationshipElementValue(createReferenceValue(relationshipElement.getFirst()), createReferenceValue(relationshipElement.getSecond()));
	}

	@Override
	public void setValue(RelationshipElementValue relationshipElementValue) {
		setReferenceValue(relationshipElement.getFirst(), relationshipElementValue.getFirst());
		setReferenceValue(relationshipElement.getSecond(), relationshipElementValue.getSecond());
	}

	private void setReferenceValue(Reference reference, ReferenceValue referenceValue) {
		reference.setType(referenceValue.getType());
		reference.setKeys(referenceValue.getKeys());
	}
	
	private ReferenceValue createReferenceValue(Reference reference) {
		return new ReferenceValue(reference.getType(), reference.getKeys());
	}
}
