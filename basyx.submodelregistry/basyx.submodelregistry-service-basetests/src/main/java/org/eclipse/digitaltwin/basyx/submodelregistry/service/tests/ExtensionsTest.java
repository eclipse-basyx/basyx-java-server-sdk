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

import java.util.List;
import java.util.Set;

import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.events.RegistryEvent;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public abstract class ExtensionsTest extends BaseInterfaceTest {

	@Test
	public void whenDeleteAllSubmodels_thenEventsAreSendAndSubmodelsRemoved() {
		List<SubmodelDescriptor> oldState = getAllSubmodels();
		assertThat(oldState).isNotEmpty();
		Set<String> idsOfRemovedDescriptors = storage.clear();
		// listener is invoked for each removal
		Mockito.verify(getEventSink(), Mockito.times(idsOfRemovedDescriptors.size())).consumeEvent(ArgumentMatchers.any(RegistryEvent.class));
		List<SubmodelDescriptor> newState = getAllSubmodels();
		assertThat(newState).isEmpty();
	}	
}