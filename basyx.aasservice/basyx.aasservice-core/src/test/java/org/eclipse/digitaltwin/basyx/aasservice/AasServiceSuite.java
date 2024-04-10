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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.Test;

/**
 * Testsuite for implementations of the AasService interface
 * 
 * @author schnicke, mateusmolina
 *
 */
public abstract class AasServiceSuite {

	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, null);

	protected abstract AasService getAasService(AssetAdministrationShell shell);

	@Test
	public void getAas() {
		AssetAdministrationShell expected = DummyAssetAdministrationShellFactory.create();
		AasService aasService = getAasService(expected);
		assertEquals(expected, aasService.getAAS());
	}

	@Test
	public void getSubmodelReference() {
		AssetAdministrationShell expected = DummyAssetAdministrationShellFactory.create();
		DummyAssetAdministrationShellFactory.addDummySubmodelReference(expected);
		AasService aasService = getAasService(expected);

		List<Reference> submodelReferences = aasService.getSubmodelReferences(NO_LIMIT_PAGINATION_INFO).getResult();
		Reference submodelReference = getFirstSubmodelReference(submodelReferences);
		assertEquals(DummyAssetAdministrationShellFactory.submodelReference, submodelReference);
	}

	@Test
	public void addSubmodelReference() {
		AssetAdministrationShell expected = DummyAssetAdministrationShellFactory.create();
		AasService aasService = getAasService(expected);

		Submodel submodel = createDummySubmodel();

		aasService.addSubmodelReference(submodel.getSemanticId());

		List<Reference> submodelReferences = aasService.getSubmodelReferences(NO_LIMIT_PAGINATION_INFO).getResult();

		Reference submodelReference = getFirstSubmodelReference(submodelReferences);

		assertTrue(
				submodelReference.getKeys().stream().filter(ref -> ref.getValue().equals("testKey")).findAny().isPresent());
	}

	@Test
	public void removeSubmodelReference() {
		AssetAdministrationShell expected = DummyAssetAdministrationShellFactory.create();
		DummyAssetAdministrationShellFactory.addDummySubmodelReference(expected);
		AasService aasService = getAasService(expected);

		List<Reference> submodelReferences = aasService.getSubmodelReferences(NO_LIMIT_PAGINATION_INFO).getResult();
		aasService.removeSubmodelReference(DummyAssetAdministrationShellFactory.SUBMODEL_ID);
		submodelReferences = aasService.getSubmodelReferences(NO_LIMIT_PAGINATION_INFO).getResult();
		assertEquals(0, submodelReferences.size());
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void removeNonExistingSubmodelReference() {
		AssetAdministrationShell shell = DummyAssetAdministrationShellFactory.create();
		AasService aasService = getAasService(shell);
		aasService.removeSubmodelReference("doesNotMatter");
	}

	@Test
	public void getAssetInformation() {
		AssetAdministrationShell shell = DummyAssetAdministrationShellFactory.create();
		AasService aasService = getAasService(shell);
		assertEquals(shell.getAssetInformation(), aasService.getAssetInformation());
	}
	
	@Test
	public void setAssetInformation() {
		AssetAdministrationShell shell = DummyAssetAdministrationShellFactory.create();
		AasService aasService = getAasService(shell);

		AssetInformation assetInfo = createDummyAssetInformation();
		aasService.setAssetInformation(assetInfo);
		assertEquals(assetInfo, aasService.getAssetInformation());
	}

	@Test
	public void getPaginatedSubmodelReferencesPaginated() {
		List<Reference> submodelReferences = createDummyReferences();
		AssetAdministrationShell shell = new DefaultAssetAdministrationShell.Builder().id("paginatedAAS")
				.submodels(submodelReferences).build();
		AasService aasService = getAasService(shell);
		PaginationInfo pInfo = new PaginationInfo(1, "");
		CursorResult<List<Reference>> paginatedReferences = aasService.getSubmodelReferences(pInfo);
		assertEquals(1, paginatedReferences.getResult().size());
		assertEquals(submodelReferences.stream().findFirst().get(),
				paginatedReferences.getResult().stream().findFirst().get());
	}

	@Test
	public void updateThumbnail() throws FileNotFoundException, IOException {
		AssetAdministrationShell shell = DummyAssetAdministrationShellFactory.createWithDefaultThumbnail();
		AasService aasService = getAasService(shell);

		aasService.setThumbnail("dummyImgA.jpeg", "", createDummyImageIS_A());

		InputStream actualThumbnailIs = new FileInputStream(aasService.getThumbnail());

		InputStream expectedThumbnail = createDummyImageIS_A();

		assertTrue(IOUtils.contentEquals(expectedThumbnail, actualThumbnailIs));
	}

	@Test
	public void setThumbnail() throws FileNotFoundException, IOException {
		AssetAdministrationShell shell = DummyAssetAdministrationShellFactory.create();
		AasService aasService = getAasService(shell);

		aasService.setThumbnail("dummyImgA.jpeg", "", createDummyImageIS_A());

		InputStream actualThumbnailIs = new FileInputStream(aasService.getThumbnail());

		InputStream expectedThumbnail = createDummyImageIS_A();

		assertTrue(IOUtils.contentEquals(expectedThumbnail, actualThumbnailIs));
	}

	@Test
	public void getThumbnail() throws IOException {
		AssetAdministrationShell shell = DummyAssetAdministrationShellFactory.create();
		AasService aasService = getAasService(shell);

		aasService.setThumbnail("dummyImgA.jpeg", "", createDummyImageIS_A());

		InputStream actualThumbnailIs = new FileInputStream(aasService.getThumbnail());

		InputStream expectedThumbnail = createDummyImageIS_A();

		assertTrue(IOUtils.contentEquals(expectedThumbnail, actualThumbnailIs));
	}

	@Test(expected = FileDoesNotExistException.class)
	public void getNonExistingThumbnail() {
		AssetAdministrationShell shell = DummyAssetAdministrationShellFactory.create();
		AasService aasService = getAasService(shell);

		aasService.getThumbnail();
	}

	@Test(expected = FileDoesNotExistException.class)
	public void deleteThumbnail() throws FileNotFoundException, IOException {
		AssetAdministrationShell shell = DummyAssetAdministrationShellFactory.createWithDefaultThumbnail();
		AasService aasService = getAasService(shell);
		
		aasService.deleteThumbnail();

		aasService.getThumbnail();
	}

	@Test(expected = FileDoesNotExistException.class)
	public void deleteNonExistingThumbnail() throws IOException {
		AssetAdministrationShell shell = DummyAssetAdministrationShellFactory.create();
		AasService aasService = getAasService(shell);

		aasService.deleteThumbnail();
	}

	private AssetInformation createDummyAssetInformation() {
		AssetInformation assetInfo = new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE)
				.globalAssetId("assetIDTestKey")
				.build();
		return assetInfo;
	}

	private Reference getFirstSubmodelReference(List<Reference> submodelReferences) {
		return submodelReferences.get(0);
	}

	private DefaultSubmodel createDummySubmodel() {
		return new DefaultSubmodel.Builder()
				.semanticId(
						new DefaultReference.Builder().keys(new DefaultKey.Builder().value("testKey").build()).build())
				.build();
	}

	/**
	 * @return 5 References each with value of smRef_(0-4)
	 */
	private List<Reference> createDummyReferences() {
		List<Reference> referenceList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Reference ref = new DefaultReference.Builder().type(ReferenceTypes.MODEL_REFERENCE)
					.keys(new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value("smRef_" + i).build()).build();
			referenceList.add(ref);
		}
		return referenceList;
	}

	public static InputStream createDummyImageIS_A() throws IOException {
		return createDummyImageIS(0x000000);
	}

	private static InputStream createDummyImageIS(int color) throws IOException {
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

		image.setRGB(0, 0, 0x000000);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "jpeg", baos);

		byte[] imageData = baos.toByteArray();
		return new ByteArrayInputStream(imageData);
	}
}
