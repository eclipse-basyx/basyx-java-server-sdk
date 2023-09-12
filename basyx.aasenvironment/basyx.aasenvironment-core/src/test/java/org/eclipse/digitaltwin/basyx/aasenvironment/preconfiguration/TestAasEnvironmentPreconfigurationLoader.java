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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.aasenvironment.AasEnvironmentSerialization;
import org.eclipse.digitaltwin.basyx.aasenvironment.base.DefaultAASEnvironmentSerialization;
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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.integration.file.RecursiveDirectoryScanner;
import org.xml.sax.SAXException;

/**
 * @author jungjan, witt
 */
public class TestAasEnvironmentPreconfigurationLoader {
	private final static String RESOURCES_DIR = "src/test/resources/";
	private final static String ENVIRONMENTS_DIR = RESOURCES_DIR + "environments/";

	private final static String AASX_SUFFIX = "aasx";
	private final static String JSON_SUFFIX = "json";
	private final static String XML_SUFFIX = "xml";

	private static AasRepository expectedAasRepository;
	private static SubmodelRepository expectedSubmodelRepository;
	private static ConceptDescriptionRepository expectetdConceptDescriptionRepository;

	private static AasEnvironmentSerialization aasEnvironmentSerialization;

	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, "");

	@BeforeClass
	public static void setUpClass() throws SerializationException, IOException, DeserializationException, InvalidOperationException, InvalidFormatException, SAXException {
		expectedAasRepository = new InMemoryAasRepository(new InMemoryAasServiceFactory());
		expectedSubmodelRepository = new InMemorySubmodelRepository(new InMemorySubmodelServiceFactory());
		expectetdConceptDescriptionRepository = new InMemoryConceptDescriptionRepository();

		aasEnvironmentSerialization = new DefaultAASEnvironmentSerialization(expectedAasRepository, expectedSubmodelRepository, expectetdConceptDescriptionRepository);
		prepareResourcesDirectory(ENVIRONMENTS_DIR);
	}

	@AfterClass
	public static void cleanUp() {
		cleanResourcesDirectory(ENVIRONMENTS_DIR);
	}

	private static void prepareResourcesDirectory(String rootFolder) throws SerializationException, IOException, DeserializationException, SAXException, InvalidOperationException, InvalidFormatException {
		createDirectoryAasxEnvironment();
		createDirectoryJsonEnvironment();
		createDirectoryXmlEnvironment();
	}

	public static void createDirectoryAasxEnvironment() throws SerializationException, IOException, InvalidOperationException, InvalidFormatException, DeserializationException {
		createShells(AASX_SUFFIX).forEach(expectedAasRepository::createAas);
		createSubmodels(AASX_SUFFIX).stream()
				.forEach(expectedSubmodelRepository::createSubmodel);
		createConceptDescriptions(AASX_SUFFIX).forEach(expectetdConceptDescriptionRepository::createConceptDescription);

		byte[] serialization = aasEnvironmentSerialization.createAASXAASEnvironmentSerialization(getShellIds(createShells(AASX_SUFFIX)), getSubmodelIds(createSubmodels(AASX_SUFFIX)), true);
		String filePath = "a/testEnvironment.aasx";
		writeBytesToFile(filePath, serialization);
	}

	public static void createDirectoryJsonEnvironment() throws SerializationException, IOException, DeserializationException {
		createShells(JSON_SUFFIX).forEach(expectedAasRepository::createAas);
		createSubmodels(JSON_SUFFIX).stream()
				.forEach(expectedSubmodelRepository::createSubmodel);
		createConceptDescriptions(JSON_SUFFIX).forEach(expectetdConceptDescriptionRepository::createConceptDescription);

		String jsonSerialization = aasEnvironmentSerialization.createJSONAASEnvironmentSerialization(getShellIds(createShells(JSON_SUFFIX)), getSubmodelIds(createSubmodels(JSON_SUFFIX)), true);
		String filePath = "b/testEnvironment.json";
		writeStringToFile(filePath, jsonSerialization);
	}

	public static void createDirectoryXmlEnvironment() throws SerializationException, IOException, SAXException, DeserializationException {
		createShells(XML_SUFFIX).forEach(expectedAasRepository::createAas);
		createSubmodels(XML_SUFFIX).stream()
				.forEach(expectedSubmodelRepository::createSubmodel);
		createConceptDescriptions(XML_SUFFIX).forEach(expectetdConceptDescriptionRepository::createConceptDescription);

		String xmlSerialization = aasEnvironmentSerialization.createXMLAASEnvironmentSerialization(getShellIds(createShells(XML_SUFFIX)), getSubmodelIds(createSubmodels(XML_SUFFIX)), true);
		String filePath = "b/testEnvironment.xml";
		writeStringToFile(filePath, xmlSerialization);
	}

	private static void writeStringToFile(String filePath, String data) {

		String fielPath = ENVIRONMENTS_DIR + filePath;
		File file = new File(fielPath);
		try (FileWriter fileWriter = new FileWriter(file)) {
			fileWriter.write(data);
			fileWriter.flush();
		} catch (IOException ignore) {
		}

	}

	private static void writeBytesToFile(String filePath, byte[] data) {
		String fielPath = ENVIRONMENTS_DIR + filePath;
		File file = new File(fielPath);
		try (FileOutputStream outputStream = new FileOutputStream(file)) {
			outputStream.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<String> getSubmodelIds(Collection<Submodel> submodels) {
		return submodels.stream()
				.map(submodel -> ((DefaultSubmodel) submodel).getId())
				.collect(Collectors.toList());
	}

	private static List<String> getShellIds(Collection<AssetAdministrationShell> shells) {
		return shells.stream()
				.map(shell -> ((DefaultAssetAdministrationShell) shell).getId())
				.collect(Collectors.toList());
	}

	private static void cleanResourcesDirectory(String rootFolder) {
		Collection<String> suffixesToBeDeleted = Arrays.asList(".aasx", ".json", ".xml");

		File rootDirectory = new File(rootFolder);
		RecursiveDirectoryScanner directoryScanner = new RecursiveDirectoryScanner();
		List<File> content = directoryScanner.listFiles(rootDirectory);

		for (File file : content) {
			for (String suffix : suffixesToBeDeleted) {
				if (file.getName()
						.endsWith(suffix)) {
					file.delete();
				}
			}
		}
	}

	@Test
	public void TestloadEnvironmentFromFolder() throws InvalidFormatException, IOException, DeserializationException {
		AasRepository actualShellRepository = new InMemoryAasRepository(new InMemoryAasServiceFactory());
		SubmodelRepository actualSubmodelRepository = new InMemorySubmodelRepository(new InMemorySubmodelServiceFactory());
		ConceptDescriptionRepository actualConceptDescriptionRepository = new InMemoryConceptDescriptionRepository();

		ResourceLoader resourceLoader = new FileSystemResourceLoader();
		String environmentsDir = RESOURCES_DIR + "environments";

		AasEnvironmentPreconfigurationLoader peconfigloader = new AasEnvironmentPreconfigurationLoader(resourceLoader, environmentsDir);
		peconfigloader.loadEnvironmentFromFolder(actualShellRepository, actualSubmodelRepository, actualConceptDescriptionRepository);

		assertAasxEnvironment(actualShellRepository, actualSubmodelRepository, actualConceptDescriptionRepository);
		assertJsonEnvironment(actualShellRepository, actualSubmodelRepository, actualConceptDescriptionRepository);
		assertXmlEnvironment(actualShellRepository, actualSubmodelRepository, actualConceptDescriptionRepository);

	}

	private void assertAasxEnvironment(AasRepository shellRepository, SubmodelRepository submodelReposiroty, ConceptDescriptionRepository conceptDescriptionRepository) {
		assertTrue(shellRepository.getAllAas(NO_LIMIT_PAGINATION_INFO)
				.getResult()
				.containsAll(createShells(AASX_SUFFIX)));
		assertTrue(submodelReposiroty.getAllSubmodels(NO_LIMIT_PAGINATION_INFO)
				.getResult()
				.containsAll(createSubmodels(AASX_SUFFIX)));
		assertTrue(conceptDescriptionRepository.getAllConceptDescriptions(NO_LIMIT_PAGINATION_INFO)
				.getResult()
				.containsAll(createConceptDescriptions(AASX_SUFFIX)));
	}

	private void assertJsonEnvironment(AasRepository shellRepository, SubmodelRepository submodelReposiroty, ConceptDescriptionRepository conceptDescriptionRepository) {
		assertTrue(shellRepository.getAllAas(NO_LIMIT_PAGINATION_INFO)
				.getResult()
				.containsAll(createShells(JSON_SUFFIX)));
		assertTrue(submodelReposiroty.getAllSubmodels(NO_LIMIT_PAGINATION_INFO)
				.getResult()
				.containsAll(createSubmodels(JSON_SUFFIX)));
		assertTrue(conceptDescriptionRepository.getAllConceptDescriptions(NO_LIMIT_PAGINATION_INFO)
				.getResult()
				.containsAll(createConceptDescriptions(JSON_SUFFIX)));
	}

	private void assertXmlEnvironment(AasRepository shellRepository, SubmodelRepository submodelReposiroty, ConceptDescriptionRepository conceptDescriptionRepository) {
		assertTrue(shellRepository.getAllAas(NO_LIMIT_PAGINATION_INFO)
				.getResult()
				.containsAll(createShells(XML_SUFFIX)));
		assertTrue(submodelReposiroty.getAllSubmodels(NO_LIMIT_PAGINATION_INFO)
				.getResult()
				.containsAll(createSubmodels(XML_SUFFIX)));
		assertTrue(conceptDescriptionRepository.getAllConceptDescriptions(NO_LIMIT_PAGINATION_INFO)
				.getResult()
				.containsAll(createConceptDescriptions(XML_SUFFIX)));
	}

	private static Collection<AssetAdministrationShell> createShells(String suffix) {
		AssetAdministrationShell shell1 = new DefaultAssetAdministrationShell.Builder().id("technical-data-shell-id-" + suffix)
				.idShort("technical-data-shell-idShort-" + suffix)
				.assetInformation(new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE)
						.globalAssetID(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID)
						.build())
				.build();

		AssetAdministrationShell shell2 = new DefaultAssetAdministrationShell.Builder().id("operational-data-shell-id-" + suffix)
				.idShort("operational-data-shell-idShort-" + suffix)
				.assetInformation(new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE)
						.globalAssetID(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_OPERATION_ID)
						.build())
				.build();
		return Arrays.asList(shell1, shell2);
	}

	private static Collection<Submodel> createSubmodels(String suffix) {

		Submodel submodel1 = DummySubmodelFactory.createTechnicalDataSubmodel();
		Submodel submodel2 = DummySubmodelFactory.createOperationalDataSubmodel();
		submodel1.setId(submodel1.getId() + suffix);
		submodel2.setId(submodel2.getId() + suffix);
		return Arrays.asList(submodel1, submodel2);
	}

	public static Collection<ConceptDescription> createConceptDescriptions(String suffix) {
		ConceptDescription conceptDescription1 = new DefaultConceptDescription.Builder().id(SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_SEMANTIC_ID_PROPERTY + suffix)
				.build();
		ConceptDescription conceptDescription2 = new DefaultConceptDescription.Builder().id(DummySubmodelFactory.SUBMODEL_OPERATIONAL_DATA_SEMANTIC_ID_PROPERTY + suffix)
				.build();

		return Arrays.asList(conceptDescription1, conceptDescription2);
	}
}
