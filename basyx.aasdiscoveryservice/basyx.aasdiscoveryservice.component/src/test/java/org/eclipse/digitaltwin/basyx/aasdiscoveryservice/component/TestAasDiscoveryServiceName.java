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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.component;

import static org.junit.Assert.assertEquals;

import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 
 * Test the configuration of the {@link AasDiscoveryService} name
 *
 * @author danish
 *
 */
public class TestAasDiscoveryServiceName {
	private static final String CONFIGURED_AAS_DISC_SERV_NAME = "configured-aas-disc-serv-name";
	private static final String BASYX_AAS_DISC_SERV_NAME_KEY = "basyx.aasdiscoveryservice.name";

	private static ConfigurableApplicationContext appContext;

	public void startContext() {
		appContext = new SpringApplication(AasDiscoveryServiceComponent.class).run(new String[] {});
	}

	public static void closeContext() {
		appContext.close();
	}

	@Test
	public void getDefaultServiceName() {
		startContext();

		AasDiscoveryService service = appContext.getBean(AasDiscoveryService.class);

		assertEquals("aas-discovery-service", service.getName());

		closeContext();
	}

	@Test
	public void getConfiguredServiceName() {
		configureServiceNamePropertyAndStartContext();

		AasDiscoveryService service = appContext.getBean(AasDiscoveryService.class);

		assertEquals(CONFIGURED_AAS_DISC_SERV_NAME, service.getName());

		resetServiceNamePropertyAndCloseContext();
	}

	private void resetServiceNamePropertyAndCloseContext() {
		System.clearProperty(BASYX_AAS_DISC_SERV_NAME_KEY);

		closeContext();
	}

	private void configureServiceNamePropertyAndStartContext() {
		System.setProperty(BASYX_AAS_DISC_SERV_NAME_KEY, CONFIGURED_AAS_DISC_SERV_NAME);

		startContext();
	}

}
