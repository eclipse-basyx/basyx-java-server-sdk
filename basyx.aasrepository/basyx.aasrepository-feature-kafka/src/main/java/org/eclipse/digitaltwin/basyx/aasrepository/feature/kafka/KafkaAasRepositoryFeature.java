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
package org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka;

import org.eclipse.digitaltwin.basyx.aasrepository.AasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.AasRepositoryFeature;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events.AasEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
/**
 * @author geso02 (Sonnenberg DFKI GmbH)
 */
@ConditionalOnExpression("#{${" + KafkaAasRepositoryFeature.FEATURENAME + ".enabled:false} or ${basyx.feature.kafka.enabled:false}}")
@Component
public class KafkaAasRepositoryFeature implements AasRepositoryFeature {
	
	public final static String FEATURENAME = "basyx.aasrepository.feature.kafka";
	
	public final static String FEATURE_ENABLED_EXPRESSION = "#{${" + FEATURENAME + ".enabled:false} or ${basyx.feature.kafka.enabled:false}}"; 
	
	private final AasEventHandler evtHandler; 
	
	@Autowired
	public KafkaAasRepositoryFeature(AasEventHandler evtHandler) {
		this.evtHandler = evtHandler;
	}

	@Override
	public AasRepositoryFactory decorate(AasRepositoryFactory aasServiceFactory) {
		return new KafkaAasRepositoryFactory(aasServiceFactory, evtHandler);
	}

	@Override
	public void initialize() {
	}

	@Override
	public void cleanUp() {

	}

	@Override
	public String getName() {
		return "AasRepository KAFKA";
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
