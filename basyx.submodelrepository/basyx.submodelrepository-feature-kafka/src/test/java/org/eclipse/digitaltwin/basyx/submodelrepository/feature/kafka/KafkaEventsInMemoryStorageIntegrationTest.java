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
package org.eclipse.digitaltwin.basyx.submodelrepository.feature.kafka;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Blob;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultBlob;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.kafka.KafkaAdapter;
import org.eclipse.digitaltwin.basyx.kafka.KafkaAdapters;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.SubmodelServiceTestComponent;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.TestSubmodels;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.model.SubmodelEvent;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events.model.SubmodelEventType;
import org.eclipse.digitaltwin.basyx.submodelservice.value.PropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
@ActiveProfiles("test-submodel")
@ContextConfiguration(classes = SubmodelServiceTestComponent.class)
@TestPropertySource(properties = { "basyx.backend=InMemory", "spring.kafka.bootstrap-servers=localhost:9092", KafkaSubmodelRepositoryFeature.FEATURENAME + ".preservationlevel=REMOVE_BLOB_VALUE",
		KafkaSubmodelRepositoryFeature.FEATURENAME + ".enabled=true", KafkaSubmodelRepositoryFeature.FEATURENAME + ".topic.name=submodel-events" })

public class KafkaEventsInMemoryStorageIntegrationTest {

	private static final String IDSHORT_BLOB = "blob";

	private static final String IDSHORT_LIST0 = "List0";

	private static final String ID_SM1 = "http://sm.id/1";

	private KafkaAdapter<SubmodelEvent> adapter = KafkaAdapters.getAdapter( "submodel-events", SubmodelEvent.class);
	
	private SubmodelRepository repo;

	@Autowired
	private KafkaSubmodelRepositoryFeature feature;

	@Autowired
	private SubmodelRepositoryFactory factory;

	@Autowired
	private JsonSerializer serializer;

	@Before
	public void init() {
		repo = feature.decorate(factory).create();
		adapter.skipMessages();
		cleanup();
	}

	@Test
	public void testSubmodelCreated() {
		Submodel sm = TestSubmodels.createSubmodel(ID_SM1, TestSubmodels.IDSHORT_PROP_0, "7");
		repo.createSubmodel(sm);

		SubmodelEvent evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SM_CREATED, evt.getType());
		Assert.assertEquals(ID_SM1, evt.getId());
		Assert.assertEquals(sm, evt.getSubmodel());
		Assert.assertNull(evt.getSmElementPath());
		Assert.assertNull(evt.getSmElement());
	}

	@Test
	public void testSubmodelUpdated() {
		Submodel sm = TestSubmodels.createSubmodel(ID_SM1, TestSubmodels.IDSHORT_PROP_0, "7");
		repo.createSubmodel(sm);
		SubmodelEvent evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SM_CREATED, evt.getType());

		Submodel smUpdated = TestSubmodels.createSubmodel(ID_SM1, TestSubmodels.IDSHORT_PROP_0, "9");
		repo.updateSubmodel(ID_SM1, smUpdated);

		evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SM_UPDATED, evt.getType());
		Assert.assertEquals(ID_SM1, evt.getId());
		Assert.assertEquals(smUpdated, evt.getSubmodel());
		Assert.assertNull(evt.getSmElementPath());
		Assert.assertNull(evt.getSmElement());
	}

	@Test
	public void testSubmodelPatched() {
		Submodel sm = TestSubmodels.createSubmodel(ID_SM1, TestSubmodels.IDSHORT_PROP_0, "7");
		repo.createSubmodel(sm);
		SubmodelEvent evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SM_CREATED, evt.getType());

		SubmodelElement elem1 = TestSubmodels.submodelElement(TestSubmodels.IDSHORT_PROP_1, "2");
		repo.patchSubmodelElements(ID_SM1, List.of(elem1));

		evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SM_UPDATED, evt.getType());
		Assert.assertEquals(ID_SM1, evt.getId());
		Submodel smPatched = new DefaultSubmodel.Builder().id(ID_SM1).idShort(TestSubmodels.IDSHORT_SM).submodelElements(List.of(elem1)).build();
		Assert.assertEquals(smPatched, evt.getSubmodel());
		Assert.assertNull(evt.getSmElementPath());
		Assert.assertNull(evt.getSmElement());
	}

	@Test
	public void testSetSubmodelElementValue() {
		PropertyValue value = new PropertyValue("111");
		Submodel sm = TestSubmodels.createSubmodel(TestSubmodels.ID_SM, TestSubmodels.IDSHORT_PROP_0, "7");
		repo.createSubmodel(sm);
		SubmodelEvent evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SM_CREATED, evt.getType());
		repo.setSubmodelElementValue(TestSubmodels.ID_SM, TestSubmodels.IDSHORT_PROP_0, value);
		evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SME_UPDATED, evt.getType());

		Assert.assertEquals(TestSubmodels.ID_SM, evt.getId());
		Assert.assertNull(evt.getSubmodel());
		Assert.assertEquals(TestSubmodels.IDSHORT_PROP_0, evt.getSmElementPath());
		Assert.assertEquals(TestSubmodels.submodelElement(TestSubmodels.IDSHORT_PROP_0, "111"), evt.getSmElement());
	}

	@Test
	public void testSubmodelDeleted() {
		Submodel sm = TestSubmodels.createSubmodel(ID_SM1, TestSubmodels.IDSHORT_PROP_0, "7");
		repo.createSubmodel(sm);
		SubmodelEvent evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SM_CREATED, evt.getType());

		repo.deleteSubmodel(ID_SM1);
		evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SM_DELETED, evt.getType());

		Assert.assertEquals(ID_SM1, evt.getId());
		Assert.assertNull(evt.getSubmodel());
		Assert.assertNull(evt.getSmElementPath());
		Assert.assertNull(evt.getSmElement());
	}

	@Test
	public void testSubmodelElementAdded() {
		Submodel sm = TestSubmodels.createSubmodel(ID_SM1, TestSubmodels.IDSHORT_PROP_0, "7");
		repo.createSubmodel(sm);
		SubmodelEvent evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SM_CREATED, evt.getType());

		SubmodelElement elem = TestSubmodels.submodelElement(TestSubmodels.IDSHORT_PROP_1, "88");
		repo.createSubmodelElement(ID_SM1, elem);

		evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SME_CREATED, evt.getType());
		Assert.assertEquals(ID_SM1, evt.getId());
		Assert.assertEquals(TestSubmodels.IDSHORT_PROP_1, evt.getSmElementPath());
		Assert.assertNull(evt.getSubmodel());
		Assert.assertEquals(elem, evt.getSmElement());
	}

	@Test
	public void testSubmodelElementUpdated() {
		Submodel sm = TestSubmodels.createSubmodel(ID_SM1, TestSubmodels.IDSHORT_PROP_0, "7");
		repo.createSubmodel(sm);
		SubmodelEvent evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SM_CREATED, evt.getType());

		SubmodelElement elem = TestSubmodels.submodelElement(TestSubmodels.IDSHORT_PROP_0, "88");
		repo.updateSubmodelElement(ID_SM1, TestSubmodels.IDSHORT_PROP_0, elem);

		evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SME_UPDATED, evt.getType());
		Assert.assertEquals(ID_SM1, evt.getId());
		Assert.assertEquals(TestSubmodels.IDSHORT_PROP_0, evt.getSmElementPath());
		Assert.assertNull(evt.getSubmodel());
		Assert.assertEquals(elem, evt.getSmElement());
	}

	@Test
	public void testSubmodelElementAddedUnderPath() {
		Submodel sm = TestSubmodels.createSubmodel(ID_SM1, TestSubmodels.IDSHORT_PROP_0, "7");
		repo.createSubmodel(sm);
		SubmodelEvent evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SM_CREATED, evt.getType());

		SubmodelElement elem = TestSubmodels.submodelElement(TestSubmodels.IDSHORT_PROP_1, "88");
		repo.createSubmodelElement(ID_SM1, TestSubmodels.IDSHORT_COLL, elem);
		evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SME_CREATED, evt.getType());
		Assert.assertEquals(ID_SM1, evt.getId());
		Assert.assertEquals(TestSubmodels.IDSHORT_COLL + "." + TestSubmodels.IDSHORT_PROP_1, evt.getSmElementPath());
		Assert.assertNull(evt.getSubmodel());
		Assert.assertEquals(elem, evt.getSmElement());
	}
	
	@Test
	public void testSubmodelElementAddedUnderListPath() {
		Submodel sm = TestSubmodels.createSubmodel(ID_SM1, TestSubmodels.IDSHORT_PROP_0, "7");
		sm.getSubmodelElements().add(new DefaultSubmodelElementList.Builder().idShort("List")
				.value(new DefaultProperty.Builder().idShort("P77").value("77").build())
				.build());
		repo.createSubmodel(sm);
		SubmodelEvent evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SM_CREATED, evt.getType());

		SubmodelElement elem = TestSubmodels.submodelElement(TestSubmodels.IDSHORT_PROP_1, "88");
		repo.createSubmodelElement(ID_SM1, "List", elem);
		evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SME_CREATED, evt.getType());
		Assert.assertEquals(ID_SM1, evt.getId());
		Assert.assertEquals("List[1]", evt.getSmElementPath());
		Assert.assertNull(evt.getSubmodel());
		Assert.assertEquals(elem, evt.getSmElement());
	}

	@Test
	public void testSubmodelElementAddedAndBlobValueNotPartOfTheEvent() {
		Submodel sm = TestSubmodels.submodel();
		repo.createSubmodel(sm);
		SubmodelEvent evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SM_CREATED, evt.getType());

		SubmodelElementList sme = new DefaultSubmodelElementList.Builder().idShort(IDSHORT_LIST0).value(new DefaultBlob.Builder().idShort(IDSHORT_BLOB).value(new byte[] { 1, 2, 3, 4 }).build()).build();
		repo.createSubmodelElement(TestSubmodels.ID_SM, TestSubmodels.IDSHORT_COLL, sme);

		SubmodelEvent evtCreated = adapter.next();
		Assert.assertEquals(SubmodelEventType.SME_CREATED, evtCreated.getType());

		SubmodelElementList expected = new DefaultSubmodelElementList.Builder().idShort(IDSHORT_LIST0).value(new DefaultBlob.Builder().idShort(IDSHORT_BLOB).build()).build();

		Assert.assertEquals(TestSubmodels.ID_SM, evtCreated.getId());
		Assert.assertEquals(TestSubmodels.IDSHORT_COLL + "." + IDSHORT_LIST0, evtCreated.getSmElementPath());
		Assert.assertEquals(expected, evtCreated.getSmElement());
		Assert.assertNull(evtCreated.getSubmodel());
	}

	@Test
	public void testSubmodelElementCreatedAndBlobValueNotPartOfTheEvent() {
		Submodel sm = TestSubmodels.submodel();
		repo.createSubmodel(sm);
		SubmodelEvent evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SM_CREATED, evt.getType());

		Blob blob = new DefaultBlob.Builder().idShort(IDSHORT_BLOB).value(new byte[] { 1, 2, 3, 4 }).build();
		repo.createSubmodelElement(TestSubmodels.ID_SM, blob);

		SubmodelEvent evtCreated = adapter.next();
		Assert.assertEquals(SubmodelEventType.SME_CREATED, evtCreated.getType());

		Blob expected = new DefaultBlob.Builder().idShort(IDSHORT_BLOB).build();

		Assert.assertEquals(TestSubmodels.ID_SM, evtCreated.getId());
		Assert.assertEquals(IDSHORT_BLOB, evtCreated.getSmElementPath());
		Assert.assertEquals(expected, evtCreated.getSmElement());
		Assert.assertNull(evtCreated.getSubmodel());
	}

	@Test
	public void testSubmodelElementDeleted() {
		Submodel sm = TestSubmodels.submodel();
		repo.createSubmodel(sm);
		SubmodelEvent evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SM_CREATED, evt.getType());

		Property prop = new DefaultProperty.Builder().idShort(IDSHORT_BLOB).value("4").build();
		repo.createSubmodelElement(TestSubmodels.ID_SM, prop);

		SubmodelEvent evtCreated = adapter.next();
		Assert.assertEquals(SubmodelEventType.SME_CREATED, evtCreated.getType());
		repo.deleteSubmodelElement(TestSubmodels.ID_SM, TestSubmodels.IDSHORT_PROP_TO_BE_REMOVED);

		SubmodelEvent evtDeleted = adapter.next();
		Assert.assertEquals(SubmodelEventType.SME_DELETED, evtDeleted.getType());

		Assert.assertEquals(TestSubmodels.ID_SM, evtDeleted.getId());
		Assert.assertEquals(TestSubmodels.IDSHORT_PROP_TO_BE_REMOVED, evtDeleted.getSmElementPath());
		Assert.assertNull(evtDeleted.getSmElement());
		Assert.assertNull(evtDeleted.getSubmodel());
	}

	@Test
	public void testGetterAreWorking() throws ElementDoesNotExistException, SerializationException {
		Submodel expectedSm = TestSubmodels.submodel();
		repo.createSubmodel(expectedSm);
		SubmodelEvent evt = adapter.next();
		Assert.assertEquals(SubmodelEventType.SM_CREATED, evt.getType());

		List<Submodel> result = repo.getAllSubmodels(new PaginationInfo(null, null)).getResult();
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(expectedSm, result.get(0));
		List<SubmodelElement> expectedElems = expectedSm.getSubmodelElements().stream().sorted(Comparator.comparing(SubmodelElement::getIdShort)).collect(Collectors.toList());
		Assert.assertEquals(expectedElems, repo.getSubmodelElements(TestSubmodels.ID_SM, new PaginationInfo(null, null)).getResult());

		Assert.assertEquals(expectedSm, repo.getSubmodel(TestSubmodels.ID_SM));

		SubmodelValueOnly smvOnly = new SubmodelValueOnly(expectedSm.getSubmodelElements());

		Assert.assertEquals(serializer.write(smvOnly), serializer.write(repo.getSubmodelByIdValueOnly(TestSubmodels.ID_SM)));

		Submodel expectedMetaData = new DefaultSubmodel.Builder().id(TestSubmodels.ID_SM).submodelElements((List<SubmodelElement>) null).idShort(TestSubmodels.IDSHORT_SM).build();
		Assert.assertEquals(expectedMetaData, repo.getSubmodelByIdMetadata(TestSubmodels.ID_SM));

		Assert.assertEquals(new DefaultSubmodelElementCollection.Builder().idShort(TestSubmodels.IDSHORT_COLL).build(), repo.getSubmodelElement(TestSubmodels.ID_SM, TestSubmodels.IDSHORT_COLL));
	}

	@Test
	public void testFeatureIsEnabled() {
		Assert.assertTrue(feature.isEnabled());
	}

	public void cleanup() {
		if (repo != null) {
			for (Submodel sm : repo.getAllSubmodels(new PaginationInfo(null, null)).getResult()) {
				repo.deleteSubmodel(sm.getId());
				SubmodelEvent evt = adapter.next();
				Assert.assertEquals(SubmodelEventType.SM_DELETED, evt.getType());
				Assert.assertEquals(sm.getId(), evt.getId());
			}
		}
		adapter.assertNoAdditionalMessages();

	}

	@After
	public void tearDown() {
		cleanup();
	}

}
