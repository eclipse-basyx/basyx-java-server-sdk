/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.submodelregistry.service.tests.integration;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

/**
 * Persistency TestSuite for SubmodelRegistry
 * 
 * @author mateusmolina
 */
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public abstract class PersistencyTestSuite {

	protected static final String DESC_ID = "TestDescriptor";

	protected abstract SubmodelRegistryStorage getStorage();

	protected abstract void restartComponent();

	@Test
	public void testSubmodelDescriptorPersistency() {
		SubmodelDescriptor expectedDescriptor = buildSimpleDescriptor();

		getStorage().insertSubmodelDescriptor(expectedDescriptor);

		restartComponent();

		SubmodelDescriptor actualDescriptor = getStorage().getSubmodelDescriptor(DESC_ID);

		assertEquals(expectedDescriptor, actualDescriptor);
	}

	static SubmodelDescriptor buildSimpleDescriptor() {
		return new SubmodelDescriptor(DESC_ID, List.of());
	}
}
