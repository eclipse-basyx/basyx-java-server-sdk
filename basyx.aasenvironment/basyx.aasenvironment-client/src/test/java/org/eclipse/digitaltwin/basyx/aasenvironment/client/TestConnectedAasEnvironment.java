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

package org.eclipse.digitaltwin.basyx.aasenvironment.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasenvironment.AASEnvironmentSerializationTestSuite;
import org.eclipse.digitaltwin.basyx.aasenvironment.AasEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.TestAASEnvironmentSerialization;
import org.eclipse.digitaltwin.basyx.aasenvironment.base.DefaultAASEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.internal.SerializationApi;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.SimpleAasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.SimpleConceptDescriptionRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.inmemory.AasInMemoryBackendProvider;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasServiceFactory;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionInMemoryBackendProvider;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncoder;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelInMemoryBackendProvider;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.SimpleSubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;

public class TestConnectedAasEnvironment extends AASEnvironmentSerializationTestSuite  {
	protected static final String ENVIRONMENT_BASE_PATH = "http://localhost:8081";

	protected SerializationApi serializationApi;
	
	protected ConnectedAasEnvironment connectedAasEnvironment;
	
	protected static ConfigurableApplicationContext appContext;
	
	private AasEnvironment aasEnvironment;
	private static AasRepository aasRepository;
	private static SubmodelRepository submodelRepository;
	private static ConceptDescriptionRepository conceptDescriptionRepository;
	
	@BeforeClass
	public static void initApplication() {
		appContext = new SpringApplication(DummyAasEnvironmentComponent.class).run(new String[] {});

		aasRepository = appContext.getBean(AasRepository.class);
		submodelRepository = appContext.getBean(SubmodelRepository.class);
		conceptDescriptionRepository = appContext.getBean(ConceptDescriptionRepository.class);
	}

	@AfterClass
	public static void cleanUpContext() {
		appContext.close();
	}
	
	@Before
	public void setup() {
		for (Submodel submodel : createDummySubmodels()) {
			submodelRepository.createSubmodel(submodel);
		}

		for (AssetAdministrationShell shell : createDummyShells()) {
			aasRepository.createAas(shell);
		}

		aasEnvironment = new DefaultAASEnvironment(aasRepository, submodelRepository, conceptDescriptionRepository);
	
		this.serializationApi = getSerializationApi();
		aasEnvironment = new ConnectedAasEnvironment(this.serializationApi);
	}
	
	protected SerializationApi getSerializationApi() {
		return spy(new SerializationApi(ENVIRONMENT_BASE_PATH));
	}
	
	@Override
	public AasEnvironment getAasEnvironment() {
		return this.aasEnvironment;
	}

	@Override
	public AasRepository getAasRepository() {
		return this.aasRepository;
	}

	@Override
	public SubmodelRepository getSubmodelRepository() {
		return this.submodelRepository;
	}

	@Override
	public ConceptDescriptionRepository getConceptDescriptionRepository() {
		return this.conceptDescriptionRepository;
	}
    
}
