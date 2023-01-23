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
package org.eclipse.digitaltwin.basyx.aasrepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.junit.Before;
import org.junit.Test;

/**
 * Testsuite for implementations of the AasRepository interface
 * 
 * @author schnicke
 *
 */
public abstract class AasRepositorySuite {

	private AssetAdministrationShell aas1;

	private static final String aasIdWithNeedForEncoding = "aas1/s";

	private AssetAdministrationShell aas2;
	private static final String aas2Id = "aas2";

	private AasRepository aasRepo;

	protected abstract AasRepositoryFactory getAasRepositoryFactory();

	@Before
	public void initSuite() {
		aasRepo = getAasRepositoryFactory().create();
		createAASDummies();
	}

	private void createAASDummies() {
		aas1 = new DefaultAssetAdministrationShell.Builder()
				.id(aasIdWithNeedForEncoding)
				.build();

		aas2 = new DefaultAssetAdministrationShell.Builder()
				.id(aas2Id)
				.build();
	}

	@Test
	public void allAasRetrieval() throws Exception {
		aasRepo.createAas(aas1);
		aasRepo.createAas(aas2);

		Collection<AssetAdministrationShell> coll = aasRepo.getAllAas();
		assertEquals(2, coll.size());

		assertTrue(coll.contains(aas1));
		assertTrue(coll.contains(aas2));
	}

	@Test
	public void getAASByIdentifier() throws CollidingIdentifierException, ElementDoesNotExistException {
		aasRepo.createAas(aas1);
		AssetAdministrationShell retrieved = aasRepo.getAas(aas1.getId());
		assertEquals(aas1, retrieved);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getNonExistingAASByIdentifier() throws ElementDoesNotExistException {
		aasRepo.getAas("nonexisting");
	}

	@Test(expected = CollidingIdentifierException.class)
	public void collidingAASIdentifiers() throws CollidingIdentifierException {
		aasRepo.createAas(aas1);
		aasRepo.createAas(aas1);
	}
}
