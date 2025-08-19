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
package org.eclipse.digitaltwin.basyx.aasrepository.component;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.KafkaAasRepositoryFeature;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.TestApplication;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events.model.AasEvent;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events.model.AasEventType;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.kafka.KafkaAdapter;
import org.eclipse.digitaltwin.basyx.kafka.KafkaAdapters;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author sonnenberg (DFKI GmbH)
 */
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = { "org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka" })
@RunWith(SpringRunner.class)
@TestPropertySource(properties = { "basyx.feature.kafka.enabled=true", "spring.kafka.bootstrap-servers=PLAINTEXT_HOST://localhost:9092", KafkaAasRepositoryFeature.FEATURENAME + "kafka.enabled=true",
		KafkaAasRepositoryFeature.FEATURENAME + ".topic.name=aas-events" })
public class KafkaFeatureEnabledSmokeTest {

	private static KafkaAdapter<AasEvent> adapter = KafkaAdapters.getAdapter(TestApplication.KAFKA_AAS_TOPIC, AasEvent.class);

	
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private JsonSerializer serializer;

	@Autowired
	private AasRepository aasRepo;

	
	
	@Before
	public void init() {
		adapter.skipMessages();
		cleanup();

	}
	
	@After
	public void dispose() {
		cleanup();
	}

	public void cleanup() {
		for (AssetAdministrationShell aas : aasRepo.getAllAas(null, null, new PaginationInfo(null, null)).getResult()) {
			aasRepo.deleteAas(aas.getId());
			adapter.next();
		}
		adapter.assertNoAdditionalMessages();
	}

	@Test
	public void testAasCreatedEvent() throws SerializationException {
		AssetAdministrationShell shell = new DefaultAssetAdministrationShell.Builder().id("http://aas.id/1").idShort("1").build();
		HttpEntity<String> entity = createHttpEntity(shell);
		restTemplate.exchange(createEndpointUrl(), HttpMethod.POST, entity, String.class);
		AasEvent event = adapter.next();
		Assert.assertEquals(AasEventType.AAS_CREATED, event.getType());
		Assert.assertEquals(shell.getId(), event.getId());
		Assert.assertEquals(shell, event.getAas());
	}

	private String createEndpointUrl() {
		return "http://localhost:" + port + "/shells";
	}

	private HttpEntity<String> createHttpEntity(AssetAdministrationShell shell) throws SerializationException {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
		return new HttpEntity<>(serializer.write(shell), headers);
	}
}
