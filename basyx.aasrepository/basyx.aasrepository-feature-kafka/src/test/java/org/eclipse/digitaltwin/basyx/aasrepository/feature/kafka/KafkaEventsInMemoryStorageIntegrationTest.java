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
package org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.CrudAasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events.model.AasEvent;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events.model.AasEventType;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceFactory;
import org.eclipse.digitaltwin.basyx.aasservice.backend.AasBackend;
import org.eclipse.digitaltwin.basyx.aasservice.backend.CrudAasServiceFactory;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasBackend;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.kafka.KafkaAdapter;
import org.eclipse.digitaltwin.basyx.kafka.KafkaAdapters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * @author sonnenberg (DFKI GmbH)
 */
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ComponentScan(basePackages = { "org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka" })
@ContextConfiguration(classes = { TestApplication.class })
@TestPropertySource(properties = { KafkaAasRepositoryFeature.FEATURENAME + ".enabled=true", "spring.kafka.bootstrap-servers=localhost:9092", KafkaAasRepositoryFeature.FEATURENAME + ".topic.name=" + TestApplication.KAFKA_AAS_TOPIC })
public class KafkaEventsInMemoryStorageIntegrationTest {

	private static KafkaAdapter<AasEvent> adapter = KafkaAdapters.getAdapter(TestApplication.KAFKA_AAS_TOPIC, AasEvent.class);

	@Autowired
	private KafkaAasRepositoryFeature feature;

	private AasRepository repo;

	@BeforeEach
	public void init() {
		adapter.skipMessages();
		FileRepository fileRepo = new InMemoryFileRepository();
		AasBackend aasRepositoryBackend = new InMemoryAasBackend();
		AasServiceFactory sf = new CrudAasServiceFactory(aasRepositoryBackend, fileRepo);
		CrudAasRepositoryFactory factory = new CrudAasRepositoryFactory(aasRepositoryBackend, sf, "test");
		repo = feature.decorate(factory).create();

		cleanup();
	}

	@Test
	public void testCreateAas() {
		AssetAdministrationShell shell = TestShells.shell();
		repo.createAas(shell);

		AasEvent evt = adapter.next();
		Assertions.assertEquals(AasEventType.AAS_CREATED, evt.getType());
		Assertions.assertEquals(TestShells.ID_AAS, evt.getId());
		Assertions.assertNull(evt.getSubmodelId());
		Assertions.assertNull(evt.getAssetInformation());
		Assertions.assertNull(evt.getReference());
		Assertions.assertEquals(shell, evt.getAas());
	}

	@Test
	public void testUpdateAas() {
		AssetAdministrationShell shell = TestShells.shell();
		repo.createAas(shell);

		AasEvent evtCreated = adapter.next();
		Assertions.assertEquals(AasEventType.AAS_CREATED, evtCreated.getType());
		Assertions.assertEquals(shell, evtCreated.getAas());

		AssetAdministrationShell newShell = TestShells.shell();
		newShell.setIdShort("newIdShort");
		repo.updateAas(newShell.getId(), newShell);

		AasEvent evtUpdated = adapter.next();
		Assertions.assertEquals(AasEventType.AAS_UPDATED, evtUpdated.getType());

		Assertions.assertEquals(TestShells.ID_AAS, evtUpdated.getId());
		Assertions.assertNull(evtUpdated.getSubmodelId());
		Assertions.assertNull(evtUpdated.getAssetInformation());
		Assertions.assertNull(evtUpdated.getReference());
		Assertions.assertEquals(newShell, evtUpdated.getAas());
	}

	@Test
	public void testDelete() {
		AssetAdministrationShell shell = TestShells.shell();
		repo.createAas(shell);

		AasEvent evtCreated = adapter.next();
		Assertions.assertEquals(AasEventType.AAS_CREATED, evtCreated.getType());
		Assertions.assertEquals(shell, evtCreated.getAas());

		repo.deleteAas(shell.getId());

		AasEvent evtDeleted = adapter.next();
		Assertions.assertEquals(AasEventType.AAS_DELETED, evtDeleted.getType());

		Assertions.assertEquals(TestShells.ID_AAS, evtDeleted.getId());
		Assertions.assertNull(evtDeleted.getSubmodelId());
		Assertions.assertNull(evtDeleted.getAssetInformation());
		Assertions.assertNull(evtDeleted.getReference());
		Assertions.assertNull(evtDeleted.getAas());
	}

	@Test
	public void testAssetInformation() {
		AssetAdministrationShell shell = TestShells.shell();
		repo.createAas(shell);

		AasEvent evtCreated = adapter.next();
		Assertions.assertEquals(AasEventType.AAS_CREATED, evtCreated.getType());
		Assertions.assertEquals(shell, evtCreated.getAas());

		AssetInformation assetInfo = new DefaultAssetInformation.Builder().assetKind(AssetKind.TYPE).assetType("robot").globalAssetId("aas:robot:id").build();
		repo.setAssetInformation(shell.getId(), assetInfo);

		AasEvent evtAasIdSet = adapter.next();
		Assertions.assertEquals(AasEventType.ASSET_INFORMATION_SET, evtAasIdSet.getType());

		Assertions.assertEquals(TestShells.ID_AAS, evtAasIdSet.getId());
		Assertions.assertNull(evtAasIdSet.getSubmodelId());
		Assertions.assertEquals(assetInfo, evtAasIdSet.getAssetInformation());
		Assertions.assertNull(evtAasIdSet.getReference());
		Assertions.assertNull(evtAasIdSet.getAas());
	}

	@Test
	public void testSubmodelReferenceAdded() {
		AssetAdministrationShell shell = TestShells.shell();
		repo.createAas(shell);

		AasEvent evtCreated = adapter.next();
		Assertions.assertEquals(AasEventType.AAS_CREATED, evtCreated.getType());
		Assertions.assertEquals(shell, evtCreated.getAas());

		String smId = "http://sm.id/1";
		Reference ref = new DefaultReference.Builder().type(ReferenceTypes.MODEL_REFERENCE).keys(new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(smId).build()).build();
		repo.addSubmodelReference(TestShells.ID_AAS, ref);

		AasEvent evtRefAdded = adapter.next();
		Assertions.assertEquals(AasEventType.SM_REF_ADDED, evtRefAdded.getType());

		Assertions.assertEquals(TestShells.ID_AAS, evtRefAdded.getId());
		Assertions.assertEquals(smId, evtRefAdded.getSubmodelId());
		Assertions.assertNull(evtRefAdded.getAssetInformation());
		Assertions.assertEquals(ref, evtRefAdded.getReference());
		Assertions.assertNull(evtRefAdded.getAas());
	}

	@Test
	public void testSubmodelReferenceRemoved() {
		AssetAdministrationShell shell = TestShells.shell();
		repo.createAas(shell);

		AasEvent evtCreated = adapter.next();
		Assertions.assertEquals(AasEventType.AAS_CREATED, evtCreated.getType());
		Assertions.assertEquals(shell, evtCreated.getAas());

		repo.removeSubmodelReference(TestShells.ID_AAS, TestShells.ID_SM);
		AasEvent evtRefRemoved = adapter.next();
		Assertions.assertEquals(AasEventType.SM_REF_DELETED, evtRefRemoved.getType());

		Assertions.assertEquals(TestShells.ID_AAS, evtRefRemoved.getId());
		Assertions.assertEquals(TestShells.ID_SM, evtRefRemoved.getSubmodelId());
		Assertions.assertNull(evtRefRemoved.getAssetInformation());
		Assertions.assertNull(evtRefRemoved.getReference());
		Assertions.assertNull(evtRefRemoved.getAas());
	}

	@Test
	public void testGetterAreWorking() {
		AssetAdministrationShell shell = TestShells.shell();
		repo.createAas(shell);
		AasEvent evtCreated = adapter.next();
		Assertions.assertEquals(AasEventType.AAS_CREATED, evtCreated.getType());

		Assertions.assertEquals(1, repo.getSubmodelReferences(TestShells.ID_AAS, new PaginationInfo(null, null)).getResult().size());
		Assertions.assertEquals(shell, repo.getAas(TestShells.ID_AAS));
		Assertions.assertEquals(1, repo.getAllAas(null, null, new PaginationInfo(null, null)).getResult().size());
		Assertions.assertEquals(shell.getAssetInformation(), repo.getAssetInformation(TestShells.ID_AAS));
	}

	@Test
	public void testFeatureIsEnabled() {
		Assertions.assertTrue(feature.isEnabled());
	}

	@AfterEach
	public void tearDown() {
		cleanup();
	}

	public void cleanup() {
		for (AssetAdministrationShell aas : repo.getAllAas(null, null, new PaginationInfo(null, null)).getResult()) {
			repo.deleteAas(aas.getId());
			AasEvent deletedEvt = adapter.next();
			Assertions.assertEquals(AasEventType.AAS_DELETED, deletedEvt.getType());
			Assertions.assertEquals(aas.getId(), deletedEvt.getId());
		}
		adapter.assertNoAdditionalMessages();
	}
}