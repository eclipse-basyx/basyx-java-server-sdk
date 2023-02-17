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
package org.eclipse.digitaltwin.basyx.submodelservice.value;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.LangString;
import org.eclipse.digitaltwin.aas4j.v3.model.MultiLanguageProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Range;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangString;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceUtil;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.FileValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.MultiLanguagePropertyValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.PropertyValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.RangeValueMapper;
import org.eclipse.digitaltwin.basyx.submodelservice.value.mapper.ValueMapper;
import org.junit.Test;

/**
 * Tests the mapped value from SubmodelElements
 * 
 * @author danish
 *
 */
public class TestMappedSubmodelElementValue {

	@Test
	public void mappedGetRangeValue() {
		Range expected = SubmodelServiceUtil.createRangeSubmodelElement();

		RangeValue retrievedValue = (RangeValue) new RangeValueMapper(expected).getValue();

		assertEquals(expected.getMin(), String.valueOf(retrievedValue.getMin()));

		assertEquals(expected.getMax(), String.valueOf(retrievedValue.getMax()));
	}
	
	@Test
	public void mappedSetRangeValue() {
		int expectedMin = 50;
		int expectedMax = 100;

		Range range = SubmodelServiceUtil.createRangeSubmodelElement();

		setRangeValue(expectedMin, expectedMax, range);

		assertEquals(Integer.valueOf(expectedMin), Integer.valueOf(range.getMin()));
		
		assertEquals(Integer.valueOf(expectedMax), Integer.valueOf(range.getMax()));
	}

	@Test
	public void mappedGetPropertyValue() {
		String expectedValue = "200";

		Property property = new DefaultProperty.Builder().value(expectedValue).valueType(DataTypeDefXsd.INTEGER)
				.build();

		ValueMapper rangeValueMapper = new PropertyValueMapper(property);

		assertEquals(expectedValue, ((PropertyValue) rangeValueMapper.getValue()).getValue());
	}
	
	@Test
	public void mappedSetPropertyValue() {
		String expectedValue = "5000";
		
		Property property = SubmodelServiceUtil.createPropertySubmodelElement();

		setPropertyValue(expectedValue, property);

		assertEquals(expectedValue, property.getValue());
	}

	@Test
	public void mappedGetMultiLanguagePropertyValue() {
		List<LangString> expectedValue = Arrays.asList(new DefaultLangString("Hello", "en"), new DefaultLangString("Hallo", "de"));

		MultiLanguageProperty multiLanguageProperty = SubmodelServiceUtil.createMultiLanguagePropertySubmodelElement();

		ValueMapper multiLanguagePropertyValueMapper = new MultiLanguagePropertyValueMapper(multiLanguageProperty);

		assertEquals(expectedValue.get(0).getLanguage(),
				((MultiLanguagePropertyValue) multiLanguagePropertyValueMapper.getValue()).getValue().get(0)
						.getLanguage());
	}

	@Test
	public void mappedSetMultiLanguagePropertyValue() {
		List<LangString> expectedValue = Arrays.asList(new DefaultLangString("Bonjour", "fr"), new DefaultLangString("Hola", "es"));

		MultiLanguageProperty multiLanguageProperty = SubmodelServiceUtil.createMultiLanguagePropertySubmodelElement();

		setMultiLanguagePropertyValue(expectedValue, multiLanguageProperty);

		assertEquals(expectedValue, multiLanguageProperty.getValue());
	}

	@Test
	public void mappedGetFileValue() {
		String expectedValue = SubmodelServiceUtil.SUBMODEL_TECHNICAL_DATA_FILE_VALUE;

		File file = SubmodelServiceUtil.createFileSubmodelElement();

		ValueMapper fileValueMapper = new FileValueMapper(file);

		assertEquals(expectedValue, ((FileValue) fileValueMapper.getValue()).getValue());
	}
	
	@Test
	public void mappedSetFileValue() {
		String expectedContentType = "application/pdf";
		String expectedValue = "someTestFile.pdf";
		
		File file = SubmodelServiceUtil.createFileSubmodelElement();

		setFileValue(expectedContentType, expectedValue, file);

		assertEquals(expectedContentType, file.getContentType());
		
		assertEquals(expectedValue, file.getValue());
	}
	
	private void setFileValue(String expectedContentType, String expectedValue, File file) {
		SubmodelElementValue submodelElementValue = new FileValue(expectedContentType, expectedValue);

		ValueMapper fileValueMapper = new FileValueMapper(file);
		fileValueMapper.setValue(submodelElementValue);
	}
	
	private void setRangeValue(int expectedMin, int expectedMax, Range range) {
		SubmodelElementValue submodelElementValue = new RangeValue(expectedMin, expectedMax);

		ValueMapper rangeValueMapper = new RangeValueMapper(range);
		rangeValueMapper.setValue(submodelElementValue);
	}
	
	private void setPropertyValue(String expectedValue, Property property) {
		SubmodelElementValue submodelElementValue = new PropertyValue(expectedValue);

		ValueMapper propertyValueMapper = new PropertyValueMapper(property);
		propertyValueMapper.setValue(submodelElementValue);
	}
	
	private void setMultiLanguagePropertyValue(List<LangString> valueToWrite,
			MultiLanguageProperty multiLanguageProperty) {
		SubmodelElementValue submodelElementValue = new MultiLanguagePropertyValue(valueToWrite);

		ValueMapper multiLanguagePropertyValueMapper = new MultiLanguagePropertyValueMapper(multiLanguageProperty);
		multiLanguagePropertyValueMapper.setValue(submodelElementValue);
	}
}
