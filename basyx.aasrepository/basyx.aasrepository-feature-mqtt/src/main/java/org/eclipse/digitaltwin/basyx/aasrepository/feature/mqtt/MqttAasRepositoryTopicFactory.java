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
package org.eclipse.digitaltwin.basyx.aasrepository.feature.mqtt;

import java.util.StringJoiner;

import org.eclipse.digitaltwin.basyx.common.mqttcore.AbstractMqttTopicFactory;
import org.eclipse.digitaltwin.basyx.common.mqttcore.encoding.Encoder;

/**
 * A helper class containing methods that create topics used by the
 * AASAggregator.
 * 
 */
public class MqttAasRepositoryTopicFactory extends AbstractMqttTopicFactory {
	private static final String AASREPOSITORY = "aas-repository";
	private static final String SHELLS = "shells";
	private static final String SUBMODELS = "submodels";
	private static final String CREATED = "created";
	private static final String UPDATED = "updated";
	private static final String DELETED = "deleted";

	/**
	 * @param encoder
	 *            Used for encoding the aasId/submodelId
	 */
	public MqttAasRepositoryTopicFactory(Encoder encoder) {
		super(encoder);
	}

	/**
	 * Creates the hierarchical topic for the create event
	 * 
	 * @param repoId
	 * @return
	 */
	public String createCreateAASTopic(String repoId) {
		return new StringJoiner("/", "", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(CREATED)
				.toString();
	}
	
	/**
	 * Creates the hierarchical topic for the update event
	 * 
	 * @param repoId
	 * @return
	 */
	public String createUpdateAASTopic(String repoId) {
		return new StringJoiner("/", "", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(UPDATED)
				.toString();
	}
	
	/**
	 * Creates the hierarchical topic for the delete event
	 * 
	 * @param repoId
	 * @return
	 */
	public String createDeleteAASTopic(String repoId) {
		return new StringJoiner("/", "", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(DELETED)
				.toString();
	}
	
	/**
	 * Creates the hierarchical topic for the submodel reference create event
	 * 
	 * @param repoId
	 * @param referenceId
	 * @return
	 */
	public String createCreateAASSubmodelReferenceTopic(String repoId, String referenceId) {
		return new StringJoiner("/", "", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(SUBMODELS)
				.add(referenceId)
				.add(CREATED)
				.toString();
	}
	
	/**
	 * Creates the hierarchical topic for the submodel reference delete event
	 * 
	 * @param repoId
	 * @param referenceId
	 * @return
	 */
	public String createDeleteAASSubmodelReferenceTopic(String repoId, String referenceId) {
		return new StringJoiner("/", "", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(SUBMODELS)
				.add(referenceId)
				.add(DELETED)
				.toString();
	}
}
