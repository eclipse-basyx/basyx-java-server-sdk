/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.configuration.builder;

import org.eclipse.basyx.components.configuration.ConfigurableComponent;




/**
 * Base class for component configuration builders
 * 
 * If the end() operation of a component configuration builder is invoked, and no sufficient configuration is provided, the component will
 * throw an InsufficientConfigurationDataException.
 * 
 * @author kuhn
 *
 */
public abstract class BaSyxConfigurationBuilder<ParentBuilderType> {

	
	/**
	 * Configured component
	 */
	@SuppressWarnings("rawtypes")
	protected ConfigurableComponent configuredComponent = null;
	
	
	/**
	 * Parent builder
	 */
	protected ParentBuilderType parentBuilder = null;
	
	
	
	/**
	 * End configuration 
	 * 
	 * This base implementation invokes the {@literal <<<>>>} operation of the configured component if configured 
	 * component is not null. Only set the configured component for the top level builder, not for
	 * nested builders.
	 */
	@SuppressWarnings("unchecked")
	public ParentBuilderType end() {
		// Null pointer check
		// - If configured component is null, this is the case for nested builders, parent builder
		//   must not be null.
		if (configuredComponent == null) return parentBuilder;
		
		// Configure configured component
		configuredComponent.configureComponent(this);
		
		// Return null - if a configured component is set, no parent builder is set
		return null;
	}
	
	
	/**
	 * Set reference to configured component
	 */
	public void setConfiguredComponent(ConfigurableComponent<?> component) {
		configuredComponent = component;
	}
	
	
	/**
	 * Set parent builder
	 */
	public void setParentBuilder(ParentBuilderType parentBldr) {
		parentBuilder = parentBldr;
	}
}
