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

package org.eclipse.digitaltwin.basyx.conceptdescriptionservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.junit.Test;

/**
 * Testsuite for implementations of the ConceptDescriptionService interface
 * 
 * @author danish
 *
 */
public abstract class ConceptDescriptionServiceSuite {

	protected abstract ConceptDescriptionService getConceptDescriptionService(ConceptDescription conceptDescription);

	@Test
	public void getConceptDescription() {
		ConceptDescription conceptDescription = DummyConceptDescriptionFactory.createConceptDescription();
		ConceptDescriptionService cdService = getConceptDescriptionService(conceptDescription);

		assertEquals(conceptDescription, cdService.getConceptDescription());
	}

	@Test
	public void getIsCaseOf() {
		ConceptDescription conceptDescription = DummyConceptDescriptionFactory.createConceptDescription();
		ConceptDescriptionService cdService = getConceptDescriptionService(conceptDescription);

		assertEquals(conceptDescription.getIsCaseOf(), cdService.getIsCaseOf());
	}

	@Test
	public void setIsCaseOf() {
		ConceptDescription conceptDescription = DummyConceptDescriptionFactory.createConceptDescription();
		ConceptDescriptionService cdService = getConceptDescriptionService(conceptDescription);
		
		List<Reference> expectedIsCaseOf = prepareDummyIsCaseOf();
		
		cdService.setIsCaseOf(expectedIsCaseOf);

		List<Reference> actualIsCaseOf = cdService.getIsCaseOf();
		
		assertTrue(expectedIsCaseOf.containsAll(actualIsCaseOf));
	}

	private List<Reference> prepareDummyIsCaseOf() {
		List<Key> firstKeys = Arrays
				.asList(new DefaultKey.Builder().type(KeyTypes.CAPABILITY).value("Capability").build());
		List<Key> secondKeys = Arrays
				.asList(new DefaultKey.Builder().type(KeyTypes.BLOB).value("Blob").build());
		Reference firstReference = new DefaultReference.Builder().type(ReferenceTypes.GLOBAL_REFERENCE)
				.keys(firstKeys).build();
		Reference secondReference = new DefaultReference.Builder()
				.type(ReferenceTypes.MODEL_REFERENCE).keys(secondKeys).build();
		
		return Arrays.asList(firstReference, secondReference);
	}

}
