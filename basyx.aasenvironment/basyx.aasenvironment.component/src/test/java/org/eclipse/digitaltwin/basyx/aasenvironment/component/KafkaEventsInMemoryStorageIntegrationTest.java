/*******************************************************************************
 * Copyright (C) 2024 DFKI GmbH (https://www.dfki.de/en/web)
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
 * 
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.aasenvironment.component;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.KafkaAasRepositoryFeature;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.TestShells;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events.model.AasEvent;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events.model.AasEventType;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.kafka.KafkaAdapter;
import org.eclipse.digitaltwin.basyx.kafka.KafkaAdapters;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.kafka.KafkaSubmodelRepositoryFeature;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.TestSubmodels;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.model.SubmodelEvent;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.model.SubmodelEventType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * @author sonnenberg (DFKI GmbH)
 */
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ComponentScan(basePackages = { "org.eclipse.digitaltwin.basyx" })
@RunWith(SpringRunner.class)
@TestPropertySource(properties = { "basyx.environment=", "basyx.feature.kafka.enabled=true", "spring.kafka.bootstrap-servers=localhost:9092" })
@AutoConfigureMockMvc
public class KafkaEventsInMemoryStorageIntegrationTest {

	@Autowired
	private KafkaAasRepositoryFeature aasFeature;

	@Autowired
	private KafkaSubmodelRepositoryFeature submodelFeature;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private JsonSerializer serializer;

	@Autowired
	private SubmodelRepository smRepo;

	@Autowired
	private AasRepository aasRepo;

	private static KafkaAdapter<SubmodelEvent> adapterSm = KafkaAdapters.getAdapter("submodel-events", SubmodelEvent.class);
	private static KafkaAdapter<AasEvent> adapterAas = KafkaAdapters.getAdapter("aas-events", AasEvent.class);
	
	@Before
	public void init() {
		cleanup();
	}

	@Test
	public void testCreateAas() throws Exception {
		AssetAdministrationShell shell = TestShells.shell();
		String body = serializer.write(shell);

		mvc.perform(MockMvcRequestBuilders.post("/shells").contentType(MediaType.APPLICATION_JSON).content(body).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.content().json(body));
		AasEvent aasEvt = adapterAas.next();
		Assert.assertEquals(shell, aasEvt.getAas());
		Assert.assertEquals(shell.getId(), aasEvt.getId());
		Assert.assertNull(aasEvt.getSubmodelId());
		Assert.assertNull(aasEvt.getAssetInformation());
		Assert.assertNull(aasEvt.getReference());

		Submodel sm = TestSubmodels.createSubmodel("http://submodels/123", "123", "hello");
		body = serializer.write(sm);
		mvc.perform(MockMvcRequestBuilders.post("/submodels").contentType(MediaType.APPLICATION_JSON).content(body).accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isCreated());
		SubmodelEvent smEvt = adapterSm.next();
		Assert.assertEquals(sm, smEvt.getSubmodel());
		Assert.assertEquals(sm.getId(), smEvt.getId());
		Assert.assertNull(smEvt.getSmElement());
		Assert.assertNull(smEvt.getSmElementPath());
	}

	@Test
	public void testFeatureIsEnabled() {
		Assert.assertTrue(aasFeature.isEnabled());
		Assert.assertTrue(submodelFeature.isEnabled());
	}

	@After
	public void dispose() {
		cleanup();
	}

	public void cleanup() {
		for (AssetAdministrationShell aas : aasRepo.getAllAas(null, null, new PaginationInfo(null, null)).getResult()) {
			aasRepo.deleteAas(aas.getId());
			AasEvent aasEvt = adapterAas.next();
			Assert.assertEquals(AasEventType.AAS_DELETED, aasEvt.getType());
		}

		for (Submodel sm : smRepo.getAllSubmodels(new PaginationInfo(null, null)).getResult()) {
			smRepo.deleteSubmodel(sm.getId());
			SubmodelEvent smEvt = adapterSm.next();
			Assert.assertEquals(SubmodelEventType.SM_DELETED, smEvt.getType());
		}
		adapterSm.assertNoAdditionalMessages();
		adapterAas.assertNoAdditionalMessages();
	}
}
