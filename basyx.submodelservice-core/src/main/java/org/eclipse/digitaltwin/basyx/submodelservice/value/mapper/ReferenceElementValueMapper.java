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
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceElement;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ReferenceElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ReferenceValue;

/**
 * Maps {@link ReferenceElement} value to {@link ReferenceElementValue} 
 * 
 * @author danish
 *
 */
public class ReferenceElementValueMapper implements ValueMapper<ReferenceElementValue> {
	private ReferenceElement referenceElement;
	
	public ReferenceElementValueMapper(ReferenceElement referenceElement) {
		this.referenceElement = referenceElement;
	}

	@Override
	public ReferenceElementValue getValue() {
		return new ReferenceElementValue(new ReferenceValue(referenceElement.getValue().getType(), referenceElement.getValue().getKeys()));
	}

	@Override
	public void setValue(ReferenceElementValue referenceElementValue) {
		referenceElement.setValue(updateReference(referenceElementValue));
	}

	private Reference updateReference(ReferenceElementValue referenceElementValue) {
		Reference reference = referenceElement.getValue();
		reference.setType(referenceElementValue.getReferenceValue().getType());
		reference.setKeys(referenceElementValue.getReferenceValue().getKeys());
		
		return referenceElement.getValue();
	}
}
