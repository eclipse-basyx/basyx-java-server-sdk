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

package org.eclipse.digitaltwin.basyx.aasenvironment.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.ParseException;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;

/**
 * Tests the Preconfiguration feature of the AAS Environment
 * 
 * @author fried, witt, jungjan, danish
 */
public class TestPreconfiguration {
	private static ConfigurableApplicationContext appContext;
	private static SubmodelRepository submodelRepo;
	private static AasRepository aasRepo;
	private static ConceptDescriptionRepository conceptDescriptionRepo;

	@BeforeClass
	public static void startAASEnvironment() throws Exception {
		appContext = new SpringApplication(AasEnvironmentComponent.class).run(new String[] {});
		submodelRepo = appContext.getBean(SubmodelRepository.class);
		aasRepo = appContext.getBean(AasRepository.class);
		conceptDescriptionRepo = appContext.getBean(ConceptDescriptionRepository.class);
	}

	@Test
	public void jsonLoadedSubmodelIsInRepository() throws IOException, ParseException {
		Submodel submodel = submodelRepo.getSubmodel("http://acplt.test/Submodels/Assets/TestAsset/Identification");
		assertNotNull(submodel);
	}

	@Test
	public void jsonLoadedShellIsInRepository() throws IOException, ParseException {
		AssetAdministrationShell shell = aasRepo.getAas("https://acplt.test/Test_AssetAdministrationShell");
		assertNotNull(shell);
	}

	@Test
	public void jsonLoadedConceptDescriptionIsInRepository() throws IOException, ParseException {
		ConceptDescription conceptDescription = conceptDescriptionRepo.getConceptDescription("https://acplt.test/Test_ConceptDescription");
		assertNotNull(conceptDescription);
	}

	@Test
	public void xmlLoadedSubmodelIsInRepository() throws IOException, ParseException {
		Submodel submodel = submodelRepo.getSubmodel("http://i40.customer.test/type/1/1/7A7104BDAB57E184");
		assertNotNull(submodel);
	}

	@Test
	public void xmlLoadedShellIsInRepository() throws IOException, ParseException {
		AssetAdministrationShell shell = aasRepo.getAas("http://customer.test/aas/9175_7013_7091_9168");
		assertNotNull(shell);
	}

	@Test
	public void xmlLoadedConceptDescriptionIsInRepository() throws IOException, ParseException {
		ConceptDescription conceptDescription = conceptDescriptionRepo.getConceptDescription("http://www.vdi2770.test/blatt1/Entwurf/Okt18/cd/Description/Title");
		assertNotNull(conceptDescription);
	}

	@Test
	public void aasxLoadedSubmodelIsInRepository() throws IOException, ParseException {
		Submodel submodel = submodelRepo.getSubmodel("http://i40.customer.com/type/1/1/7A7104BDAB57E184");
		assertNotNull(submodel);
	}

	@Test
	public void aasxLoadedShellIsInRepository() throws IOException, ParseException {
		AssetAdministrationShell shell = aasRepo.getAas("http://customer.com/aas/9175_7013_7091_9168");
		assertNotNull(shell);
	}

	@Test
	public void aasxLoadedConceptDescriptionIsInRepository() throws IOException, ParseException {
		ConceptDescription conceptDescription = conceptDescriptionRepo.getConceptDescription("http://www.vdi2770.com/blatt1/Entwurf/Okt18/cd/Description/Title");
		assertNotNull(conceptDescription);
	}

	@Test
	public void aasxFromDirectoryLoadedShellsInRepository() {
		AssetAdministrationShell shell1 = aasRepo.getAas("technical-data-shell-id-aasx");
		AssetAdministrationShell shell2 = aasRepo.getAas("operational-data-shell-id-aasx");

		assertNotNull(shell1);
		assertNotNull(shell2);
	}

	@Test
	public void aasxFromDirectoryLoadedSubmodelsInRepository() {
		Submodel submodel1 = submodelRepo.getSubmodel("7A7104BDAB57E184aasx");
		Submodel submodel2 = submodelRepo.getSubmodel("AC69B1CB44F07935aasx");

		assertNotNull(submodel1);
		assertNotNull(submodel2);
	}

	@Test
	public void getThumbnail() throws IOException {
		File file = aasRepo.getThumbnail("http://customer.com/aas/9175_7013_7091_9168");

		InputStream expectedContent = getClass().getClassLoader().getResourceAsStream("testFiles/verwaltungsschale-detail-part1.png");
		InputStream actualContent = new FileInputStream(file);

		assertTrue(IOUtils.contentEquals(expectedContent, actualContent));
	}

	@Test
	public void jsonFromDirectoryLoadedShellsInRepository() {
		AssetAdministrationShell shell1 = aasRepo.getAas("technical-data-shell-id-json");
		AssetAdministrationShell shell2 = aasRepo.getAas("operational-data-shell-id-json");

		assertNotNull(shell1);
		assertNotNull(shell2);
	}

	@Test
	public void jsonFromDirectoryLoadedSubmodelsInRepository() {
		Submodel submodel1 = submodelRepo.getSubmodel("7A7104BDAB57E184json");
		Submodel submodel2 = submodelRepo.getSubmodel("AC69B1CB44F07935json");

		assertNotNull(submodel1);
		assertNotNull(submodel2);
	}

	@Test
	public void xmlFromDirectoryLoadedShellsInRepository() {
		AssetAdministrationShell shell1 = aasRepo.getAas("technical-data-shell-id-xml");
		AssetAdministrationShell shell2 = aasRepo.getAas("operational-data-shell-id-xml");

		assertNotNull(shell1);
		assertNotNull(shell2);
	}

	@Test
	public void xmlFromDirectoryLoadedSubmodelsInRepository() {
		Submodel submodel1 = submodelRepo.getSubmodel("7A7104BDAB57E184xml");
		Submodel submodel2 = submodelRepo.getSubmodel("AC69B1CB44F07935xml");

		assertNotNull(submodel1);
		assertNotNull(submodel2);
	}
	
	@Test
	public void aasxPDFFileIntegrationInSubmodelRepository() throws FileNotFoundException, IOException {
		InputStream expectedFile = getInputStreamOfFileFromClasspath("testFiles/OperatingManual.pdf");
		String expectedFileExtension = "pdf";
		
		File actualFile = submodelRepo.getFileByPathSubmodel("http://i40.customer.com/type/1/1/1A7B62B529F19152", "OperatingManual.DigitalFile_PDF");
		
		assertEquals(expectedFileExtension, getExtension(actualFile.getName()));
		assertTrue(IOUtils.contentEquals(expectedFile, FileUtils.openInputStream(actualFile)));
	}
	
	@Test
	public void aasxPNGFileIntegrationInSubmodelRepository() throws FileNotFoundException, IOException {
		String expectedFilePath = "testFiles/verwaltungsschale-detail-part1.png";
		String expectedFileExtension = "png";
		
		File actualFile1 = submodelRepo.getFileByPathSubmodel("7A7104BDAB57E184aasx", "FileData");
		File actualFile2 = submodelRepo.getFileByPathSubmodel("7A7104BDAB57E184aasx", "SubmodelElementCollection.FileData");
		
		assertFileContents(expectedFilePath, expectedFileExtension, actualFile1, actualFile2);
	}

	@AfterClass
	public static void shutdownAASEnvironment() {
		appContext.close();
	}
	
	private void assertFileContents(String expectedFilePath, String expectedFileExtension, File actualFile1, File actualFile2) throws IOException {
		assertEquals(expectedFileExtension, getExtension(actualFile1.getName()));
		assertEquals(expectedFileExtension, getExtension(actualFile2.getName()));
		assertTrue(IOUtils.contentEquals(getInputStreamOfFileFromClasspath(expectedFilePath), FileUtils.openInputStream(actualFile1)));
		assertTrue(IOUtils.contentEquals(getInputStreamOfFileFromClasspath(expectedFilePath), FileUtils.openInputStream(actualFile2)));
	}
	
	private InputStream getInputStreamOfFileFromClasspath(String fileName) throws FileNotFoundException, IOException {
		ClassPathResource classPathResource = new ClassPathResource(fileName);
		
		return classPathResource.getInputStream();
	}
	
	private String getExtension(String filename) {
	    return FilenameUtils.getExtension(filename);
	}

}
