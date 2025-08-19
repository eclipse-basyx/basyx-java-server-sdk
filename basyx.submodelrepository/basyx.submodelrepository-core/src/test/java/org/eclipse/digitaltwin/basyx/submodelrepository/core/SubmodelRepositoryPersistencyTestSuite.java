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

package org.eclipse.digitaltwin.basyx.submodelrepository.core;

import org.apache.commons.io.IOUtils;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.DummySubmodelFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceHelper;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Persistency TestSuite for {@link SubmodelRepository}
 * 
 * @author danish
 */
public abstract class SubmodelRepositoryPersistencyTestSuite {
	protected abstract SubmodelRepository getSubmodelRepository();

	protected abstract void restartComponent();

	@Test
	public void submodelIsPersisted() {
		Submodel expectedSubmodel = DummySubmodelFactory.createSimpleDataSubmodel();
		getSubmodelRepository().createSubmodel(expectedSubmodel);

		restartComponent();

		Submodel actualSubmodel = getSubmodelRepository().getSubmodel(DummySubmodelFactory.SUBMODEL_SIMPLE_DATA_ID);

		assertEquals(expectedSubmodel, actualSubmodel);
	}

	@Test
	public void fileIsPersisted() throws IOException {
		Submodel submodel = DummySubmodelFactory.createSubmodelWithFileElement();
		getSubmodelRepository().createSubmodel(submodel);

		getSubmodelRepository().setFileValue(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT, "dummyFile.jpeg", "image/jpeg", createDummyImageIS_A());

		restartComponent();

		InputStream actualThumbnailIs = new FileInputStream(getSubmodelRepository().getFileByPathSubmodel(DummySubmodelFactory.SUBMODEL_FOR_FILE_TEST, SubmodelServiceHelper.SUBMODEL_TECHNICAL_DATA_FILE_ID_SHORT));

		InputStream expectedThumbnailIs = createDummyImageIS_A();

		assertTrue(IOUtils.contentEquals(expectedThumbnailIs, actualThumbnailIs));
	}

	private InputStream createDummyImageIS_A() throws IOException {
		return createDummyImageIS(0x000000);
	}

	private InputStream createDummyImageIS(int color) throws IOException {
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

		image.setRGB(0, 0, 0x000000);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "jpeg", baos);

		byte[] imageData = baos.toByteArray();
		return new ByteArrayInputStream(imageData);
	}
}
