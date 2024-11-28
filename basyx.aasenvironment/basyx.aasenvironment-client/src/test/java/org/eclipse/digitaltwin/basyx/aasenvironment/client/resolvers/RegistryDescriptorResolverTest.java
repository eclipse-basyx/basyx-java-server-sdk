/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasenvironment.client.resolvers;

import static org.junit.Assert.assertEquals;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.DummyAasEnvironmentComponent;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.TestFixture;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Tests for {@link AasDescriptorResolver}, {@link SubmodelDescriptorResolver}
 *
 * @author mateusmolina
 *
 */
public class RegistryDescriptorResolverTest {
	
	private static ConfigurableApplicationContext appContext;
	private static AasRepository aasRepository;
	private static SubmodelRepository smRepository;

	private static final String AAS_REPOSITORY_BASE_PATH = "http://localhost:8081";
	private static final String SM_REPOSITORY_BASE_PATH = "http://localhost:8081";

	private static final TestFixture FIXTURE = new TestFixture(AAS_REPOSITORY_BASE_PATH, SM_REPOSITORY_BASE_PATH);

	@BeforeClass
	public static void initApplication() {
		appContext = new SpringApplication(DummyAasEnvironmentComponent.class).run(new String[] {});

		aasRepository = appContext.getBean(AasRepository.class);
		smRepository = appContext.getBean(SubmodelRepository.class);
		aasRepository.createAas(FIXTURE.buildAasPre1());
		smRepository.createSubmodel(FIXTURE.buildSmPre1());
	}
	
	@AfterClass
	public static void cleanUp() {
		appContext.close();
	}

	@Test
	public void resolveAasDescriptor() {
		AasDescriptorResolver resolver = new AasDescriptorResolver(new EndpointResolver());
		
		AssetAdministrationShell expectedAas = FIXTURE.buildAasPre1();

		AssetAdministrationShell actualAas = resolver.resolveDescriptor(FIXTURE.buildAasPre1Descriptor()).getAAS();

		assertEquals(expectedAas, actualAas);
	}

	@Test
	public void resolveAasDescriptor_withMultipleInterfaces() {
		AasDescriptorResolver resolver = new AasDescriptorResolver(new EndpointResolver());

		AssetAdministrationShell expectedAas = FIXTURE.buildAasPre1();

		AssetAdministrationShell actualAas = resolver.resolveDescriptor(FIXTURE.buildAasPre1Descriptor_withMultipleInterfaces()).getAAS();

		assertEquals(expectedAas, actualAas);
	}

	@Test
	public void resolveSmDescriptor() {
		SubmodelDescriptorResolver resolver = new SubmodelDescriptorResolver(new EndpointResolver());

		Submodel expectedSm = FIXTURE.buildSmPre1();
		
		Submodel actualSm = resolver.resolveDescriptor(FIXTURE.buildSmPre1Descriptor()).getSubmodel();

		assertEquals(expectedSm, actualSm);
	}

	@Test
	public void resolveSmDescriptor_withMultipleInterfaces() {
		SubmodelDescriptorResolver resolver = new SubmodelDescriptorResolver(new EndpointResolver());

		Submodel expectedSm = FIXTURE.buildSmPre1();

		Submodel actualSm = resolver.resolveDescriptor(FIXTURE.buildSmPre1Descriptor_withMultipleInterfaces()).getSubmodel();

		assertEquals(expectedSm, actualSm);
	}
}
