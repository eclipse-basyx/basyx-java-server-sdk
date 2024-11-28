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
package org.eclipse.digitaltwin.basyx.submodelregistry.service.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.events.RegistryEvent;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.events.RegistryEventSink;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.RegistrationEventSendingSubmodelRegistryStorage;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;
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

	protected static final String SM_ID_5 = "sm5";
	protected static final String SM_ID_3 = "sm3";
	protected static final String SM_ID_2 = "sm2";
	protected static final String UNKNOWN_SM_ID = "unknown";
	
	private RegistryEventSink eventSink = Mockito.mock(RegistryEventSink.class);

	@Autowired
	public SubmodelRegistryStorage baseStorage;

	protected RegistrationEventSendingSubmodelRegistryStorage storage;

	@Rule
	public TestResourcesLoader testResourcesLoader = new TestResourcesLoader();

	protected RegistryEventSink getEventSink() {
		return eventSink;
	}

	@Before
	public void setUp() throws IOException {
		storage = new RegistrationEventSendingSubmodelRegistryStorage(baseStorage, eventSink);
		List<SubmodelDescriptor> submodels = testResourcesLoader.loadRepositoryDefinition(SubmodelDescriptor.class);
		submodels.forEach(baseStorage::insertSubmodelDescriptor);
	}

	@After
	public void tearDown() {
		storage.clear();
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

	protected List<SubmodelDescriptor> getAllSubmodels() {
		return storage.getAllSubmodelDescriptors(PaginationInfo.NO_LIMIT).getResult();
	}

	protected CursorResult<List<SubmodelDescriptor>> getAllSubmodelsWithPagination(int limit, String cursor) {
		return storage.getAllSubmodelDescriptors(new PaginationInfo(limit, cursor));
	}

}