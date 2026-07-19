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
package org.eclipse.digitaltwin.basyx.aasrepository.feature.mqtt;

import static org.junit.Assert.assertEquals;

import org.eclipse.digitaltwin.basyx.common.mqttcore.encoding.Base64URLEncoder;
import org.junit.Test;

public class MqttAasRepositoryTopicFactoryTest {
	private static final String SUBMODEL_ID = "https://example.org/submodels/ä?x=1";
	private static final String ENCODED_SUBMODEL_ID = "aHR0cHM6Ly9leGFtcGxlLm9yZy9zdWJtb2RlbHMvw6Q_eD0x";

	private final MqttAasRepositoryTopicFactory topicFactory = new MqttAasRepositoryTopicFactory(new Base64URLEncoder());

	@Test
	public void encodesSubmodelReferenceIdentifiers() {
		assertEquals("aas-repository/custom-repo/shells/submodels/" + ENCODED_SUBMODEL_ID + "/created",
				topicFactory.createCreateAASSubmodelReferenceTopic("custom-repo", SUBMODEL_ID));
		assertEquals("aas-repository/custom-repo/shells/submodels/" + ENCODED_SUBMODEL_ID + "/deleted",
				topicFactory.createDeleteAASSubmodelReferenceTopic("custom-repo", SUBMODEL_ID));
	}
}
