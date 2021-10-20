/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.configuration;

import org.eclipse.basyx.components.configuration.builder.BaSyxConfigurationBuilder;

/**
 * Base interface for configurable components
 * 
 * @author kuhn
 *
 */
public interface ConfigurableComponent<T extends BaSyxConfigurationBuilder<?>> {

	
	/**
	 * Create a component builder for this component that is used for configurating the component
	 */
	public T configure();
	
	
	/**
	 * Configure the component
	 */
	public void configureComponent(T configuration);
}
