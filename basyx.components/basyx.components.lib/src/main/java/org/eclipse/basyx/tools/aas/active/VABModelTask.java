/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.tools.aas.active;

import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Abstract task that can be executed and works on an {@link IModelProvider}
 * 
 * @author espen, schnicke
 *
 */
public interface VABModelTask {
	/**
	 * Executes the task a single time on a passed model provider
	 * 
	 * @param model
	 *            the provider to be worked on
	 * @throws Exception
	 */
	public void execute(IModelProvider model) throws Exception;
}
