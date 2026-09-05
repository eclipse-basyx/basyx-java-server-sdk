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

package org.eclipse.digitaltwin.basyx.aasenvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Entity;
import org.eclipse.digitaltwin.aas4j.v3.model.EntityType;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.CompleteEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.CompleteEnvironment.EnvironmentType;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Regression for #933: XML-loaded SelfManagedEntity must keep SpecificAssetId
 * fields and must not JSON-serialize them as {@code {}}.
 */
public class TestEntitySpecificAssetIdsXmlLoad {

	private static final String XML_RESOURCE = "/org/eclipse/digitaltwin/basyx/aasenvironment/self_managed_entity_specific_asset_ids.xml";
	private static final String ASSET_ID_NAME = "https://admin-shell.io/idta/test";
	private static final String ASSET_ID_VALUE = "Value1";

	@Test
	public void xmlLoadedSelfManagedEntitySpecificAssetIdIsNotEmptyObject() throws Exception {
		Environment environment;
		try (InputStream xml = getClass().getResourceAsStream(XML_RESOURCE)) {
			environment = CompleteEnvironment.fromInputStream(xml, EnvironmentType.XML).getEnvironment();
		}

		Entity entity = findEntryNode(environment);
		assertEquals(EntityType.SELF_MANAGED_ENTITY, entity.getEntityType());

		List<SpecificAssetId> specificAssetIds = entity.getSpecificAssetIds();
		assertEquals(1, specificAssetIds.size());
		SpecificAssetId specificAssetId = specificAssetIds.get(0);
		assertEquals(ASSET_ID_NAME, specificAssetId.getName());
		assertEquals(ASSET_ID_VALUE, specificAssetId.getValue());
		assertNotNull(specificAssetId.getSemanticId());

		JsonNode json = new ObjectMapper().readTree(new JsonSerializer().write(entity));
		JsonNode serializedIds = json.get("specificAssetIds");
		assertEquals(1, serializedIds.size());
		assertFalse("specificAssetIds must not serialize as {}", serializedIds.get(0).isEmpty());
		assertEquals(ASSET_ID_NAME, serializedIds.get(0).get("name").asText());
		assertEquals(ASSET_ID_VALUE, serializedIds.get(0).get("value").asText());
	}

	private static Entity findEntryNode(Environment environment) {
		SubmodelElement element = environment.getSubmodels().get(0).getSubmodelElements().get(0);
		assertEquals("EntryNode", element.getIdShort());
		return (Entity) element;
	}
}
