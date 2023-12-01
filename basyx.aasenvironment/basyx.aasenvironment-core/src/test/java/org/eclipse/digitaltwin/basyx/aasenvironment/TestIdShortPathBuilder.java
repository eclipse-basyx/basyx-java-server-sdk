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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.InvalidIdShortPathElementsException;

import static org.eclipse.digitaltwin.basyx.aasenvironment.IdShortPathTestHelper.*;

import org.junit.Test;

/**
 * Tests the behaviour of {@link IdShortPathBuilder}
 * 
 * @author danish
 */
public class TestIdShortPathBuilder {
	
	private static final String EXPECTED_IDSHORTPATH_SCENARIO_1 = "SML_ONE[2][0][1]";
	private static final String EXPECTED_IDSHORTPATH_SCENARIO_2 = "SMC_TWO.SMC1.SMC2.File1_TWO";
	private static final String EXPECTED_IDSHORTPATH_SCENARIO_3 = "SML_THREE[2].SMC1.SML1[1]";
	private static final String EXPECTED_IDSHORTPATH_SCENARIO_4 = "SMC_FOUR.SML0[1][1].File1_FOUR";
	private static final String EXPECTED_IDSHORTPATH_SCENARIO_5 = "File1_FIVE";
	
	@Test
	public void scenario1() {
		IdShortPathBuilder builder = new IdShortPathBuilder(createScenario1Element());
		
		String actualIdShortPath = builder.build();
		
		assertEquals(EXPECTED_IDSHORTPATH_SCENARIO_1, actualIdShortPath);
	}
	
	@Test
	public void scenario2() {
		IdShortPathBuilder builder = new IdShortPathBuilder(createScenario2Element());
		
		String actualIdShortPath = builder.build();
		
		assertEquals(EXPECTED_IDSHORTPATH_SCENARIO_2, actualIdShortPath);
	}
	
	@Test
	public void scenario3() {
		IdShortPathBuilder builder = new IdShortPathBuilder(createScenario3Element());
		
		String actualIdShortPath = builder.build();
		
		assertEquals(EXPECTED_IDSHORTPATH_SCENARIO_3, actualIdShortPath);
	}
	
	@Test
	public void scenario4() {
		IdShortPathBuilder builder = new IdShortPathBuilder(createScenario4Element());
		
		String actualIdShortPath = builder.build();
		
		assertEquals(EXPECTED_IDSHORTPATH_SCENARIO_4, actualIdShortPath);
	}
	
	@Test
	public void scenario5() {
		IdShortPathBuilder builder = new IdShortPathBuilder(createScenario5Element());
		
		String actualIdShortPath = builder.build();
		
		assertEquals(EXPECTED_IDSHORTPATH_SCENARIO_5, actualIdShortPath);
	}
	
	@Test(expected = InvalidIdShortPathElementsException.class)
	public void createIdShortPathBuilderWithInvalidArgument() {
		List<SubmodelElement> invalidIdShortPathElements = new ArrayList<>();
		
		new IdShortPathBuilder(invalidIdShortPathElements);
	}

}
