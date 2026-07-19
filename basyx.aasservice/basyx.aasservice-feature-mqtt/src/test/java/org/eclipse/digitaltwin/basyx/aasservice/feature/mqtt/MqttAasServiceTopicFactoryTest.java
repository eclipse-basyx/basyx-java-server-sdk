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
package org.eclipse.digitaltwin.basyx.aasservice.feature.mqtt;

import static org.junit.Assert.assertEquals;

import org.eclipse.digitaltwin.basyx.common.mqttcore.encoding.Base64URLEncoder;
import org.junit.Test;

public class MqttAasServiceTopicFactoryTest {
	private static final String AAS_ID = "https://example.org/aas/ä";
	private static final String ENCODED_AAS_ID = "aHR0cHM6Ly9leGFtcGxlLm9yZy9hYXMvw6Q";

	private final MqttAasServiceTopicFactory topicFactory = new MqttAasServiceTopicFactory(new Base64URLEncoder());

	@Test
	public void encodesShellIdentifiers() {
		assertEquals("aas-repository/custom-repo/shells/" + ENCODED_AAS_ID + "/assetInformation/updated",
				topicFactory.createSetAssetInformationTopic("custom-repo", AAS_ID));
		assertEquals("aas-repository/custom-repo/shells/" + ENCODED_AAS_ID + "/submodelReferences/created",
				topicFactory.createAddSubmodelReferenceTopic("custom-repo", AAS_ID));
	}
}
