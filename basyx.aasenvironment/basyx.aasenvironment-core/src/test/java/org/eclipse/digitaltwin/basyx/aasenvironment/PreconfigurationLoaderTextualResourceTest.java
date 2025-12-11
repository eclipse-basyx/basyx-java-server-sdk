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

import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.basyx.aasenvironment.base.DefaultAASEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.preconfiguration.AasEnvironmentPreconfigurationLoader;
import org.eclipse.digitaltwin.basyx.core.exceptions.ZipBombException;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests the behavior of {@link AasEnvironmentPreconfigurationLoader}
 * 
 * @author sonnenberg, mateusmolina
 *
 */
public class PreconfigurationLoaderTextualResourceTest extends AasEnvironmentLoaderTest {

	@Override
	protected void loadRepositories(List<String> pathsToLoad) throws IOException, InvalidFormatException, DeserializationException, ZipBombException {
		AasEnvironmentPreconfigurationLoader envLoader = new AasEnvironmentPreconfigurationLoader(rLoader, pathsToLoad);
		envLoader.loadPreconfiguredEnvironments(new DefaultAASEnvironment(aasRepository, submodelRepository, conceptDescriptionRepository));
	}

	@Test
	public void testWithEmptyResource_NoElementsAreDeployed() throws InvalidFormatException, IOException, DeserializationException, ZipBombException {
		loadRepositories(List.of());
		Assert.assertTrue(aasRepository.getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().isEmpty());
		Assert.assertTrue(submodelRepository.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().isEmpty());
		Assert.assertTrue(conceptDescriptionRepository.getAllConceptDescriptions(PaginationInfo.NO_LIMIT).getResult().isEmpty());

		Mockito.verify(aasRepository, Mockito.never()).createAas(Mockito.any());
		Mockito.verify(aasRepository, Mockito.never()).updateAas(Mockito.anyString(), Mockito.any());

		Mockito.verify(submodelRepository, Mockito.never()).createSubmodel(Mockito.any());
		Mockito.verify(submodelRepository, Mockito.never()).createSubmodel(Mockito.any());
	}
}
