/*******************************************************************************
 * Copyright (C) 2024 DFKI GmbH (https://www.dfki.de/en/web)
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
 * 
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.submodelrepository.feature.kafka;

import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.SubmodelRepositoryFeature;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.SubmodelEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * @author geso02 (Sonnenberg DFKI GmbH)
 */
@ConditionalOnExpression("#{${" + KafkaSubmodelRepositoryFeature.FEATURENAME + ".enabled:false} or ${basyx.feature.kafka.enabled:false}}")
@Component
public class KafkaSubmodelRepositoryFeature implements SubmodelRepositoryFeature {
	
	public final static String FEATURENAME = "basyx.submodelrepository.feature.kafka";

	public final static String FEATURE_ENABLED_EXPRESSION = "#{${" + FEATURENAME + ".enabled:false} or ${basyx.feature.kafka.enabled:false}}"; 

	private final SubmodelEventHandler handler;
	
	@Autowired
	public KafkaSubmodelRepositoryFeature(SubmodelEventHandler handler) {
		this.handler = handler;
	}

	@Override
	public SubmodelRepositoryFactory decorate(SubmodelRepositoryFactory factory) {
		return new KafkaSubmodelRepositoryFactory(factory, handler);
	}

	@Override
	public void initialize() {
	}

	@Override
	public void cleanUp() {

	}

	@Override
	public String getName() {
		return "SubmodelRepository Kafka";
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
