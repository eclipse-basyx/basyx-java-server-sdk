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
package org.eclipse.digitaltwin.basyx.submodelrepository.component;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.kafka.KafkaAdapter;
import org.eclipse.digitaltwin.basyx.kafka.KafkaAdapters;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.kafka.KafkaSubmodelRepositoryFeature;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.model.SubmodelEvent;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.model.SubmodelEventType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author sonnenberg (DFKI GmbH)
 */
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = { "org.eclipse.digitaltwin.basyx" })
@ContextConfiguration(classes = { SubmodelRepositoryComponent.class })
@RunWith(SpringRunner.class)
@TestPropertySource(properties = { "basyx.feature.kafka.enabled=true", "spring.kafka.bootstrap-servers=PLAINTEXT_HOST://localhost:9092", KafkaSubmodelRepositoryFeature.FEATURENAME + ".enabled=true",
		KafkaSubmodelRepositoryFeature.FEATURENAME + ".topic.name=submodel-events" })

public class SubmodelKafkaFeatureEnabledSmokeTest {

	private static KafkaAdapter<SubmodelEvent> adapter = KafkaAdapters.getAdapter("submodel-events", SubmodelEvent.class);


	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private JsonSerializer serializer;

	
	@Before
	public void setUp() {
		adapter.skipMessages();
	}
	@Test
	public void testAasCreatedEvent() throws SerializationException {
		Submodel sm = new DefaultSubmodel.Builder().id("http://sm.id/1").build();
		HttpEntity<String> entity = createHttpEntity(sm);
		Assert.assertTrue(restTemplate.exchange(createEndpointUrl(), HttpMethod.POST, entity, String.class).getStatusCode().is2xxSuccessful());
		SubmodelEvent event = adapter.next();
		Assert.assertEquals(SubmodelEventType.SM_CREATED, event.getType());
		Assert.assertEquals(sm.getId(), event.getId());
		Assert.assertEquals(sm, event.getSubmodel());
	}

	@After
	public void cleanup() {
		adapter.assertNoAdditionalMessages();

	}

	private String createEndpointUrl() {
		return "http://localhost:" + port + "/submodels";
	}

	private HttpEntity<String> createHttpEntity(Submodel sm) throws SerializationException {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
		return new HttpEntity<>(serializer.write(sm), headers);
	}
}
