/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasenvironment;

import static org.eclipse.digitaltwin.basyx.aasenvironment.IdShortPathTestHelper.*;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.junit.Test;

/**
 * Tests the behaviour of {@link FileElementPathCollector}
 * 
 * @author danish
 */
public class TestFileElementPathCollector {
	
	@Test
	public void findIdShortPath() {
		List<List<SubmodelElement>> expectedIdShortPathElements = new ArrayList<List<SubmodelElement>>();
		expectedIdShortPathElements.addAll(Arrays.asList(createScenario1Element(), createScenario2Element(), createScenario3Element(), createScenario4Element(), createScenario5Element()));
		
		List<List<SubmodelElement>> actualIdShortPathElements = new FileElementPathCollector(createSubmodel("DummySubmodel")).collect();
		
		assertTrue(expectedIdShortPathElements.containsAll(actualIdShortPathElements));
		assertTrue(actualIdShortPathElements.containsAll(expectedIdShortPathElements));
	}
	
	@Test
	public void findIdShortPathWithMultipleFilesInPath() {
		List<List<SubmodelElement>> expectedIdShortPathElements = createScenarioWithMultipleFileElements();
		
		List<List<SubmodelElement>> actualIdShortPathElements = new FileElementPathCollector(createSubmodelWithMultipleFileElements("DummySubmodel")).collect();
		
		assertTrue(expectedIdShortPathElements.containsAll(actualIdShortPathElements));
		assertTrue(actualIdShortPathElements.containsAll(expectedIdShortPathElements));
	}
	
}
