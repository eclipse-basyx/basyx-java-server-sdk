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

package org.eclipse.digitaltwin.basyx.aasservice.feature.mqtt;

import java.util.StringJoiner;

import org.eclipse.digitaltwin.basyx.common.mqttcore.AbstractMqttTopicFactory;
import org.eclipse.digitaltwin.basyx.common.mqttcore.encoding.Encoder;

public class MqttAasServiceTopicFactory extends AbstractMqttTopicFactory {
	private static final String AASREPOSITORY = "aas-repository";
	private static final String SHELLS = "shells";
	private static final String ASSET_INFORMATION = "assetInformation";
	private static final String SUBMODEL_REFERENCES = "submodelReferences";
	private static final String CREATED = "created";
	private static final String UPDATED = "updated";
	private static final String DELETED = "deleted";

	public MqttAasServiceTopicFactory(Encoder encoder) {
		super(encoder);
	}

	/**
	 * Creates the hierarchical topic for a AssetInformation change eventimport
	 * org.eclipse.digitaltwin.basyx.common.encoding.Encoder;
	 * 
	 * @param repoId
	 * @return
	 */
	public String createSetAssetInformationTopic(String repoId, String shellId) {
		return new StringJoiner("/", "", "").add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(encodeId(shellId))
				.add(ASSET_INFORMATION)
				.add(UPDATED)
				.toString();
	}

	/**
	 * Creates the hierarchical topic for the update event
	 * 
	 * @param repoId
	 * @return
	 */
	public String createAddSubmodelReferenceTopic(String repoId, String shellId) {
		return new StringJoiner("/", "", "").add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(encodeId(shellId))
				.add(SUBMODEL_REFERENCES)
				.add(CREATED)
				.toString();
	}

	/**
	 * Creates the hierarchical topic for the remove submodelReference event
	 * 
	 * @param repoId
	 * @return
	 */
	public String createRemoveSubmodelReferenceTopic(String repoId, String shellId) {
		return new StringJoiner("/", "", "").add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(encodeId(shellId))
				.add(SUBMODEL_REFERENCES)
				.add(DELETED)
				.toString();
	}

}
