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

import org.eclipse.digitaltwin.aas4j.v3.model.LangString;
import org.eclipse.digitaltwin.aas4j.v3.model.MultiLanguageProperty;
import org.eclipse.digitaltwin.basyx.submodelservice.value.LangStringValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.MultiLanguagePropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;

/**
 * Maps {@link MultiLanguageProperty} value to
 * {@link MultiLanguagePropertyValue}
 * 
 * @author danish
 *
 */
public class MultiLanguagePropertyValueMapper implements ValueMapper {
	private MultiLanguagePropertyValue multiLanguagePropertyValue;

	public MultiLanguagePropertyValueMapper(MultiLanguageProperty multiLanguageProperty) {
		this.multiLanguagePropertyValue = new MultiLanguagePropertyValue(
				mapLangString(multiLanguageProperty.getValue()));
	}

	@Override
	public SubmodelElementValue getValue() {
		return this.multiLanguagePropertyValue;
	}

	private List<LangStringValue> mapLangString(List<LangString> langStrings) {
		return langStrings.stream().map(LangStringValue::new).collect(Collectors.toList());
	}
}
