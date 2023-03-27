/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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
 ******************************************************************************/


package org.eclipse.digitaltwin.basyx.core;

/**
 * Base interface for all features utilized by the BaSyx components
 * 
 * @author schnicke
 *
 * @param <ComponentFactory>
 *            the componentFactory type extended by this feature
 */
public interface BaSyxFeature<ComponentFactory> {

	/**
	 * Initializes the feature, e.g., by creating backend connections
	 */
	void initialize();

	/**
	 * Cleans up the feature, e.g, by shutting down all backend connections created
	 * in {@link #initialize()}
	 */
	void cleanUp();

	/**
	 * 
	 * @return the name of the feature
	 */
	String getName();

	/**
	 * 
	 * @return if this feature is enabled and should be utilized
	 */
	boolean isEnabled();

	/**
	 * Applies the feature to the component by decorating it
	 * 
	 * @param component
	 *            to be decorated
	 * @return decorated component
	 */
	ComponentFactory decorate(ComponentFactory component);
}
