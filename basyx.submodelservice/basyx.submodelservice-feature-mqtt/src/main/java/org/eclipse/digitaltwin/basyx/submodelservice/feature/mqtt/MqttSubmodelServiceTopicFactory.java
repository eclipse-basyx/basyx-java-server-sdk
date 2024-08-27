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
	private static final String SUBMODEL_ELEMENTS = "submodelElements";

	/**
	 * Used for encoding the idShort
	 * 
	 * @param encoder
	 */
	public MqttSubmodelServiceTopicFactory(Encoder encoder) {
		super(encoder);
	}

	/**
	 * Creates the hierarchical topic for the create event of submodelElements
	 * 
	 * @param idShort
	 */
	public String createCreateSubmodelElementTopic(String idShort) {
		return new StringJoiner("/", "", "").add(SUBMODEL_SERVICE).add(SUBMODELS).add(SUBMODEL_ELEMENTS).add(idShort).add(CREATED).toString();
	}

	/**
	 * Creates the hierarchical topic for the update event of submodelElements
	 * 
	 * @param idShort
	 */
	public String createUpdateSubmodelElementTopic(String idShort) {

		return new StringJoiner("/", "", "").add(SUBMODEL_SERVICE).add(SUBMODELS).add(SUBMODEL_ELEMENTS).add(idShort).add(UPDATED).toString();
	}

	/**
	 * Creates the hierarchical topic for the delete event of submodelElements
	 * 
	 * @param idShort
	 */
	public String createDeleteSubmodelElementTopic(String idShort) {
		return new StringJoiner("/", "", "").add(SUBMODEL_SERVICE).add(SUBMODELS).add(SUBMODEL_ELEMENTS).add(idShort).add(DELETED).toString();
	}
}
