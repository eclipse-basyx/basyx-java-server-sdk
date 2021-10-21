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
 * @deprecated Moved to SDK
 * @author schnicke
 *
 */
@Deprecated
public class AASBundle extends org.eclipse.basyx.aas.bundle.AASBundle {

	public AASBundle(IAssetAdministrationShell aas, Set<ISubmodel> submodels) {
		super(aas, submodels);
	}
}
