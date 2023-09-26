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
import java.util.Collection;
import java.util.List;


import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServer;
import org.eclipse.digitaltwin.basyx.aasxfileserver.PackageDescription;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.junit.Test;
import org.eclipse.digitaltwin.basyx.aasxfileserver.core.DummyAASXFileServerFactory;

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
		Collection<PackageDescription> packageDescriptions = DummyAASXFileServerFactory.getAllDummyAASXPackages(server);			
			 
		assertGetAllAASXPackageIds(packageDescriptions);
	}
	
	@Test
	public void getAllAASXPackageIdsEmpty() {
		
		AASXFileServer server = getAASXFileServer();	
		Collection<PackageDescription> packageDescriptions = server.getAllAASXPackageIds();			
				
		assertIsEmpty(packageDescriptions);		
	}

	@Test(expected = ElementDoesNotExistException.class)
	public void getSpecificNonExistingPackageId() {
		
		AASXFileServer server = getAASXFileServer();
		server.getAASXByPackageId("doesNotExist");
	}

	@Test
	public void updateExistingAASXByPackageId() {				
		
		AASXFileServer server = getAASXFileServer();	
		
		PackageDescription expectedPackageDescription = DummyAASXFileServerFactory.createFirstDummyAASXPackage(server);	
		
	    updateAasxPackage(server, expectedPackageDescription.getPackageId(), DummyAASXFileServerFactory.SECOND_AAS_IDS , 
	    		DummyAASXFileServerFactory.SECOND_FILE, DummyAASXFileServerFactory.SECOND_FILENAME);
		
		Collection<PackageDescription> actualPackageDescription = server.getAllAASXPackageIds();
				
		assertUpdatedAASXPackageId(expectedPackageDescription, actualPackageDescription, server);
	}	

	@Test(expected = ElementDoesNotExistException.class)
	public void updateNonExistingAASXByPackageId() {
		
		String packageId = "notExisting";		
		AASXFileServer server = getAASXFileServer();		
		server.updateAASXByPackageId(packageId, DummyAASXFileServerFactory.FIRST_AAS_IDS, 
				DummyAASXFileServerFactory.FIRST_FILE, DummyAASXFileServerFactory.FIRST_FILENAME);
	}	
	
	@Test
	public void getAASXByPackageId() throws ElementDoesNotExistException, IOException {
		
		AASXFileServer server = getAASXFileServer();
		PackageDescription packageDescription = DummyAASXFileServerFactory.createFirstDummyAASXPackage(server);			
		InputStream actualValue = server.getAASXByPackageId(packageDescription.getPackageId());	
		InputStream expectedValue = DummyAASXFileServerFactory.FIRST_FILE;	
		
		assertInputStreamsEqual(expectedValue, actualValue);
	}

	@Test
	public void deleteAASXByPackageId() {
		
		AASXFileServer server = getAASXFileServer();
		PackageDescription packageDescription = DummyAASXFileServerFactory.createFirstDummyAASXPackage(server);		
		server.deleteAASXByPackageId(packageDescription.getPackageId()); 
		try {
			server.getAASXByPackageId(packageDescription.getPackageId());
			fail();
		} catch (ElementDoesNotExistException expected) {
		}
	}	
	
	@Test(expected = ElementDoesNotExistException.class)
	public void deleteNonExistingAasxFileServer() {
		
		AASXFileServer server = getAASXFileServer();
		server.deleteAASXByPackageId("nonExisting");
	}	
	
	private void updateAasxPackage(AASXFileServer server, String packageId, List<String> expectedAasIds, InputStream secondFile,
			String secondFilename) {
		
		server.updateAASXByPackageId(packageId, expectedAasIds, 
				DummyAASXFileServerFactory.SECOND_FILE, DummyAASXFileServerFactory.SECOND_FILENAME);
		
	}
	
	private void assertGetAllAASXPackageIds(Collection<PackageDescription> packageDescriptions) {
        
        assertEquals(2, packageDescriptions.size());
       
        List<String> expectedFirstAasIds = DummyAASXFileServerFactory.FIRST_AAS_IDS;        
        List<String> expectedSecondAasIds = DummyAASXFileServerFactory.SECOND_AAS_IDS;        

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
	
	private void assertUpdatedAASXPackageId(PackageDescription expectedPackageDescription, Collection<PackageDescription> actualPackageDescriptions, AASXFileServer server) {
		assertEquals(1, actualPackageDescriptions.size());
		assertTrue(actualPackageDescriptions.contains(expectedPackageDescription)); 
		
		InputStream actualAASXFile = server.getAASXByPackageId(actualPackageDescriptions.iterator().next().getPackageId());		        	
	    InputStream expectedAASXFile = server.getAASXByPackageId(expectedPackageDescription.getPackageId());
	        	
	    assertEquals(expectedAASXFile,actualAASXFile);        	
	}
	
	private void assertIsEmpty(Collection<PackageDescription> packageDescription) {		
		assertTrue(packageDescription.isEmpty());
	}	

	private boolean assertInputStreamsEqual(InputStream expectedValue, InputStream actualValue) throws IOException {
	    int expectedByte;
	    int actualByte;

	    while ((expectedByte = expectedValue.read()) != -1) {
	        actualByte = actualValue.read();

	        if (expectedByte != actualByte) {
	            return false;
	        }
	    }
	    return actualValue.read() == -1;
	}

}
