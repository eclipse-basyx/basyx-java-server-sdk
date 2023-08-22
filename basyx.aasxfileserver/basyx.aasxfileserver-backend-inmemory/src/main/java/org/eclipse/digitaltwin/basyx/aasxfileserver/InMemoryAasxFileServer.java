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

package org.eclipse.digitaltwin.basyx.aasxfileserver;

import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;

/**
 * In-memory implementation of the AasxFileServer
 *
 * @author chaithra
 *
 */
public class InMemoryAasxFileServer implements AasxFileServer {

	private Map<String, Package> packageMap = new LinkedHashMap<>();
	private AtomicInteger packageId = new AtomicInteger(0);

	/**
	 * Creates the InMemoryAasxFileServer
	 * @author chaithra
	 */	
	public InMemoryAasxFileServer() {}	
	
	@Override
	public Collection<PackageDescription> getAllAASXPackageIds() {

		return packageMap.values().stream()
				.map(aasxPackage -> aasxPackage.getPackageDescription())
				.collect(Collectors.toList());
	}

	@Override
	public InputStream getAASXByPackageId(String packageId) throws ElementDoesNotExistException {
		throwIfAasxFileIdDoesNotExist(packageId);

		return packageMap.get(packageId).getPackagesBody().getFile();
	}

	@Override
	public void updateAASXByPackageId(String packageId, List<String> aasIds, InputStream file, String filename)
			throws ElementDoesNotExistException {
		
		throwIfAasxFileIdDoesNotExist(packageId);

		updateAASXPackage(packageId, aasIds, file, filename);
	}	

	@Override
	public PackageDescription createAASXPackage(List<String> aasIds, InputStream file, String fileName)
			throws CollidingIdentifierException {
		
		String newpackageId = String.valueOf(packageId.incrementAndGet());		
		PackageDescription packageDescription = createPackageDescription(aasIds, newpackageId);		
		createPackage(aasIds, file, fileName, newpackageId, packageDescription);

		return packageDescription;
	}	

	@Override
	public void deleteAASXPackageById(String packageId) throws ElementDoesNotExistException {
		throwIfAasxFileIdDoesNotExist(packageId);

		packageMap.remove(packageId);
	}

	public Integer increment(Integer n) {
		return n++;
	}
	
	private PackageDescription createPackageDescription(List<String> aasIds, String newpackageId) {		
		PackageDescription packageDescription = new PackageDescription();
		packageDescription.packageId(newpackageId);
		packageDescription.aasIds(aasIds);
		
		return packageDescription;
	}
	
	private PackagesBody createPackagesBody(List<String> aasIds, InputStream file, String fileName) {		
		PackagesBody packagesBody = new PackagesBody();
		packagesBody.aasIds(aasIds);
		packagesBody.file(file);
		packagesBody.fileName(fileName);
		
		return packagesBody;
	}	
	
	private void createPackage(List<String> aasIds, InputStream file, String fileName, String newpackageId, PackageDescription packageDescription) {
		PackagesBody packagesBody = createPackagesBody(aasIds, file, fileName);		
		Package aasxPackage = new Package(newpackageId, packageDescription, packagesBody);		
		packageMap.put(newpackageId, aasxPackage);
	}
	
	private void updateAASXPackage(String packageId, List<String> aasIds, InputStream file, String filename) {
		Package aasxPackage = this.packageMap.get(packageId);		
		aasxPackage.getPackagesBody().setAasIds(aasIds);
		aasxPackage.getPackagesBody().setFileName(filename);		
		aasxPackage.getPackagesBody().setFile(file);		
		aasxPackage.getPackageDescription().setAasIds(aasIds);
	}	
	
	private void throwIfAasxFileIdDoesNotExist(String id) {
		
		if (!packageMap.containsKey(id))
			throw new ElementDoesNotExistException(id);
	}	

}
