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

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
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
@ContextConfiguration(classes = SubmodelServiceTestComponent.class)
@RunWith(SpringRunner.class)
@TestPropertySource(properties = { "spring.kafka.bootstrap-servers=localhost:9092", KafkaSubmodelServiceFeature.FEATURENAME + ".preservationlevel=IDS_ONLY", KafkaSubmodelServiceFeature.FEATURENAME + ".enabled=true",
		KafkaSubmodelServiceFeature.FEATURENAME + ".topic.name=submodel-events", })
public class KafkaSubmodelServiceIdsOnlySmokeTest {

	private static KafkaAdapter<SubmodelEvent> adapter = KafkaAdapters.getAdapter("submodel-events", SubmodelEvent.class);


	@Autowired
	private KafkaSubmodelServiceFeature feature;

	@Autowired
	private Submodel submodel;

	private SubmodelService service;

	@Before
	public void init() throws InterruptedException, SerializationException {
		adapter.skipMessages();
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
		Assert.assertNull(evt.getSmElement());
	}

	@Test
	public void testSubmodelElementPatched() {
		Assert.assertTrue(feature.isEnabled());

		SubmodelElement elem0 = TestSubmodels.submodelElement(TestSubmodels.IDSHORT_PROP_0, "0");
		SubmodelElement elem1 = TestSubmodels.submodelElement(TestSubmodels.IDSHORT_PROP_1, "1");
		service.patchSubmodelElements(List.of(elem0, elem1));

		SubmodelEvent evt = adapter.next();
		// the submodel was updated
		Assert.assertEquals(SubmodelEventType.SM_UPDATED, evt.getType());
		Assert.assertEquals(submodel.getId(), evt.getId());
		Assert.assertNull(evt.getSubmodel()); // ids only
		Assert.assertNull(evt.getSmElementPath());
		Assert.assertNull(evt.getSmElement());

	}

}
