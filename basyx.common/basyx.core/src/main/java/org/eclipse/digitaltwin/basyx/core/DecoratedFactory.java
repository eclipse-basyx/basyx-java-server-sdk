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

import java.util.List;

/**
 * Base factory class for all component feature decoration.
 * 
 * @author schnicke
 *
 * @param <FactoryToDecorate>
 *            the component type factory to decorate
 * @param <Feature>
 *            the component feature type to utilize for decoration
 */
public class DecoratedFactory<FactoryToDecorate, Feature extends BaSyxFeature<FactoryToDecorate>> {
	private FactoryToDecorate decorated;

	/**
	 * Initializes the factory by decorating the passed component with all passed
	 * features that are enabled. Additionally, all passed and enabled features are
	 * initialized
	 * 
	 * @param toDecorate
	 * @param features
	 */
	public DecoratedFactory(FactoryToDecorate toDecorate, List<Feature> features) {
		decorated = toDecorate;

		for (Feature feature : features) {
			decorated = handleFeature(decorated, feature);
		}
	}

	private FactoryToDecorate handleFeature(FactoryToDecorate toDecorate, Feature feature) {
		if (!feature.isEnabled())
			return toDecorate;

		feature.initialize();
		return feature.decorate(toDecorate);
	}

	/**
	 * 
	 * @return the decorated factory
	 */
	protected FactoryToDecorate getDecorated() {
		return decorated;
	}
}
