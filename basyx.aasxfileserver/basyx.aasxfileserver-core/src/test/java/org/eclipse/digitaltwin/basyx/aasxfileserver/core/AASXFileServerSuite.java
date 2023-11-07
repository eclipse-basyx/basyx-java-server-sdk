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

package org.eclipse.digitaltwin.basyx.aasxfileserver.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.IOUtils;

import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServer;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.PackageDescription;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.junit.Test;

/**
 * Testsuite for implementations of the {@link AASXFileServer} interface
 * 
 * @author chaithra
 *
 */
public abstract class AASXFileServerSuite {

	protected abstract AASXFileServer getAASXFileServer();

	@Test
	public void getAllAASXPackageIds() {

		AASXFileServer server = getAASXFileServer();
		DummyAASXFileServerFactory.createMultipleDummyAASXPackagesOnServer(server);

		PackageDescription expectedDescription1 = DummyAASXFileServerFactory.createDummyPackageDescription("1", DummyAASXFileServerFactory.FIRST_SHELL_IDS);
		PackageDescription expectedDescription2 = DummyAASXFileServerFactory.createDummyPackageDescription("2", DummyAASXFileServerFactory.SECOND_SHELL_IDS);

		Collection<PackageDescription> expectedPackageDescriptions = Arrays.asList(expectedDescription1, expectedDescription2);
		
		Collection<PackageDescription> actualPackageDescriptions = server.getAllAASXPackageIds("");

		assertGetAllAASXPackageIds(expectedPackageDescriptions, actualPackageDescriptions);
	}
	
	@Test
	public void getAllAASXPackageIdsByShellId() {

		AASXFileServer server = getAASXFileServer();
		DummyAASXFileServerFactory.createMultipleDummyAASXPackagesOnServer(server);

		PackageDescription expectedDescription = DummyAASXFileServerFactory.createDummyPackageDescription("2", DummyAASXFileServerFactory.SECOND_SHELL_IDS);

		Collection<PackageDescription> expectedPackageDescriptions = Arrays.asList(expectedDescription);
		
		Collection<PackageDescription> actualPackageDescriptions = server.getAllAASXPackageIds("AAS_ID_3");

		assertGetAllAASXPackageIds(expectedPackageDescriptions, actualPackageDescriptions);
	}

	@Test
	public void createAASXPackage() {

		AASXFileServer server = getAASXFileServer();
		PackageDescription actualPackageDescription = DummyAASXFileServerFactory.createFirstDummyAASXPackageOnServer(server);

		PackageDescription expectedPackageDescription = DummyAASXFileServerFactory.createDummyPackageDescription("1", DummyAASXFileServerFactory.FIRST_SHELL_IDS);

		assertEquals(expectedPackageDescription, actualPackageDescription);
	}

	@Test
	public void getAllAASXPackageIdsEmpty() {
		String shellId = "testShellId";

		AASXFileServer server = getAASXFileServer();
		Collection<PackageDescription> packageDescriptions = server.getAllAASXPackageIds(shellId);

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

		PackageDescription expectedPackageDescription = DummyAASXFileServerFactory.createFirstDummyAASXPackageOnServer(server);

		updateAASXPackage(server, expectedPackageDescription.getPackageId(), DummyAASXFileServerFactory.SECOND_SHELL_IDS, DummyAASXFileServerFactory.SECOND_FILE, DummyAASXFileServerFactory.SECOND_FILENAME);

		Collection<PackageDescription> actualPackageDescription = server.getAllAASXPackageIds("");

		assertUpdatedAASXPackageId(expectedPackageDescription, actualPackageDescription, server);
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void updateNonExistingAASXByPackageId() {

		String packageId = "notExisting";

		AASXFileServer server = getAASXFileServer();

		updateAASXPackage(server, packageId, DummyAASXFileServerFactory.FIRST_SHELL_IDS, DummyAASXFileServerFactory.FIRST_FILE, DummyAASXFileServerFactory.FIRST_FILENAME);
	}

	@Test
	public void getAASXByPackageId() throws ElementDoesNotExistException, IOException {

		AASXFileServer server = getAASXFileServer();

		PackageDescription packageDescription = DummyAASXFileServerFactory.createFirstDummyAASXPackageOnServer(server);

		InputStream actualValue = server.getAASXByPackageId(packageDescription.getPackageId());
		InputStream expectedValue = DummyAASXFileServerFactory.FIRST_FILE;

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

	private void assertGetAllAASXPackageIds(Collection<PackageDescription> expectedPackageDescriptions, Collection<PackageDescription> actualPackageDescriptions) {
		assertTrue(expectedPackageDescriptions.containsAll(actualPackageDescriptions));
		assertTrue(actualPackageDescriptions.containsAll(expectedPackageDescriptions));
	}

	private void assertUpdatedAASXPackageId(PackageDescription expectedPackageDescription, Collection<PackageDescription> actualPackageDescriptions, AASXFileServer server) throws IOException {

		assertEquals(1, actualPackageDescriptions.size());
		assertTrue(actualPackageDescriptions.contains(expectedPackageDescription));

		InputStream actualAASXFile = server.getAASXByPackageId("1");
		InputStream expectedAASXFile = DummyAASXFileServerFactory.SECOND_FILE;

		assertTrue(IOUtils.contentEquals(expectedAASXFile, actualAASXFile));
	}

}
