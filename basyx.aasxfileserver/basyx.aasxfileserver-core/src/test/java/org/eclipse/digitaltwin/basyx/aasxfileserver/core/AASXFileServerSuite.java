/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasxfileserver.core;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.digitaltwin.aas4j.v3.model.PackageDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultPackageDescription;
import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServer;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.Test;

/**
 * Testsuite for implementations of the {@link AASXFileServer} interface
 * 
 * @author chaithra, zielstor, fried
 *
 */
public abstract class AASXFileServerSuite {

	protected abstract AASXFileServer getAASXFileServer();
	@Test
	public void getAllAASXPackageIds() {

		AASXFileServer server = getAASXFileServer();
		List<PackageDescription> pkgDescs = DummyAASXFileServerFactory.createMultipleDummyAASXPackagesOnServer(server);

		PackageDescription expectedDescription1 = DummyAASXFileServerFactory.createDummyPackageDescription(pkgDescs.get(0).getPackageId(), DummyAASXFileServerFactory.FIRST_SHELL_IDS);
		PackageDescription expectedDescription2 = DummyAASXFileServerFactory.createDummyPackageDescription(pkgDescs.get(1).getPackageId(), DummyAASXFileServerFactory.SECOND_SHELL_IDS);

		List<PackageDescription> expectedPackageDescriptions = Arrays.asList(expectedDescription1, expectedDescription2);
		
		CursorResult<List<PackageDescription>> pagedPackageDescriptions = server.getAllAASXPackageIds("", PaginationInfo.NO_LIMIT);

		List<PackageDescription> actualPackageDescriptions = pagedPackageDescriptions.getResult();

		assertGetAllAASXPackageIds(expectedPackageDescriptions, actualPackageDescriptions);
	}
	
	@Test
	public void getAllAASXPackageIdsByShellId() {

		AASXFileServer server = getAASXFileServer();
		List<PackageDescription> pkgDescs = DummyAASXFileServerFactory.createMultipleDummyAASXPackagesOnServer(server);

		PackageDescription expectedDescription = DummyAASXFileServerFactory.createDummyPackageDescription(pkgDescs.get(1).getPackageId(), DummyAASXFileServerFactory.SECOND_SHELL_IDS);

		List<PackageDescription> expectedPackageDescriptions = Arrays.asList(expectedDescription);

		CursorResult<List<PackageDescription>> pagedPackageDescriptions = server.getAllAASXPackageIds("AAS_ID_3", PaginationInfo.NO_LIMIT);
		List<PackageDescription> actualPackageDescriptions = pagedPackageDescriptions.getResult();
		assertGetAllAASXPackageIds(expectedPackageDescriptions, actualPackageDescriptions);
	}

	@Test
	public void createAASXPackage() {

		AASXFileServer server = getAASXFileServer();
		PackageDescription actualPackageDescription = DummyAASXFileServerFactory.createFirstDummyAASXPackageOnServer(server);

		PackageDescription expectedPackageDescription = DummyAASXFileServerFactory.createDummyPackageDescription(actualPackageDescription.getPackageId(), DummyAASXFileServerFactory.FIRST_SHELL_IDS);

		assertEquals(expectedPackageDescription, actualPackageDescription);
	}

	@Test
	public void getAllAASXPackageIdsEmpty() {
		String shellId = "testShellId";

		AASXFileServer server = getAASXFileServer();
		CursorResult<List<PackageDescription>> pagedPackageDescriptions = server.getAllAASXPackageIds(shellId, PaginationInfo.NO_LIMIT);
		List<PackageDescription> packageDescriptions = pagedPackageDescriptions.getResult();
		assertTrue(packageDescriptions.isEmpty());
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getSpecificNonExistingPackageId() {

		AASXFileServer server = getAASXFileServer();
		server.getAASXByPackageId("doesNotExist");
	}

	@Test
	public void updateExistingAASXByPackageId() throws IOException {

		AASXFileServer server = getAASXFileServer();

		PackageDescription initialPackageDescription = DummyAASXFileServerFactory.createFirstDummyAASXPackageOnServer(server);

		updateAASXPackage(server, initialPackageDescription.getPackageId(), DummyAASXFileServerFactory.SECOND_SHELL_IDS, DummyAASXFileServerFactory.class.getClassLoader().getResourceAsStream("TestAAS2.aasx"), DummyAASXFileServerFactory.SECOND_FILENAME);

		PackageDescription expectedPackageDescription = new DefaultPackageDescription();
		expectedPackageDescription.setPackageId(initialPackageDescription.getPackageId());
		expectedPackageDescription.setAasIds(DummyAASXFileServerFactory.SECOND_SHELL_IDS);

		CursorResult<List<PackageDescription>> pagedPackageDescriptions = server.getAllAASXPackageIds("", PaginationInfo.NO_LIMIT);
		List<PackageDescription> actualPackageDescription = pagedPackageDescriptions.getResult();
		assertUpdatedAASXPackageId(expectedPackageDescription, actualPackageDescription, server);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void updateNonExistingAASXByPackageId() {

		String packageId = "notExisting";

		AASXFileServer server = getAASXFileServer();

		updateAASXPackage(server, packageId, DummyAASXFileServerFactory.FIRST_SHELL_IDS, DummyAASXFileServerFactory.class.getClassLoader().getResourceAsStream("TestAAS1.aasx"), DummyAASXFileServerFactory.FIRST_FILENAME);
	}

	@Test
	public void getAASXByPackageId() throws ElementDoesNotExistException, IOException {

		AASXFileServer server = getAASXFileServer();

		PackageDescription packageDescription = DummyAASXFileServerFactory.createFirstDummyAASXPackageOnServer(server);

		InputStream actualValue = server.getAASXByPackageId(packageDescription.getPackageId());
		InputStream expectedValue = DummyAASXFileServerFactory.class.getClassLoader().getResourceAsStream("TestAAS1.aasx");

		assertTrue(IOUtils.contentEquals(expectedValue, actualValue));
	}

	@Test
	public void deleteAASXByPackageId() {

		AASXFileServer server = getAASXFileServer();

		PackageDescription packageDescription = DummyAASXFileServerFactory.createFirstDummyAASXPackageOnServer(server);

		server.deleteAASXByPackageId(packageDescription.getPackageId());

		try {
			server.getAASXByPackageId(packageDescription.getPackageId());
			fail();
		} catch (ElementDoesNotExistException expected) {
		}
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void deleteNonExistingAASXPackage() {

		AASXFileServer server = getAASXFileServer();
		server.deleteAASXByPackageId("nonExisting");
	}

	private void updateAASXPackage(AASXFileServer server, String packageId, List<String> expectedShellIds, InputStream file, String filename) {

		server.updateAASXByPackageId(packageId, expectedShellIds, file, filename);
	}

	private void assertGetAllAASXPackageIds(List<PackageDescription> expectedPackageDescriptions, List<PackageDescription> actualPackageDescriptions) {
		assertTrue(expectedPackageDescriptions.containsAll(actualPackageDescriptions));
		assertTrue(actualPackageDescriptions.containsAll(expectedPackageDescriptions));
	}

	private void assertUpdatedAASXPackageId(PackageDescription expectedPackageDescription, List<PackageDescription> actualPackageDescriptions, AASXFileServer server) throws IOException {

		assertEquals(1, actualPackageDescriptions.size());
		assertTrue(actualPackageDescriptions.contains(expectedPackageDescription));

		InputStream actualAASXFile = server.getAASXByPackageId(expectedPackageDescription.getPackageId());
		InputStream expectedAASXFile = DummyAASXFileServerFactory.class.getClassLoader().getResourceAsStream("TestAAS2.aasx");

		assertTrue(IOUtils.contentEquals(expectedAASXFile, actualAASXFile));
	}

}
