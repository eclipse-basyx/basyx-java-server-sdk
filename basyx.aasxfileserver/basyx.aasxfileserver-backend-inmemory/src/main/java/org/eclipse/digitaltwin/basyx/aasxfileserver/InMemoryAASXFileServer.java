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

import org.eclipse.digitaltwin.basyx.aasxfileserver.model.Package;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.PackageDescription;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.PackagesBody;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;

/**
 * In-Memory implementation of the {@link AASXFileServer}
 *
 * @author chaithra
 *
 */
public class InMemoryAASXFileServer implements AASXFileServer {

	private Map<String, Package> packageMap = new LinkedHashMap<>();
	private AtomicInteger packageId = new AtomicInteger(0);

	private String aasxFileServerName;

	/**
	 * Creates the InMemoryAASXFileServer
	 * 
	 */
	public InMemoryAASXFileServer() {
	}

	/**
	 * Creates the InMemoryAASXFileServer
	 * 
	 * @param aasxRepositoryName
	 *            Name of the CDRepository
	 */
	public InMemoryAASXFileServer(String aasxFileServerName) {
		this.aasxFileServerName = aasxFileServerName;
	}

	@Override
	public Collection<PackageDescription> getAllAASXPackageIds(String shellId) {
		Collection<PackageDescription> packageDescriptions = packageMap.values().stream().map(Package::getPackageDescription).collect(Collectors.toList());
		
		if (shellId == null || shellId.isBlank())
			return packageDescriptions;
		
		return packageDescriptions.stream().filter(packageDesc -> containsShellId(packageDesc, shellId)).collect(Collectors.toList());
	}

	@Override
	public InputStream getAASXByPackageId(String packageId) throws ElementDoesNotExistException {
		throwIfAASXPackageIdDoesNotExist(packageId);

		return packageMap.get(packageId).getPackagesBody().getFile();
	}

	@Override
	public void updateAASXByPackageId(String packageId, List<String> shellIds, InputStream file, String filename) throws ElementDoesNotExistException {

		throwIfAASXPackageIdDoesNotExist(packageId);

		updateAASXPackage(packageId, shellIds, file, filename);
	}

	@Override
	public PackageDescription createAASXPackage(List<String> shellIds, InputStream file, String fileName) {

		String newpackageId = String.valueOf(packageId.incrementAndGet());

		PackageDescription packageDescription = createPackageDescription(shellIds, newpackageId);

		createPackage(shellIds, file, fileName, newpackageId, packageDescription);

		return packageDescription;
	}

	@Override
	public void deleteAASXByPackageId(String packageId) throws ElementDoesNotExistException {
		throwIfAASXPackageIdDoesNotExist(packageId);

		packageMap.remove(packageId);
	}

	@Override
	public String getName() {
		return aasxFileServerName == null ? AASXFileServer.super.getName() : aasxFileServerName;
	}

	private PackageDescription createPackageDescription(List<String> shellIds, String newPackageId) {
		PackageDescription packageDescription = new PackageDescription();
		packageDescription.packageId(newPackageId);
		packageDescription.aasIds(shellIds);

		return packageDescription;
	}

	private PackagesBody createPackagesBody(List<String> shellIds, InputStream file, String fileName) {
		PackagesBody packagesBody = new PackagesBody();
		packagesBody.aasIds(shellIds);
		packagesBody.file(file);
		packagesBody.fileName(fileName);

		return packagesBody;
	}

	private void createPackage(List<String> shellIds, InputStream file, String fileName, String newPackageId, PackageDescription packageDescription) {
		PackagesBody packagesBody = createPackagesBody(shellIds, file, fileName);

		Package aasxPackage = new Package(newPackageId, packageDescription, packagesBody);

		packageMap.put(newPackageId, aasxPackage);
	}

	private void updateAASXPackage(String packageId, List<String> shellIds, InputStream file, String filename) {
		Package aasxPackage = this.packageMap.get(packageId);

		updatePackagesBody(shellIds, file, filename, aasxPackage.getPackagesBody());

		aasxPackage.getPackageDescription().setAasIds(shellIds);
	}

	private void updatePackagesBody(List<String> shellIds, InputStream file, String filename, PackagesBody packagesBody) {
		packagesBody.setAasIds(shellIds);
		packagesBody.setFileName(filename);
		packagesBody.setFile(file);
	}

	private void throwIfAASXPackageIdDoesNotExist(String id) {

		if (!packageMap.containsKey(id))
			throw new ElementDoesNotExistException(id);
	}
	
	private boolean containsShellId(PackageDescription packageDesc, String shellId) {
		return packageDesc.getAasIds().stream().anyMatch(aasId -> aasId.equals(shellId));
	}

}
