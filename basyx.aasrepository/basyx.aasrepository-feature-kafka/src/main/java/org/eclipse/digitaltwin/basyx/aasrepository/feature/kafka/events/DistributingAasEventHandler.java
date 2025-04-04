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
package org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events.model.AasEvent;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events.model.AasEventType;
/**
 * @author geso02 (Sonnenberg DFKI GmbH)
 */
public class DistributingAasEventHandler implements AasEventHandler {

	private final AasEventDistributer evtDistributer;
	
	
	public DistributingAasEventHandler(AasEventDistributer evtDistributer) {
		this.evtDistributer = evtDistributer;
	}
	
	@Override
	public void onAasCreated(AssetAdministrationShell aas) {
		AasEvent event = new AasEvent();
		event.setType(AasEventType.AAS_CREATED);
		event.setId(aas.getId());
		event.setAas(aas);
		evtDistributer.distribute(event);
	}

	@Override
	public void onAasUpdated(String aasId, AssetAdministrationShell aas) {
		AasEvent event = new AasEvent();
		event.setType(AasEventType.AAS_UPDATED);
		event.setId(aasId);
		event.setAas(aas);
		evtDistributer.distribute(event);
	}

	@Override
	public void onAasDeleted(String aasId) {
		AasEvent event = new AasEvent();
		event.setType(AasEventType.AAS_DELETED);
		event.setId(aasId);
		evtDistributer.distribute(event);
	}

	@Override
	public void onSubmodelRefAdded(String aasId, Reference submodelReference) {
		AasEvent event = new AasEvent();
		event.setType(AasEventType.SM_REF_ADDED);
		event.setId(aasId);
		event.setSubmodelId(resolveSubmodelId(submodelReference));
		event.setReference(submodelReference);
		evtDistributer.distribute(event);
	}

	@Override
	public void onSubmodelRefDeleted(String aasId, String submodelId) {
		AasEvent event = new AasEvent();
		event.setType(AasEventType.SM_REF_DELETED);
		event.setId(aasId);
		event.setSubmodelId(submodelId);
		evtDistributer.distribute(event);
	}

	@Override
	public void onAssetInformationSet(String aasId, AssetInformation aasInfo) {
		AasEvent event = new AasEvent();
		event.setType(AasEventType.ASSET_INFORMATION_SET);
		event.setId(aasId);
		event.setAssetInformation(aasInfo);
		evtDistributer.distribute(event);
	}
	
	private String resolveSubmodelId(Reference submodelReference) {
		if (submodelReference == null) {
			return null;
		}
		if (submodelReference.getType() != ReferenceTypes.MODEL_REFERENCE) {
			return null;
		}
		List<Key> keys = submodelReference.getKeys();
		if (keys == null || keys.size() != 1) {
			return null;
		}
		Key key = keys.get(0);
		return key.getValue();
	}
}
