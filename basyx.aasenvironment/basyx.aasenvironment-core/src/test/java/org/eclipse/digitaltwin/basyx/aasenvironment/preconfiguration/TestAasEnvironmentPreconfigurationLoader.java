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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultConceptDescription;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.InMemoryAasRepository;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasServiceFactory;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.InMemoryConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.InMemorySubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceHelper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

public class TestAasEnvironmentPreconfigurationLoader {
	private AasRepository actualAasRepository;
	private SubmodelRepository actualSubmodelRepository;
	private ConceptDescriptionRepository actualConceptDescriptionRepository;

	private AasRepository expectedAasRepository;
	private SubmodelRepository expectedSubmodelRepository;
	private ConceptDescriptionRepository expectetdConceptDescriptionRepository;

	public static final String AAS_TECHNICAL_DATA_ID = "shell001";
	public static final String AAS_OPERATIONAL_DATA_ID = "shell002";
	public static final String SUBMODEL_TECHNICAL_DATA_ID = "7A7104BDAB57E184";
	public static final String CONCEPT_DESCRIPTION_ID_NOT_INCLUDED_IN_ENV = "IdNotToBeIncludedInSerializedEnv";
	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, "");

	@Before
	public void setup() {
		actualSubmodelRepository = new InMemorySubmodelRepository(new InMemorySubmodelServiceFactory());
		actualAasRepository = new InMemoryAasRepository(new InMemoryAasServiceFactory());

		actualConceptDescriptionRepository = new InMemoryConceptDescriptionRepository();

	}

	private static Collection<Submodel> createDummySubmodels() {
		Collection<Submodel> submodels = new ArrayList<>();
		submodels.add(DummySubmodelFactory.createOperationalDataSubmodel());
		submodels.add(DummySubmodelFactory.createTechnicalDataSubmodel());
		return submodels;
	}

	private static Collection<AssetAdministrationShell> createDummyShells() {
		AssetAdministrationShell shell1 = new DefaultAssetAdministrationShell.Builder().id(AAS_TECHNICAL_DATA_ID).idShort(AAS_TECHNICAL_DATA_ID)
				.assetInformation(new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE).globalAssetID(SUBMODEL_TECHNICAL_DATA_ID).build()).build();

		AssetAdministrationShell shell2 = new DefaultAssetAdministrationShell.Builder().id(AAS_OPERATIONAL_DATA_ID).idShort(AAS_OPERATIONAL_DATA_ID)
				.assetInformation(new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE).globalAssetID(SUBMODEL_TECHNICAL_DATA_ID).build()).build();
		Collection<AssetAdministrationShell> shells = new ArrayList<>();
		shells.add(shell1);
		shells.add(shell2);
		return shells;
	}

	public static Collection<ConceptDescription> createDummyConceptDescriptions() {
		Collection<ConceptDescription> conceptDescriptions = new ArrayList<>();

		conceptDescriptions.add(new DefaultConceptDescription.Builder().id(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_PROPERTY).build());
		conceptDescriptions.add(new DefaultConceptDescription.Builder().id(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_SEMANTIC_ID_PROPERTY).build());
		conceptDescriptions.add(new DefaultConceptDescription.Builder().id(CONCEPT_DESCRIPTION_ID_NOT_INCLUDED_IN_ENV).build());

		return conceptDescriptions;
	}

	@Test
	public void TestloadEnvironmentFromFolder() {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		String folder = "./environments";
		AasEnvironmentPreconfigurationLoader peconfigloader = new AasEnvironmentPreconfigurationLoader(resourceLoader, folder);
		peconfigloader.loadEnvironmentFromFolder(actualAasRepository, actualSubmodelRepository, actualConceptDescriptionRepository);
		assertJSONEnvironment();

	}

	private void assertJSONEnvironment() {
		assertTrue(actualAasRepository.getAllAas(NO_LIMIT_PAGINATION_INFO).getResult().containsAll(createDummyShells()));
		assertTrue(actualSubmodelRepository.getAllSubmodels(NO_LIMIT_PAGINATION_INFO).getResult().containsAll(createDummySubmodels()));
		assertTrue(actualConceptDescriptionRepository.getAllConceptDescriptions(NO_LIMIT_PAGINATION_INFO).getResult().containsAll(createDummyConceptDescriptions()));
	}

}
