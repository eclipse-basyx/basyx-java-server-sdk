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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.AASXDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.InMemoryFile;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.aasenvironment.AasEnvironment;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceHelper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.xml.sax.SAXException;

public class TestConnectedAasEnvironment {	
	public static final String AAS_TECHNICAL_DATA_ID = "shell001";
	public static final String AAS_OPERATIONAL_DATA_ID = "shell002";
	public static final String SUBMODEL_TECHNICAL_DATA_ID = "7A7104BDAB57E184";
	public static final String SUBMODEL_OPERATIONAL_DATA_ID = "AC69B1CB44F07935";
	public static final String CONCEPT_DESCRIPTION_ID_NOT_INCLUDED_IN_ENV = "IdNotToBeIncludedInSerializedEnv";
	
	protected ConnectedAasEnvironment connectedAasEnvironment;
	
	protected static ConfigurableApplicationContext appContext;
	
	private AasEnvironment aasEnvironment;
	private static AasRepository aasRepository;
	private static SubmodelRepository submodelRepository;
	private static ConceptDescriptionRepository conceptDescriptionRepository;
	
	@BeforeClass
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

	@AfterClass
	public static void cleanUpContext() {
		appContext.close();
	}
	
	@Before
	public void setup() throws IOException {
		aasEnvironment = new ConnectedAasEnvironment();
	}
	
	public AasEnvironment getAasEnvironment() {
		return this.aasEnvironment;
	}

	public AasRepository getAasRepository() {
		return TestConnectedAasEnvironment.aasRepository;
	}

	public SubmodelRepository getSubmodelRepository() {
		return TestConnectedAasEnvironment.submodelRepository;
	}

	public ConceptDescriptionRepository getConceptDescriptionRepository() {
		return TestConnectedAasEnvironment.conceptDescriptionRepository;
	}
	
	public static Collection<Submodel> createDummySubmodels() {
		Collection<Submodel> submodels = new ArrayList<>();
		submodels.add(DummySubmodelFactory.createOperationalDataSubmodel());
		submodels.add(DummySubmodelFactory.createTechnicalDataSubmodel());
		return submodels;
	}

	public static Collection<AssetAdministrationShell> createDummyShells() {
		AssetAdministrationShell shell1 = new DefaultAssetAdministrationShell.Builder().id(AAS_TECHNICAL_DATA_ID).idShort(AAS_TECHNICAL_DATA_ID)
				.assetInformation(new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE).globalAssetId(SUBMODEL_TECHNICAL_DATA_ID).build()).build();

		AssetAdministrationShell shell2 = new DefaultAssetAdministrationShell.Builder().id(AAS_OPERATIONAL_DATA_ID).idShort(AAS_OPERATIONAL_DATA_ID)
				.assetInformation(new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE).globalAssetId(SUBMODEL_TECHNICAL_DATA_ID).build()).build();
		Collection<AssetAdministrationShell> shells = new ArrayList<>();
		shells.add(shell1);
		shells.add(shell2);
		return shells;
	}

	@Test
	public void testAASEnviromentSerializationWithJSON() throws SerializationException, IOException, DeserializationException {
		boolean includeConceptDescription = true;
		boolean aasIdsIncluded = true;
		boolean submodelIdsIncluded = true;

		String jsonSerialization = getAasEnvironment().createJSONAASEnvironmentSerialization(getShellIds(createDummyShells()), getSubmodelIds(createDummySubmodels()), includeConceptDescription);
		validateJSON(jsonSerialization, aasIdsIncluded, submodelIdsIncluded, includeConceptDescription);

		validateRepositoriesState();
	}

	@Test
	public void testAASEnviromentSerializationWithXML() throws SerializationException, IOException, SAXException, DeserializationException {
		boolean includeConceptDescription = true;
		boolean aasIdsIncluded = true;
		boolean submodelIdsIncluded = true;

		String xmlSerialization = getAasEnvironment().createXMLAASEnvironmentSerialization(getShellIds(createDummyShells()), getSubmodelIds(createDummySubmodels()), includeConceptDescription);
		validateXml(xmlSerialization, aasIdsIncluded, submodelIdsIncluded, includeConceptDescription);

		validateRepositoriesState();
	}

	@Test
	public void testAASEnviromentSerializationWithAASX() throws SerializationException, IOException, InvalidOperationException, InvalidFormatException, DeserializationException {
		boolean includeConceptDescription = true;
		boolean aasIdsIncluded = true;
		boolean submodelIdsIncluded = true;

		byte[] serialization = getAasEnvironment().createAASXAASEnvironmentSerialization(getShellIds(createDummyShells()), getSubmodelIds(createDummySubmodels()), includeConceptDescription);
		checkAASX(new ByteArrayInputStream(serialization), aasIdsIncluded, submodelIdsIncluded, includeConceptDescription);

		validateRepositoriesState();
	}

	@Test
	public void testAASEnviromentSerializationWithJSONExcludeCD() throws SerializationException, IOException, DeserializationException {
		boolean includeConceptDescription = false;
		boolean aasIdsIncluded = true;
		boolean submodelIdsIncluded = true;

		String jsonSerialization = getAasEnvironment().createJSONAASEnvironmentSerialization(getShellIds(createDummyShells()), getSubmodelIds(createDummySubmodels()), includeConceptDescription);
		validateJSON(jsonSerialization, aasIdsIncluded, submodelIdsIncluded, includeConceptDescription);

		validateRepositoriesState();
	}

	public static void validateJSON(String actual, boolean areAASsIncluded, boolean areSubmodelsIncluded, boolean includeConceptDescription) throws DeserializationException {
		JsonDeserializer jsonDeserializer = new JsonDeserializer();
		Environment aasEnvironment = jsonDeserializer.read(actual, Environment.class);
		checkAASEnvironment(aasEnvironment, areAASsIncluded, areSubmodelsIncluded, includeConceptDescription);
	}

	public static void validateXml(String actual, boolean areAASsIncluded, boolean areSubmodelsIncluded, boolean includeConceptDescription) throws DeserializationException {
		XmlDeserializer xmlDeserializer = new XmlDeserializer();
		Environment aasEnvironment = xmlDeserializer.read(actual);

		checkAASEnvironment(aasEnvironment, areAASsIncluded, areSubmodelsIncluded, includeConceptDescription);
	}

	public static void checkAASX(InputStream inputStream, boolean areAASsIncluded, boolean areSubmodelsIncluded, boolean includeConceptDescription) throws IOException, InvalidFormatException, DeserializationException {
		AASXDeserializer aasxDeserializer = new AASXDeserializer(inputStream);
		Environment environment = aasxDeserializer.read();
		checkAASEnvironment(environment, areAASsIncluded, areSubmodelsIncluded, includeConceptDescription);
		inputStream.close();
	}

	public static void checkAASXFiles(InputStream inputStream) throws IOException, InvalidFormatException, DeserializationException {
		AASXDeserializer aasxDeserializer = new AASXDeserializer(inputStream);
		List<InMemoryFile> files = aasxDeserializer.getRelatedFiles();
		assertEquals(2,files.size());
	}

	public static Collection<ConceptDescription> createDummyConceptDescriptions() {
		Collection<ConceptDescription> conceptDescriptions = new ArrayList<>();

		conceptDescriptions.add(new DefaultConceptDescription.Builder().id(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_PROPERTY).build());
		conceptDescriptions.add(new DefaultConceptDescription.Builder().id(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_SEMANTIC_ID_PROPERTY).build());
		conceptDescriptions.add(new DefaultConceptDescription.Builder().id(CONCEPT_DESCRIPTION_ID_NOT_INCLUDED_IN_ENV).build());

		return conceptDescriptions;
	}

	private static void checkAASEnvironment(Environment aasEnvironment, boolean areAASsIncluded, boolean areSubmodelsIncluded, boolean areConceptDescriptionsIncluded) {
		if (areAASsIncluded)
			assertAasIds(aasEnvironment);

		if (areSubmodelsIncluded)
			assertSubmodelIds(aasEnvironment);

		if (areConceptDescriptionsIncluded)
			assertConceptDescriptionIds(aasEnvironment);
	}

	private static void assertConceptDescriptionIds(Environment aasEnvironment) {
		List<String> conceptDescriptionIds = retrieveConceptDescriptionIds(aasEnvironment);
		assertTrue(conceptDescriptionIds.contains(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_PROPERTY));
		assertTrue(conceptDescriptionIds.contains(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_SEMANTIC_ID_PROPERTY));
		assertFalse(conceptDescriptionIds.contains(CONCEPT_DESCRIPTION_ID_NOT_INCLUDED_IN_ENV));
	}

	private static void assertSubmodelIds(Environment aasEnvironment) {
		List<String> submodelIds = retrieveSubmodelIds(aasEnvironment);
		assertTrue(submodelIds.contains(SUBMODEL_OPERATIONAL_DATA_ID));
		assertTrue(submodelIds.contains(SUBMODEL_TECHNICAL_DATA_ID));
	}

	private static void assertAasIds(Environment aasEnvironment) {
		List<String> aasIds = retrieveShellIds(aasEnvironment);
		assertTrue(aasIds.contains(AAS_TECHNICAL_DATA_ID));
		assertTrue(aasIds.contains(AAS_OPERATIONAL_DATA_ID));
	}

	private List<String> getSubmodelIds(Collection<Submodel> submodels) {
		return submodels.stream().map(sm -> ((DefaultSubmodel) sm).getId()).collect(Collectors.toList());
	}

	private List<String> getShellIds(Collection<AssetAdministrationShell> shells) {
		return shells.stream().map(shell -> ((DefaultAssetAdministrationShell) shell).getId()).collect(Collectors.toList());
	}

	private static List<String> retrieveSubmodelIds(Environment aasEnvironment) {
		List<String> submodelIds = new ArrayList<>();
		aasEnvironment.getSubmodels().forEach(s -> {
			submodelIds.add(((DefaultSubmodel) s).getId());
		});
		return submodelIds;
	}

	private static List<String> retrieveShellIds(Environment aasEnvironment) {
		List<String> aasIds = new ArrayList<>();

		aasEnvironment.getAssetAdministrationShells().forEach(a -> {
			aasIds.add(((DefaultAssetAdministrationShell) a).getId());
		});
		return aasIds;
	}

	private static List<String> retrieveConceptDescriptionIds(Environment aasEnvironment) {
		return aasEnvironment.getConceptDescriptions().stream().map(cd -> cd.getId()).collect(Collectors.toList());
	}

	private void validateRepositoriesState() {
		assertTrue(getAasRepository().getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().containsAll(createDummyShells()));
		assertTrue(getSubmodelRepository().getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().containsAll(createDummySubmodels()));
		assertTrue(getConceptDescriptionRepository().getAllConceptDescriptions(PaginationInfo.NO_LIMIT).getResult().containsAll(createDummyConceptDescriptions()));
	}    
}
