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
package org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.SubmodelEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnExpression(KafkaSubmodelServiceFeature.FEATURE_ENABLED_EXPRESSION)
@ConditionalOnProperty(name = KafkaSubmodelServiceApplicationListener.SUBMODEL_EVENTS_ACTIVATED, havingValue = "true", matchIfMissing = false)
public class KafkaSubmodelServiceApplicationListener  implements ApplicationListener<ApplicationEvent> {
	
	public static final String SUBMODEL_EVENTS_ACTIVATED = KafkaSubmodelServiceFeature.FEATURENAME + ".submodelevents";
	
	private final SubmodelEventHandler handler;
	private final Submodel submodel;
	
	@Autowired
	private KafkaSubmodelServiceApplicationListener(SubmodelEventHandler handler, Submodel submodel) {
		this.handler = handler;
		this.submodel = submodel;
	}
	
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
    	// only fired if submodelEvents are active
    	if (event instanceof ApplicationReadyEvent) {
    		handler.onSubmodelCreated(submodel);
    	} else if (event instanceof ContextClosedEvent) {
    		handler.onSubmodelDeleted(submodel.getId());
    	}
    }
}
