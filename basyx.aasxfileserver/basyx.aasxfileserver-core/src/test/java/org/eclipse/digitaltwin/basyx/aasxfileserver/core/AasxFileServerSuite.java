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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.digitaltwin.basyx.aasxfileserver.AasxFileServer;
import org.eclipse.digitaltwin.basyx.aasxfileserver.PackageDescription;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.junit.Test;

/**
 * Testsuite for implementations of the AasxFileServerSuite interface
 * 
 * @author chaithra
 *
 */
public abstract class AasxFileServerSuite {
	
	protected abstract AasxFileServer getAasxFileServer();	
	
	private static final List<String> DUMMY_AAS_IDS = new ArrayList<>(Arrays.asList("AAS_ID_1", "AAS_ID_2"));	
	private static final String DUMMY_FILENAME = "test_file.txt";
	private static final byte[] byteArray = {65, 66, 67, 68, 69};
	private static final InputStream DUMMY_FILE = new ByteArrayInputStream(byteArray);	
		
	@Test
	public void getAllAASXPackageIds() {
		
		AasxFileServer server = getAasxFileServer();		
		createDummyAASXPackage(server);		
		Collection<PackageDescription> packageDescriptions = server.getAllAASXPackageIds();			
		PackageDescription firstPackage = packageDescriptions.iterator().next();	

		assertGetAllAASXPackageIds(packageDescriptions, firstPackage);
	}
	
	@Test
	public void getAllAASXPackageIdsEmpty() {
		
		AasxFileServer server = getAasxFileServer();	
		Collection<PackageDescription> packageDescriptions = server.getAllAASXPackageIds();					
				
		assertIsEmpty(packageDescriptions);		
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getSpecificNonExistingPackageId() {
		
		AasxFileServer server = getAasxFileServer();
		server.getAASXByPackageId("doesNotExist");
	}

	@Test
	public void updateExistingAASXByPackageId() {		
		
		List<String> expectedAasIds =  new ArrayList<>(Arrays.asList("AAS_ID_3", "AAS_ID_4"));
		AasxFileServer server = getAasxFileServer();
		PackageDescription packageDescription = createDummyAASXPackage(server);		
		server.updateAASXByPackageId(packageDescription.getPackageId(), expectedAasIds, DUMMY_FILE, DUMMY_FILENAME);
		
		assertUpdateAASXpaxkageID(packageDescription, expectedAasIds);
	}	

	@Test(expected = ElementDoesNotExistException.class)
	public void updateNonExistingAASXByPackageId() {
		
		String packageId = "notExisting";		
		AasxFileServer server = getAasxFileServer();		
		server.updateAASXByPackageId(packageId, DUMMY_AAS_IDS, DUMMY_FILE, DUMMY_FILENAME);
	}	
	
	@Test
	public void getAASXByPackageId() throws ElementDoesNotExistException {
		
		AasxFileServer server = getAasxFileServer();
		PackageDescription packageDescription = createDummyAASXPackage(server);			
		InputStream actualValue = server.getAASXByPackageId(packageDescription.getPackageId());
		
		assertEquals(DUMMY_FILE, actualValue);
	}

	@Test
	public void deleteAASXPackageById() {
		
		AasxFileServer server = getAasxFileServer();
		PackageDescription packageDescription = createDummyAASXPackage(server);	
		server.deleteAASXPackageById(packageDescription.getPackageId()); 
		try {
			server.getAASXByPackageId(packageDescription.getPackageId());
			fail();
		} catch (ElementDoesNotExistException expected) {
		}
	}	
	
	@Test(expected = ElementDoesNotExistException.class)
	public void deleteNonExistingConceptDescription() {
		
		AasxFileServer server = getAasxFileServer();
		server.deleteAASXPackageById("nonExisting");
	}	
	
	private PackageDescription createDummyAASXPackage(AasxFileServer server) {		
		return server.createAASXPackage(DUMMY_AAS_IDS, DUMMY_FILE, DUMMY_FILENAME);
	}
	
	private void assertGetAllAASXPackageIds(Collection<PackageDescription> packageDescriptions, PackageDescription firstPackage) {		
		assertEquals(1, packageDescriptions.size());		
		assertEquals("1", firstPackage.getPackageId()); 
		assertEquals(DUMMY_AAS_IDS,firstPackage.getAasIds());		
		assertTrue(DUMMY_AAS_IDS.containsAll(firstPackage.getAasIds()));		
	}	
	
	private void assertUpdateAASXpaxkageID(PackageDescription actualPackageDescription, List<String> expectedNewAasIds ) {		
		assertEquals(expectedNewAasIds, actualPackageDescription.getAasIds());
		assertEquals("1",actualPackageDescription.getPackageId());		
	}
	
	private void assertIsEmpty(Collection<PackageDescription> packageDescription) {		
		assertTrue(packageDescription.isEmpty());
	}		

}
