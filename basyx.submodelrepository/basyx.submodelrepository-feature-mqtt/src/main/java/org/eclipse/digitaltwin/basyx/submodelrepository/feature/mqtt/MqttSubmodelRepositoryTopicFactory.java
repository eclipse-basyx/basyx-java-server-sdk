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

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.mqtt;

import java.util.StringJoiner;

import org.eclipse.digitaltwin.basyx.common.mqttcore.AbstractMqttTopicFactory;
import org.eclipse.digitaltwin.basyx.common.mqttcore.encoding.Encoder;

/**
 * MQTT topic factory for the eventing on the submodel level.
 * 
 * @author fischer
 */
public class MqttSubmodelRepositoryTopicFactory extends AbstractMqttTopicFactory {
	private static final String SUBMODELREPOSITORY = "sm-repository";
	private static final String SUBMODELS = "submodels";
	private static final String CREATED = "created";
	private static final String UPDATED = "updated";
	private static final String DELETED = "deleted";
	private static final String PATCHED = "patched";
	private static final String SUBMODELELEMENTS = "submodelElements";
	private static final String ATTACHMENT = "attachment";

	/**
	 * @param encoder
	 *            Used for encoding the aasId/submodelId
	 */
	public MqttSubmodelRepositoryTopicFactory(Encoder encoder) {
		super(encoder);
	}

	/**
	 * Creates the hierarchical topic for the create event of submodels
	 * 
	 * @param repoId
	 */
	public String createCreateSubmodelTopic(String repoId) {
		return new StringJoiner("/", "", "").add(SUBMODELREPOSITORY).add(repoId).add(SUBMODELS).add(CREATED).toString();
	}

	/**
	 * Creates the hierarchical topic for the update event of submodels
	 * 
	 * @param repoId
	 */
	public String createUpdateSubmodelTopic(String repoId) {
		return new StringJoiner("/", "", "").add(SUBMODELREPOSITORY).add(repoId).add(SUBMODELS).add(UPDATED).toString();
	}

	/**
	 * Creates the hierarchical topic for the delete event of submodels
	 * 
	 * @param repoId
	 */
	public String createDeleteSubmodelTopic(String repoId) {
		return new StringJoiner("/", "", "").add(SUBMODELREPOSITORY).add(repoId).add(SUBMODELS).add(DELETED).toString();
	}

	/**
	 * Creates the hierarchical topic for the create event of submodelElements
	 * 
	 * @param repoId
	 */
	public String createCreateSubmodelElementTopic(String repoId, String submodelId, String submodelElementId) {
		return new StringJoiner("/", "", "").add(SUBMODELREPOSITORY).add(repoId).add(SUBMODELS).add(encodeId(submodelId)).add(SUBMODELELEMENTS).add(submodelElementId).add(CREATED).toString();
	}

	/**
	 * Creates the hierarchical topic for the update event of submodelElements
	 * 
	 * @param repoId
	 */
	public String createUpdateSubmodelElementTopic(String repoId, String submodelId, String submodelElementId) {
		return new StringJoiner("/", "", "").add(SUBMODELREPOSITORY).add(repoId).add(SUBMODELS).add(encodeId(submodelId)).add(SUBMODELELEMENTS).add(submodelElementId).add(UPDATED).toString();
	}

	/**
	 * Creates the hierarchical topic for the delete event of submodelElements
	 * 
	 * @param repoId
	 */
	public String createDeleteSubmodelElementTopic(String repoId, String submodelId, String submodelElementId) {
		return new StringJoiner("/", "", "").add(SUBMODELREPOSITORY).add(repoId).add(SUBMODELS).add(encodeId(submodelId)).add(SUBMODELELEMENTS).add(submodelElementId).add(DELETED).toString();
	}
	
	/**
	 * Creates the hierarchical topic for the patch event of submodelElements
	 * 
	 * @param repoId
	 * @param submodelId
	 */
	public String createPatchSubmodelElementsTopic(String repoId, String submodelId) {
		return new StringJoiner("/", "", "").add(SUBMODELREPOSITORY).add(repoId).add(SUBMODELS).add(encodeId(submodelId)).add(SUBMODELELEMENTS).add(PATCHED).toString();
	}
	
	/**
	 * Creates the hierarchical topic for the delete event of a file of a file element
	 * 
	 * @param repoId
	 * 
	 */
	public String createDeleteFileValueTopic(String repoId, String submodelId, String submodelElementId) {
		return new StringJoiner("/", "", "").add(SUBMODELREPOSITORY).add(repoId).add(SUBMODELS).add(encodeId(submodelId)).add(SUBMODELELEMENTS).add(submodelElementId).add(ATTACHMENT).add(DELETED).toString();
	}
	
	/**
	 * Creates the hierarchical topic for the update event of a file of a file element
	 * 
	 * @param repoId
	 * 
	 */
	public String createUpdateFileValueTopic(String repoId, String submodelId, String submodelElementId) {
		return new StringJoiner("/", "", "").add(SUBMODELREPOSITORY).add(repoId).add(SUBMODELS).add(encodeId(submodelId)).add(SUBMODELELEMENTS).add(submodelElementId).add(ATTACHMENT).add(UPDATED).toString();
	}
}
