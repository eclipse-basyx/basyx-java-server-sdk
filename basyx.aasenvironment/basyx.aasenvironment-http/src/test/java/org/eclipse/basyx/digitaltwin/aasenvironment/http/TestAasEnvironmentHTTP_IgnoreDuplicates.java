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

package org.eclipse.basyx.digitaltwin.aasenvironment.http;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;

import static org.eclipse.basyx.digitaltwin.aasenvironment.http.TestAasEnvironmentHTTP.*;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TestAasEnvironmentHTTP_IgnoreDuplicates {


	private static ConfigurableApplicationContext appContext;
	private static SubmodelRepository submodelRepo;
	private static AasRepository aasRepo;
	private static ConceptDescriptionRepository conceptDescriptionRepo;

	private static final String AAS_ID = "http://customer.com/aas/9175_7013_7091_9168";
	private static final String SUBMODEL_ID = "http://i40.customer.com/type/1/1/7A7104BDAB57E184";
	private static final String CONCEPT_DESCRIPTION_ID = "http://www.vdi2770.com/blatt1/Entwurf/Okt18/cd/Description/Title";

	private int initialAasCount;
	private int initialSmCount;
	private int initialCdCount;


	@BeforeClass
	public static void startAasRepo() throws Exception {
		appContext = new SpringApplication(DummyAASEnvironmentComponent.class).run(new String[] {});
		submodelRepo = appContext.getBean(SubmodelRepository.class);
		aasRepo = appContext.getBean(AasRepository.class);
		conceptDescriptionRepo = appContext.getBean(ConceptDescriptionRepository.class);
	}

	@Parameterized.Parameter
	public String scenario;

	@Parameters(name = "TestCase: {0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ "NO_CHANGE" },    // Normal test. No changes before AASX reupload
				{ "DELETE_AAS" },   // Delete AAS before AASX reupload
				{ "DELETE_SM" },    // Delete Submodel before AASX reupload
				{ "DELETE_CD" }     // Delete ConceptDescription before AASX reupload
		});
	}


	@Test
	public void testEnvironmentIgnoreDuplicates_AASX() throws IOException {
		uploadInitialAASX();
		recordInitialCounts();

		applyScenarioModifications();

		reuploadAASX();
		verifyFinalState();

		cleanEnvironment();
	}

	private void uploadInitialAASX() throws IOException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executePostRequest(
				HttpClients.createDefault(),
				createPostRequestWithFile(AASX_ENV_PATH, AASX_MIMETYPE));

		assertEquals(HttpStatus.OK.value(), response.getCode());
		assertEntitiesExist();
	}

	private void recordInitialCounts() {
		initialAasCount = aasRepo.getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().size();
		initialSmCount = submodelRepo.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().size();
		initialCdCount = conceptDescriptionRepo.getAllConceptDescriptions(PaginationInfo.NO_LIMIT).getResult().size();
	}

	private void applyScenarioModifications() {
		switch (scenario) {
		case "DELETE_AAS":
			deleteAas();
			break;
		case "DELETE_SM":
			deleteSubmodel();
			break;
		case "DELETE_CD":
			deleteConceptDescription();
			break;
		default:
			break; // NO_CHANGE scenario
		}
	}

	private void deleteAas() {
		aasRepo.deleteAas(AAS_ID);
		assertThrows(ElementDoesNotExistException.class, () -> aasRepo.getAas(AAS_ID));
		assertEquals(initialAasCount - 1, aasRepo.getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().size());
	}

	private void deleteSubmodel() {
		submodelRepo.deleteSubmodel(SUBMODEL_ID);
		assertThrows(ElementDoesNotExistException.class, () -> submodelRepo.getSubmodel(SUBMODEL_ID));
		assertEquals(initialSmCount - 1, submodelRepo.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().size());
	}

	private void deleteConceptDescription() {
		conceptDescriptionRepo.deleteConceptDescription(CONCEPT_DESCRIPTION_ID);
		assertThrows(ElementDoesNotExistException.class, () -> conceptDescriptionRepo.getConceptDescription(CONCEPT_DESCRIPTION_ID));
		assertEquals(initialCdCount - 1, conceptDescriptionRepo.getAllConceptDescriptions(PaginationInfo.NO_LIMIT).getResult().size());
	}

	private void reuploadAASX() throws IOException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executePostRequest(
				HttpClients.createDefault(),
				createPostRequestWithFile(AASX_ENV_PATH, AASX_MIMETYPE, true));

		assertEquals(HttpStatus.OK.value(), response.getCode());
		assertEntitiesExist();
	}

	private void verifyFinalState() {
		assertEquals(initialAasCount, aasRepo.getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().size());
		assertEquals(initialSmCount, submodelRepo.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().size());
		assertEquals(initialCdCount, conceptDescriptionRepo.getAllConceptDescriptions(PaginationInfo.NO_LIMIT).getResult().size());
	}

	private void assertEntitiesExist() {
		assertNotNull(aasRepo.getAas(AAS_ID));
		assertNotNull(submodelRepo.getSubmodel(SUBMODEL_ID));
		assertNotNull(conceptDescriptionRepo.getConceptDescription(CONCEPT_DESCRIPTION_ID));
	}

	private void cleanEnvironment() {
		try {
			submodelRepo.deleteSubmodel(SUBMODEL_ID);
			aasRepo.deleteAas(AAS_ID);
			conceptDescriptionRepo.deleteConceptDescription(CONCEPT_DESCRIPTION_ID);
		} catch (Exception ignored) {}
	}

	@AfterClass
	public static void shutdown() {
		appContext.close();
	}
}
