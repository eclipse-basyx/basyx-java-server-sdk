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

package org.eclipse.digitaltwin.basyx.aasenvironment.preconfiguration;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.basyx.aasenvironment.AasEnvironment;
import org.eclipse.digitaltwin.basyx.core.exceptions.ZipBombException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Initializer for the preconfiguration loader
 *
 * @author espen, mateusmolina
 *
 */
@Component
public class PreconfigurationLoaderInitializer implements InitializingBean {

	private AasEnvironment aasEnvironment;

	private AasEnvironmentPreconfigurationLoader preconfigurationLoader;

	@Autowired
	public PreconfigurationLoaderInitializer(AasEnvironment aasEnvironment,
			AasEnvironmentPreconfigurationLoader preconfigurationLoader) {
		super();
		this.aasEnvironment = aasEnvironment;
		this.preconfigurationLoader = preconfigurationLoader;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		loadPreconfiguredEnvironment();
	}

	private void loadPreconfiguredEnvironment() throws IOException, InvalidFormatException, DeserializationException, ZipBombException {
		if (!preconfigurationLoader.shouldLoadPreconfiguredEnvironment()) {
			return;
		}

		preconfigurationLoader.loadPreconfiguredEnvironments(aasEnvironment);
	}

}
