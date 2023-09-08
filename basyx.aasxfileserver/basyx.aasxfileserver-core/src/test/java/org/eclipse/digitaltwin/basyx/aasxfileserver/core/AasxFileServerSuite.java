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
 * Testsuite for implementations of the {@link AasxFileServer} interface
 * 
 * @author chaithra
 *
 */
public abstract class AasxFileServerSuite {
	
	protected abstract AasxFileServer getAasxFileServer();		 
		
	@Test
	public void getAllAASXPackageIds() {
		
		AasxFileServer server = getAasxFileServer();		
		Collection<PackageDescription> packageDescriptions = DummyAasxFileServerFactory.getAllDummyAASXPackages(server);			
			 
		assertGetAllAASXPackageIds(packageDescriptions);
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
		
		AasxFileServer server = getAasxFileServer();
		PackageDescription packageDescription = DummyAasxFileServerFactory.createFirstDummyAASXPackage(server);	
		List<String> expectedAasIds =  new ArrayList<>(Arrays.asList("AAS_ID_3", "AAS_ID_4"));
		server.updateAASXByPackageId(packageDescription.getPackageId(), expectedAasIds, 
				AasxFileServerSuiteHelper.FIRST_FILE, AasxFileServerSuiteHelper.FIRST_FILENAME);
		
		assertAASXPackageId(packageDescription, expectedAasIds);
	}	

	@Test(expected = ElementDoesNotExistException.class)
	public void updateNonExistingAASXByPackageId() {
		
		String packageId = "notExisting";		
		AasxFileServer server = getAasxFileServer();		
		server.updateAASXByPackageId(packageId, AasxFileServerSuiteHelper.FIRST_AAS_IDS, 
				AasxFileServerSuiteHelper.FIRST_FILE, AasxFileServerSuiteHelper.FIRST_FILENAME);
	}	
	
	@Test
	public void getAASXByPackageId() throws ElementDoesNotExistException {
		
		AasxFileServer server = getAasxFileServer();
		PackageDescription packageDescription = DummyAasxFileServerFactory.createFirstDummyAASXPackage(server);			
		InputStream actualValue = server.getAASXByPackageId(packageDescription.getPackageId());		
		
		assertEquals(AasxFileServerSuiteHelper.FIRST_FILE, actualValue);		
	}

	@Test
	public void deleteAASXByPackageId() {
		
		AasxFileServer server = getAasxFileServer();
		PackageDescription packageDescription = DummyAasxFileServerFactory.createFirstDummyAASXPackage(server);		
		server.deleteAASXByPackageId(packageDescription.getPackageId()); 
		try {
			server.getAASXByPackageId(packageDescription.getPackageId());
			fail();
		} catch (ElementDoesNotExistException expected) {
		}
	}	
	
	@Test(expected = ElementDoesNotExistException.class)
	public void deleteNonExistingAasxFileServer() {
		
		AasxFileServer server = getAasxFileServer();
		server.deleteAASXByPackageId("nonExisting");
	}	
	
	private void assertGetAllAASXPackageIds(Collection<PackageDescription> packageDescriptions) {
        
        assertEquals(2, packageDescriptions.size());
       
        List<String> expectedFirstAasIds = AasxFileServerSuiteHelper.FIRST_AAS_IDS;        
        List<String> expectedSecondAasIds = AasxFileServerSuiteHelper.SECOND_AAS_IDS;        

       for (PackageDescription packageDescription : packageDescriptions) {
            if (packageDescription.getPackageId().equals("1")) {                
                assertEquals(expectedFirstAasIds, packageDescription.getAasIds());                
            } else if (packageDescription.getPackageId().equals("2")) {                
                assertEquals(expectedSecondAasIds, packageDescription.getAasIds());                
            } else {                
                fail("Unexpected package ID: " + packageDescription.getPackageId());
            }
        }
    }
	
	private void assertAASXPackageId(PackageDescription actualPackageDescription, List<String> expectedNewAasIds ) {		
		assertEquals(expectedNewAasIds, actualPackageDescription.getAasIds());
		assertEquals(expectedNewAasIds.size(), actualPackageDescription.getAasIds().size()); 
		assertEquals("1",actualPackageDescription.getPackageId());		
	}	
	
	private void assertIsEmpty(Collection<PackageDescription> packageDescription) {		
		assertTrue(packageDescription.isEmpty());
	}	

}
