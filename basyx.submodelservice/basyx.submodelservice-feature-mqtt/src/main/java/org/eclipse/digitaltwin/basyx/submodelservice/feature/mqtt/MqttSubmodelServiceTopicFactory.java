/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.submodelservice.feature.mqtt;

import java.util.StringJoiner;

import org.eclipse.digitaltwin.basyx.common.mqttcore.AbstractMqttTopicFactory;
import org.eclipse.digitaltwin.basyx.common.mqttcore.encoding.Encoder;

/**
 * MQTT topic factory for the eventing on the submodel level.
 * 
 * @author rana
 */
public class MqttSubmodelServiceTopicFactory extends AbstractMqttTopicFactory {

	private static final String SUBMODEL_SERVICE = "sm-service";
	private static final String SUBMODELS = "submodels";
	private static final String CREATED = "created";
	private static final String UPDATED = "updated";
	private static final String DELETED = "deleted";
	private static final String PATCHED = "patched";
	private static final String SUBMODEL_ELEMENTS = "submodelElements";
	private static final String VALUE = "value";
	private static final String ATTACHMENT = "attachment";

	/**
	 * Used for encoding the submodel identifier
	 * 
	 * @param encoder
	 */
	public MqttSubmodelServiceTopicFactory(Encoder encoder) {
		super(encoder);
	}

	/**
	 * Creates the hierarchical topic for the create event of submodelElements
	 * 
	 * @param submodelId
	 * @param idShortPath
	 */
	public String createCreateSubmodelElementTopic(String submodelId, String idShortPath) {
		return createElementTopic(submodelId, idShortPath).add(CREATED).toString();
	}

	/**
	 * Creates the hierarchical topic for the update event of submodelElements
	 * 
	 * @param submodelId
	 * @param idShortPath
	 */
	public String createUpdateSubmodelElementTopic(String submodelId, String idShortPath) {
		return createElementTopic(submodelId, idShortPath).add(UPDATED).toString();
	}

	public String createUpdateSubmodelElementValueTopic(String submodelId, String idShortPath) {
		return createElementTopic(submodelId, idShortPath).add(VALUE).add(UPDATED).toString();
	}

	/**
	 * Creates the hierarchical topic for the delete event of submodelElements
	 * 
	 * @param submodelId
	 * @param idShortPath
	 */
	public String createDeleteSubmodelElementTopic(String submodelId, String idShortPath) {
		return createElementTopic(submodelId, idShortPath).add(DELETED).toString();
	}

	public String createPatchSubmodelElementsTopic(String submodelId) {
		return createSubmodelTopic(submodelId).add(SUBMODEL_ELEMENTS).add(PATCHED).toString();
	}

	public String createUpdateFileValueTopic(String submodelId, String idShortPath) {
		return createElementTopic(submodelId, idShortPath).add(ATTACHMENT).add(UPDATED).toString();
	}

	public String createDeleteFileValueTopic(String submodelId, String idShortPath) {
		return createElementTopic(submodelId, idShortPath).add(ATTACHMENT).add(DELETED).toString();
	}

	private StringJoiner createElementTopic(String submodelId, String idShortPath) {
		return createSubmodelTopic(submodelId).add(SUBMODEL_ELEMENTS).add(idShortPath);
	}

	private StringJoiner createSubmodelTopic(String submodelId) {
		return new StringJoiner("/", "", "").add(SUBMODEL_SERVICE).add(SUBMODELS).add(encodeId(submodelId));
	}
}
