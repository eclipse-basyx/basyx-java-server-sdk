/*******************************************************************************
 * Copyright (C) 2026 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.http.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.Entity;
import org.eclipse.digitaltwin.aas4j.v3.model.EntityType;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEntity;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetId;
import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.http.BaSyxHTTPConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Regression for #933: SelfManagedEntity specificAssetIds must serialize
 * name/value/semanticId rather than an empty object.
 */
public class TestEntitySpecificAssetIdJsonSerialization {

	private static final String ASSET_ID_NAME = "https://admin-shell.io/idta/test";
	private static final String ASSET_ID_VALUE = "Value1";
	private static final String SEMANTIC_ID_VALUE = "https://admin-shell.io/idta/test";
	private static final String SUBJECT_ID_VALUE = "https://example.com/subject";

	private ObjectMapper mapper;

	@Before
	public void setUp() {
		Jackson2ObjectMapperBuilder builder = new BaSyxHTTPConfiguration()
				.jackson2ObjectMapperBuilder(List.of(new Aas4JHTTPSerializationExtension()));
		mapper = builder.build();
	}

	@Test
	public void selfManagedEntitySpecificAssetIdIsNotEmptyObject() throws Exception {
		Entity entity = new DefaultEntity.Builder().idShort("EntryNode")
				.entityType(EntityType.SELF_MANAGED_ENTITY)
				.globalAssetId("https://admin-shell.io/idta/HierarchicalStructures/EntryNode/1/0")
				.specificAssetIds(List.of(new DefaultSpecificAssetId.Builder().name(ASSET_ID_NAME)
						.value(ASSET_ID_VALUE)
						.semanticId(new DefaultReference.Builder().type(ReferenceTypes.EXTERNAL_REFERENCE)
								.keys(new DefaultKey.Builder().type(KeyTypes.GLOBAL_REFERENCE)
										.value(SEMANTIC_ID_VALUE)
										.build())
								.build())
						.externalSubjectId(new DefaultReference.Builder().type(ReferenceTypes.EXTERNAL_REFERENCE)
								.keys(new DefaultKey.Builder().type(KeyTypes.GLOBAL_REFERENCE)
										.value(SUBJECT_ID_VALUE)
										.build())
								.build())
						.build()))
				.build();

		JsonNode json = mapper.readTree(mapper.writeValueAsString(entity));
		JsonNode specificAssetIds = json.get("specificAssetIds");

		assertTrue(specificAssetIds.isArray());
		assertEquals(1, specificAssetIds.size());
		JsonNode assetId = specificAssetIds.get(0);
		assertFalse("specificAssetIds must not serialize as {}", assetId.isEmpty());
		assertEquals(ASSET_ID_NAME, assetId.get("name").asText());
		assertEquals(ASSET_ID_VALUE, assetId.get("value").asText());
		assertEquals(SEMANTIC_ID_VALUE, assetId.get("semanticId").get("keys").get(0).get("value").asText());
		assertEquals(SUBJECT_ID_VALUE, assetId.get("externalSubjectId").get("keys").get(0).get("value").asText());
	}
}
