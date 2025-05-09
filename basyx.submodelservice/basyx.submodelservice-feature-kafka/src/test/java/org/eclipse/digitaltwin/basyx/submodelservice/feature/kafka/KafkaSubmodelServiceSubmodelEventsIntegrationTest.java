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

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.model.SubmodelEvent;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.model.SubmodelEventType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author geso02 (Sonnenberg DFKI GmbH)
 */
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ComponentScan(basePackages = { "org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka" })
@RunWith(SpringRunner.class)
@ActiveProfiles("test-submodel")
@ContextConfiguration(classes = {SubmodelServiceTestComponent.class})
@TestPropertySource(properties = { "spring.kafka.bootstrap-servers=PLAINTEXT_HOST://localhost:9092",
		KafkaSubmodelServiceFeature.FEATURENAME + ".preservationlevel=REMOVE_BLOB_VALUE",
		KafkaSubmodelServiceFeature.FEATURENAME + ".enabled=true",
		KafkaSubmodelServiceFeature.FEATURENAME + ".topic.name=" + SubmodelEventKafkaListener.TOPIC_NAME,
		KafkaSubmodelServiceApplicationListener.SUBMODEL_EVENTS_ACTIVATED + "=true"
})
@Import(SubmodelEventKafkaListener.class)
public class KafkaSubmodelServiceSubmodelEventsIntegrationTest {

	@Autowired
	private SubmodelEventKafkaListener listener;

	@Autowired
	private Submodel submodel;
	
	@Autowired
    private ApplicationContext context;

	@Before
	public void awaitAssignment() throws InterruptedException {
		listener.awaitTopicAssignment();
	}

	@After
	public void assertGotTearDownMessage() throws InterruptedException {
		SubmodelEvent evt = listener.next(1, TimeUnit.MINUTES);
		Assert.assertNull(evt);
	}

	@Test
	public void testSubmodelEvents() throws InterruptedException {
		// we expect the "onStartup" submodel created event
		SubmodelEvent evt = listener.next();
		Assert.assertEquals(SubmodelEventType.SM_CREATED, evt.getType());
		Assert.assertEquals(submodel.getId(), evt.getId());
		Assert.assertEquals(submodel, evt.getSubmodel());
		Assert.assertNull(evt.getSmElementPath());
		Assert.assertNull(evt.getSmElement());
		
		// simulate closing		
		context.publishEvent(new ContextClosedEvent(context));
		evt = listener.next();
		Assert.assertEquals(SubmodelEventType.SM_DELETED, evt.getType());
		Assert.assertEquals(submodel.getId(), evt.getId());
		Assert.assertNull(evt.getSubmodel());
		Assert.assertNull(evt.getSmElementPath());
		Assert.assertNull(evt.getSmElement());
	}
	

}
