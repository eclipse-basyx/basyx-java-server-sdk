/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.support.processengine.aas;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;

public class DeviceAdministrationShellFactory {

	public AssetAdministrationShell create(String aasid, String submodelid) {
		// create the aas, add submodel to aas using VABMultiSubmodelProvider
		IIdentifier id = new Identifier(IdentifierType.CUSTOM, submodelid);
		Submodel sm = new Submodel();
		sm.setIdentification(id.getIdType(), id.getId());
		sm.setIdShort("smIdShort");
		AssetAdministrationShell aas = new AssetAdministrationShell(aasid, new Identifier(IdentifierType.CUSTOM, aasid + "Id"), new Asset("assetId", new Identifier(IdentifierType.CUSTOM, aasid + "assetId"), AssetKind.INSTANCE));
		aas.addSubmodel(sm);

		return aas;
	}
}
