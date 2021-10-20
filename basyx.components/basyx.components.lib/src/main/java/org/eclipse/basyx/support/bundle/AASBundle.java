/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.support.bundle;

import java.util.Set;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;

/**
 * Helper class to bundle an AAS with its corresponding submodels, e.g. for
 * passing them to a server environment
 * 
 * @author schnicke
 *
 */
public class AASBundle {
	private IAssetAdministrationShell aas;
	private Set<ISubmodel> submodels;

	public AASBundle(IAssetAdministrationShell aas, Set<ISubmodel> submodels) {
		super();
		this.aas = aas;
		this.submodels = submodels;
	}

	public IAssetAdministrationShell getAAS() {
		return aas;
	}

	public Set<ISubmodel> getSubmodels() {
		return submodels;
	}

}
