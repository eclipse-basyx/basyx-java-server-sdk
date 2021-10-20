/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components;

/**
 * Common interfaces for all components allowing starting/stopping the component
 * 
 * @author schnicke
 *
 */
public interface IComponent {

	/**
	 * Starts the component
	 */
	public void startComponent();

	/**
	 * Shuts down the component
	 */
	public void stopComponent();
}
