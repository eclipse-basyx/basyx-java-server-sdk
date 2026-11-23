/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.SpringApplication;

/**
 * Runs the {@link ConnectedAasEnvironmentTestSuite} against an unsecured AAS
 * Environment
 *
 */
public class TestConnectedAasEnvironment extends ConnectedAasEnvironmentTestSuite {

	@BeforeAll
	public static void initApplication() throws FileNotFoundException, IOException {
		appContext = new SpringApplication(DummyAasEnvironmentComponent.class).run(new String[] {});		

		aasRepository = appContext.getBean(AasRepository.class);
		submodelRepository = appContext.getBean(SubmodelRepository.class);
		conceptDescriptionRepository = appContext.getBean(ConceptDescriptionRepository.class);
	
		for (Submodel submodel : createDummySubmodels()) {
			submodelRepository.createSubmodel(submodel);
		}

		for (AssetAdministrationShell shell : createDummyShells()) {
			aasRepository.createAas(shell);
		}
		
		for (ConceptDescription cd : createDummyConceptDescriptions()) {
			conceptDescriptionRepository.createConceptDescription(cd);
		}
	}

	@AfterAll
	public static void cleanUpContext() {
		appContext.close();
	}
}
