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
package org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka;

import java.util.concurrent.TimeUnit;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Blob;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultBlob;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.kafka.KafkaAdapter;
import org.eclipse.digitaltwin.basyx.kafka.KafkaAdapters;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelBackend;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.CrudSubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.SubmodelBackend;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.model.SubmodelEvent;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.model.SubmodelEventType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author geso02 (Sonnenberg DFKI GmbH)
 */
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ComponentScan(basePackages = { "org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka" })
@ActiveProfiles("test-submodel")
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { SubmodelServiceTestComponent.class })
@TestPropertySource(properties = { "spring.kafka.bootstrap-servers=localhost:9092", KafkaSubmodelServiceFeature.FEATURENAME + ".preservationlevel=REMOVE_BLOB_VALUE", KafkaSubmodelServiceFeature.FEATURENAME + ".enabled=true",
		KafkaSubmodelServiceFeature.FEATURENAME + ".topic.name=submodel-events" })

public class KafkaSubmodelServiceSubmodelElementsEventsIntegrationTest {

	private static KafkaAdapter<SubmodelEvent> adapter = KafkaAdapters.getAdapter("submodel-events", SubmodelEvent.class);


	@Autowired
	private KafkaSubmodelServiceFeature feature;

	@Autowired
	private Submodel submodel;

	private SubmodelService service;

	@Before
	public void startPolling() throws InterruptedException {

		FileRepository repository = new InMemoryFileRepository();
		SubmodelBackend backend = new InMemorySubmodelBackend();
		SubmodelServiceFactory smFactory = new CrudSubmodelServiceFactory(backend, repository);
		service = feature.decorate(smFactory).create(submodel);
	}

	@After
	public void assertNoAdditionalMessageAndStopPolling() throws InterruptedException {
		
		adapter.assertNoAdditionalMessages();
		
	}

	@Test
	public void testToplevelSubmodelElementAdded() {
		Assert.assertTrue(feature.isEnabled());

		SubmodelElement elem = TestSubmodels.submodelElement(TestSubmodels.IDSHORT_PROP_1, "ID");
		service.createSubmodelElement(elem);

		SubmodelEvent evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SME_CREATED, evt.getType());
		Assert.assertEquals(submodel.getId(), evt.getId());
		Assert.assertEquals(TestSubmodels.IDSHORT_PROP_1, evt.getSmElementPath());
		Assert.assertNull(evt.getSubmodel());
		Assert.assertEquals(elem, evt.getSmElement());
	}

	@Test
	public void testElementAddedUnderCollection() {
		SubmodelElement elem = TestSubmodels.submodelElement(TestSubmodels.IDSHORT_PROP_1, "55");
		service.createSubmodelElement(TestSubmodels.IDSHORT_COLL, elem);

		SubmodelEvent evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SME_CREATED, evt.getType());
		Assert.assertEquals(submodel.getId(), evt.getId());
		String expected = TestSubmodels.path(TestSubmodels.IDSHORT_COLL, TestSubmodels.IDSHORT_PROP_1);
		Assert.assertEquals(expected, evt.getSmElementPath());
		Assert.assertNull(evt.getSubmodel());
		Assert.assertEquals(elem, evt.getSmElement());
	}

	@Test
	public void testSubmodelElementAddedAndBlobValueNotPartOfTheEvent() {
		String idShortBlob = "blob";
		Blob blob = new DefaultBlob.Builder().idShort(idShortBlob).value(new byte[] { 1, 2, 3, 4, 5 }).build();
		service.createSubmodelElement(blob);

		SubmodelEvent evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SME_CREATED, evt.getType());
		Assert.assertEquals(TestSubmodels.ID_SM, evt.getId());
		Assert.assertEquals(idShortBlob, evt.getSmElementPath());
		Assert.assertNull(evt.getSubmodel());
		Assert.assertNotEquals(blob, evt.getSmElement());
		SubmodelElement expectedElem = new DefaultBlob.Builder().idShort(idShortBlob).build(); // expected has no value
		Assert.assertEquals(expectedElem, evt.getSmElement());
	}

	@Test
	public void testSubmodelElementUpdated() {
		SubmodelElement elem = TestSubmodels.submodelElement(TestSubmodels.IDSHORT_PROP_0, "99");
		service.updateSubmodelElement(TestSubmodels.IDSHORT_PROP_0, elem);

		SubmodelEvent evtUpdated = adapter.next();
		Assert.assertEquals(SubmodelEventType.SME_UPDATED, evtUpdated.getType());
		Assert.assertEquals(TestSubmodels.ID_SM, evtUpdated.getId());
		Assert.assertEquals(TestSubmodels.IDSHORT_PROP_0, evtUpdated.getSmElementPath());
		Assert.assertNull(evtUpdated.getSubmodel());
		Assert.assertEquals(elem, evtUpdated.getSmElement());
	}

	@Test
	public void testSubmodelElementDeleted() {
		service.deleteSubmodelElement(TestSubmodels.IDSHORT_PROP_TO_BE_REMOVED);

		SubmodelEvent evtDeleted = adapter.next();
		Assert.assertEquals(SubmodelEventType.SME_DELETED, evtDeleted.getType());
		Assert.assertEquals(TestSubmodels.ID_SM, evtDeleted.getId());
		Assert.assertEquals(TestSubmodels.IDSHORT_PROP_TO_BE_REMOVED, evtDeleted.getSmElementPath());
		Assert.assertNull(evtDeleted.getSubmodel());
		Assert.assertNull(evtDeleted.getSmElement());
	}

}
