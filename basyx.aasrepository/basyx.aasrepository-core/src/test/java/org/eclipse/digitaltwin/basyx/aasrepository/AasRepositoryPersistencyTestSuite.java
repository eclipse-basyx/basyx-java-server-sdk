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

package org.eclipse.digitaltwin.basyx.aasrepository;

import org.apache.commons.io.IOUtils;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceSuite;
import org.eclipse.digitaltwin.basyx.aasservice.DummyAssetAdministrationShellFactory;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Persistency TestSuite for {@link AasRepository}
 * 
 * @author danish
 */
public abstract class AasRepositoryPersistencyTestSuite {

	protected abstract AasRepository getAasRepository();

	protected abstract void restartComponent();

	@Test
	public void shellIsPersisted() {
		AssetAdministrationShell expectedAas = DummyAasFactory.createAasWithSubmodelReference();
		getAasRepository().createAas(expectedAas);

		restartComponent();

		AssetAdministrationShell actualAas = getAasRepository().getAas(DummyAasFactory.AASWITHSUBMODELREF_ID);

		assertEquals(expectedAas, actualAas);
	}

	@Test
	public void thumbnailIsPersisted() throws IOException {
		AssetAdministrationShell shell = DummyAssetAdministrationShellFactory.create();
		getAasRepository().createAas(shell);

		getAasRepository().setThumbnail("arbitrary", "dummyImgA.jpeg", "image/jpeg", AasServiceSuite.createDummyImageIS_A());

		restartComponent();

		InputStream actualThumbnailIs = new FileInputStream(getAasRepository().getThumbnail(shell.getId()));

		InputStream expectedThumbnailIs = AasServiceSuite.createDummyImageIS_A();

		assertTrue(IOUtils.contentEquals(expectedThumbnailIs, actualThumbnailIs));
	}
}
