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

package org.eclipse.digitaltwin.basyx.aasregistry.main.client.mapper;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AdministrativeInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.Extension;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType;
import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.http.BaSyxHTTPConfiguration;
import org.eclipse.digitaltwin.basyx.http.SerializationExtension;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit tests for {@link AttributeMapper}
 * 
 * @author danish
 */
public class TestAttributeMapper {

	private static AttributeMapper attributeMapper = new AttributeMapper(configureObjectMapper());

	@Test
	public void mapDescriptions() {
		List<LangStringTextType> expectedDescriptions = AttributeMapperFixture.getAasRegLangStringTextTypes();

		List<LangStringTextType> actualDescriptions = attributeMapper.mapDescription(AttributeMapperFixture.getAas4jLangStringTextTypes());

		assertEquals(expectedDescriptions.size(), actualDescriptions.size());
		assertEquals(expectedDescriptions, actualDescriptions);
	}

	@Test
	public void mapDisplayNames() {
		List<LangStringNameType> expectedDisplayNames = AttributeMapperFixture.getAasRegLangStringNameTypes();

		List<LangStringNameType> actualDisplayNames = attributeMapper.mapDisplayName(AttributeMapperFixture.getAas4jLangStringNameTypes());

		assertEquals(expectedDisplayNames.size(), actualDisplayNames.size());
		assertEquals(expectedDisplayNames, actualDisplayNames);
	}

	@Test
	public void mapExtensions() {
		List<Extension> expectedExtensions = AttributeMapperFixture.getAasRegExtensions();

		List<Extension> actualExtensions = attributeMapper.mapExtensions(AttributeMapperFixture.getAas4jExtensions());

		assertEquals(expectedExtensions.size(), actualExtensions.size());
		assertEquals(expectedExtensions, actualExtensions);
	}

	@Test
	public void mapAdministration() {
		AdministrativeInformation expectedAdministrativeInformation = AttributeMapperFixture.getAasRegAdministration();

		AdministrativeInformation actualAdministrativeInformation = attributeMapper.mapAdministration(AttributeMapperFixture.getAas4jAdministration());

		assertEquals(expectedAdministrativeInformation, actualAdministrativeInformation);
	}

	@Test
	public void mapAssetKind() {
		AssetKind expectedAssetKind = AttributeMapperFixture.AASREG_ASSET_KIND;

		AssetKind actualAssetKind = attributeMapper.mapAssetKind(AttributeMapperFixture.AAS4J_ASSET_KIND);

		assertEquals(expectedAssetKind, actualAssetKind);
	}

	private static ObjectMapper configureObjectMapper() {
		List<SerializationExtension> extensions = Arrays.asList(new Aas4JHTTPSerializationExtension());

		return new BaSyxHTTPConfiguration().jackson2ObjectMapperBuilder(extensions).build();
	}

}
