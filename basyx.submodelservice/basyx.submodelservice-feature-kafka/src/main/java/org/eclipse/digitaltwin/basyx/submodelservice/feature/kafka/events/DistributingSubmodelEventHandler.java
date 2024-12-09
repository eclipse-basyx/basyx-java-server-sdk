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
package org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.model.SubmodelEvent;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.model.SubmodelEventType;
/**
 * @author geso02 (Sonnenberg DFKI GmbH)
 */
public class DistributingSubmodelEventHandler implements SubmodelEventHandler {

	private final SubmodelEventDistributer evtDistributer;
	
	public DistributingSubmodelEventHandler(SubmodelEventDistributer evtDistributer) {
		this.evtDistributer = evtDistributer;
	}
	
	@Override
	public void onSubmodelCreated(Submodel submodel) {
		SubmodelEvent event = new SubmodelEvent();
		event.setType(SubmodelEventType.SM_CREATED);
		event.setId(submodel.getId());
		event.setSubmodel(submodel);
		evtDistributer.distribute(event);
	}
	
	@Override
	public void onSubmodelUpdated(Submodel submodel) {
		SubmodelEvent event = new SubmodelEvent();
		event.setType(SubmodelEventType.SM_UPDATED);
		event.setId(submodel.getId());
		event.setSubmodel(submodel);
		evtDistributer.distribute(event);
	}

	@Override
	public void onSubmodelDeleted(String id) {
		SubmodelEvent event = new SubmodelEvent();
		event.setType(SubmodelEventType.SM_DELETED);
		event.setId(id);
		evtDistributer.distribute(event);
	}

	@Override
	public void onSubmodelElementCreated(SubmodelElement smElement, String submodelId, String idShortPath) {
		SubmodelEvent event = new SubmodelEvent();
		event.setType(SubmodelEventType.SME_CREATED);
		event.setId(submodelId);
		event.setSmElement(smElement);
		event.setSmElementPath(idShortPath);
		evtDistributer.distribute(event);
	}
	
	@Override
	public void onSubmodelElementUpdated(SubmodelElement smElement, String submodelIdentifier, String idShortPath) {
		SubmodelEvent event = new SubmodelEvent();
		event.setType(SubmodelEventType.SME_UPDATED);
		event.setId(submodelIdentifier);
		event.setSmElement(smElement);
		event.setSmElementPath(idShortPath);
		evtDistributer.distribute(event);		
	}

	@Override
	public void onSubmodelElementDeleted(String submodelId, String idShortPath) {
		SubmodelEvent event = new SubmodelEvent();
		event.setType(SubmodelEventType.SME_DELETED);
		event.setId(submodelId);
		event.setSmElementPath(idShortPath);
		evtDistributer.distribute(event);
	}
}
