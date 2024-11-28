/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.aasregistry.service.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.events.RegistryEvent;
import org.eclipse.digitaltwin.basyx.aasregistry.service.events.RegistryEventSink;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.DescriptorFilter;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.RegistrationEventSendingAasRegistryStorage;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public abstract class BaseInterfaceTest {

	protected static final String IDENTIFICATION_2_3 = "identification_2.3";

	protected static final String IDENTIFICATION_2_2 = "identification_2.2";

	protected static final String IDENTIFICATION_NEW = "identification_new";

	protected static final String IDENTIFICATION_NEW_1 = "identification_new.1";

	protected static final String IDENTIFICATION_2_1 = "identification_2.1";

	protected static final String _2_UNKNOWN = "2.unknown";

	protected static final String UNKNOWN_1 = "unknown.1";

	protected static final String UNKNOWN = "unknown";

	protected static final String IDENTIFICATION_1 = "identification_1";

	protected static final String IDENTIFICATION_2 = "identification_2";
	
	protected static final String IDENTIFICATION_3 = "identification_3";

	private RegistryEventSink eventSink = Mockito.mock(RegistryEventSink.class);

	@Autowired
	public AasRegistryStorage baseStorage;

	protected RegistrationEventSendingAasRegistryStorage storage;

	@Rule
	public TestResourcesLoader testResourcesLoader = new TestResourcesLoader();
	
	
	protected RegistryEventSink getEventSink() {
		return eventSink;
	}

	protected void clearBaseStorage() {
		baseStorage.clear();
	}
	
	@Before
	public void setUp() throws IOException {
		storage = new RegistrationEventSendingAasRegistryStorage(baseStorage, eventSink);
		List<AssetAdministrationShellDescriptor> descriptors = testResourcesLoader.loadRepositoryDefinition(AssetAdministrationShellDescriptor.class);
		descriptors.forEach(baseStorage::insertAasDescriptor);
	}

	@After
	public void tearDown() {
		baseStorage.clear();
	}


	protected void assertNullPointerThrown(ThrowingCallable callable) {
		Throwable th = Assertions.catchThrowable(callable);
		assertThat(th).isInstanceOf(NullPointerException.class);
		verifyNoEventSent();
	}

	
	protected void verifyEventsSent() throws IOException {
 		List<RegistryEvent> events = testResourcesLoader.loadEvents();
		
		Mockito.verify(eventSink, Mockito.times(events.size())).consumeEvent(ArgumentMatchers.any(RegistryEvent.class));
		
		InOrder inOrder = Mockito.inOrder(eventSink);
		for (RegistryEvent eachEvent : events) {
			inOrder.verify(eventSink).consumeEvent(eachEvent);
		}
	}

	protected void verifyNoEventSent() {
		Mockito.verify(eventSink, Mockito.never()).consumeEvent(ArgumentMatchers.any(RegistryEvent.class));
	}

	protected List<AssetAdministrationShellDescriptor> getAllAasDescriptors() {
		return storage.getAllAasDescriptors(PaginationInfo.NO_LIMIT, new DescriptorFilter(null, null)).getResult();
	}
	

	protected List<AssetAdministrationShellDescriptor> getAllAasDescriptorsFiltered(AssetKind kind, String type) {
		return storage.getAllAasDescriptors(PaginationInfo.NO_LIMIT, new DescriptorFilter(kind, type)).getResult();
	}
	
	protected CursorResult<List<AssetAdministrationShellDescriptor>> getAllAasDescriptorsWithPagination(int limit, String cursor) {
		return storage.getAllAasDescriptors(new PaginationInfo(limit, cursor), new DescriptorFilter(null, null));
	}
	
	protected List<SubmodelDescriptor> getAllSubmodels(String id) {
		return storage.getAllSubmodels(id, PaginationInfo.NO_LIMIT).getResult();
	}
	
	protected CursorResult<List<SubmodelDescriptor>> getAllSubmodelsWithPagination(String aasId, int limit, String cursor) {
		return storage.getAllSubmodels(aasId, new PaginationInfo(limit, cursor));
	}
}