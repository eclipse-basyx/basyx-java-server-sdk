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
package org.eclipse.digitaltwin.basyx.submodelrepository.feature.mqtt;

import static org.junit.Assert.assertEquals;

import org.eclipse.digitaltwin.basyx.common.mqttcore.encoding.Base64URLEncoder;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.mqtt.MqttSubmodelServiceTopicFactory;
import org.junit.Test;

public class MqttSubmodelServiceTopicFactoryTest {
	private static final String SUBMODEL_ID = "https://example.org/submodels/ä?x=1";
	private static final String ENCODED_SUBMODEL_ID = "aHR0cHM6Ly9leGFtcGxlLm9yZy9zdWJtb2RlbHMvw6Q_eD0x";

	private final MqttSubmodelServiceTopicFactory topicFactory = new MqttSubmodelServiceTopicFactory(new Base64URLEncoder());

	@Test
	public void usesSubmodelIdentityAndUniformEventSuffixes() {
		String elementPrefix = "sm-service/submodels/" + ENCODED_SUBMODEL_ID + "/submodelElements/property";

		assertEquals(elementPrefix + "/created", topicFactory.createCreateSubmodelElementTopic(SUBMODEL_ID, "property"));
		assertEquals(elementPrefix + "/updated", topicFactory.createUpdateSubmodelElementTopic(SUBMODEL_ID, "property"));
		assertEquals(elementPrefix + "/value/updated", topicFactory.createUpdateSubmodelElementValueTopic(SUBMODEL_ID, "property"));
		assertEquals(elementPrefix + "/deleted", topicFactory.createDeleteSubmodelElementTopic(SUBMODEL_ID, "property"));
		assertEquals("sm-service/submodels/" + ENCODED_SUBMODEL_ID + "/submodelElements/patched", topicFactory.createPatchSubmodelElementsTopic(SUBMODEL_ID));
		assertEquals(elementPrefix + "/attachment/updated", topicFactory.createUpdateFileValueTopic(SUBMODEL_ID, "property"));
		assertEquals(elementPrefix + "/attachment/deleted", topicFactory.createDeleteFileValueTopic(SUBMODEL_ID, "property"));
	}
}
