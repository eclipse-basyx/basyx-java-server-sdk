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


package org.eclipse.digitaltwin.basyx.aasservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.junit.Before;
import org.junit.Test;

/**
 * Testsuite for implementations of the AasService interface
 * 
 * @author schnicke
 *
 */
public abstract class AasServiceSuite {

	private AasService aasService;
	
	private AssetAdministrationShell aas;

	protected abstract AasServiceFactory getAASServiceFactory();

	@Before
	public void initSuite() {
		aas = DummyAssetAdministrationShell.getDummyShell();
		aasService = getAASServiceFactory().create(aas);
	}

	@Test
	public void aasRetrieval() {
		assertEquals(aas, aasService.getAAS());
	}

	@Test
	public void getSubmodelReference() {
		DummyAssetAdministrationShell.addDummySubmodelReference(aas);
		List<Reference> submodelReferences = aasService.getSubmodelReferences();
		Reference submodelReference = getFirstSubmodelReference(submodelReferences);
		assertEquals(DummyAssetAdministrationShell.submodelReference, submodelReference);
	}

	@Test
	public void addSubmodelReference() {
		Submodel submodel = createNewSubmodel();

		aasService.addSubmodelReference(submodel.getSemanticId());

		List<Reference> submodelReferences = aasService.getSubmodelReferences();

		Reference submodelReference = getFirstSubmodelReference(submodelReferences);

		assertTrue(
				submodelReference.getKeys().stream().filter(ref -> ref.getValue() == "testKey").findAny().isPresent());
	}

	@Test
	public void removeSubmodelReference() {
		DummyAssetAdministrationShell.addDummySubmodelReference(aas);
		List<Reference> submodelReferences = aasService.getSubmodelReferences();
		aasService.removeSubmodelReference(DummyAssetAdministrationShell.SUBMODEL_ID);
		assertEquals(0, submodelReferences.size());
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void removeNonExistingSubmodelReference() {
		aasService.removeSubmodelReference("doesNotMatter");
	}

	private Reference getFirstSubmodelReference(List<Reference> submodelReferences) {
		return submodelReferences.get(0);
	}

	private DefaultSubmodel createNewSubmodel() {
		return new DefaultSubmodel.Builder()
				.semanticId(
						new DefaultReference.Builder().keys(new DefaultKey.Builder().value("testKey").build()).build())
				.build();
	}
}
