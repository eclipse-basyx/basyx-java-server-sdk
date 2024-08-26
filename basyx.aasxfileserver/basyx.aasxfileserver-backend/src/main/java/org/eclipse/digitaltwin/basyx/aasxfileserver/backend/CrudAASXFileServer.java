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
package org.eclipse.digitaltwin.basyx.aasxfileserver.backend;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.digitaltwin.aas4j.v3.model.PackageDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultPackageDescription;
import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServer;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.Package;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.PackagesBody;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.springframework.data.repository.CrudRepository;

/**
 * Default Implementation for the {@link AASXFileServer} based on Spring
 * {@link CrudRepository}
 * 
 * @author zielstor, fried
 *
 */
public class CrudAASXFileServer implements AASXFileServer {

	private AASXFileServerBackendProvider aasxFileServerBackendProvider;
	private String aasxFileServerName;
	private AtomicInteger packageId = new AtomicInteger(0);

	/**
	 * Constructor
	 * 
	 * @param aasxFileServerBackendProvider
	 *            The backend provider
	 * @param aasxFileServerName
	 *            The AASX file server name
	 */
	public CrudAASXFileServer(AASXFileServerBackendProvider aasxFileServerBackendProvider, String aasxFileServerName) {
		this.aasxFileServerBackendProvider = aasxFileServerBackendProvider;
		this.aasxFileServerName = aasxFileServerName;
	}

	@Override
	public CursorResult<List<PackageDescription>> getAllAASXPackageIds(String shellId,PaginationInfo pInfo) {
		List<PackageDescription> packageDescriptions = getPackages().stream().map(Package::getPackageDescription).collect(Collectors.toList());

		if (!(shellId == null || shellId.isBlank()))
			packageDescriptions = packageDescriptions.stream().filter(packageDesc -> containsShellId(packageDesc, shellId)).collect(Collectors.toList());

		TreeMap<String, PackageDescription> packageDescriptionMap = packageDescriptions.stream().collect(Collectors.toMap(PackageDescription::getPackageId, submodel -> submodel, (a, b) -> a, TreeMap::new));

		PaginationSupport<PackageDescription> paginationSupport = new PaginationSupport<>(packageDescriptionMap, PackageDescription::getPackageId);

		return paginationSupport.getPaged(pInfo);
	}

	@Override
	public InputStream getAASXByPackageId(String packageId) throws ElementDoesNotExistException {
		throwIfAASXPackageIdDoesNotExist(packageId);

		return aasxFileServerBackendProvider.getCrudRepository().findById(packageId).get().getPackagesBody().getFile();
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

		aasxFileServerBackendProvider.getCrudRepository().deleteById(packageId);
	}

	@Override
	public String getName() {
		return aasxFileServerName == null ? AASXFileServer.super.getName() : aasxFileServerName;
	}

	private PackageDescription createPackageDescription(List<String> shellIds, String newPackageId) {
		PackageDescription packageDescription = new DefaultPackageDescription();
		packageDescription.setPackageId(newPackageId);
		packageDescription.setItems(shellIds);

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

		aasxFileServerBackendProvider.getCrudRepository().save(aasxPackage);
	}

	private void updateAASXPackage(String packageId, List<String> shellIds, InputStream file, String filename) {
		Package aasxPackage = aasxFileServerBackendProvider.getCrudRepository().findById(packageId).get();

		updatePackagesBody(shellIds, file, filename, aasxPackage.getPackagesBody());

		aasxPackage.getPackageDescription().setItems(shellIds);

		aasxFileServerBackendProvider.getCrudRepository().delete(aasxPackage);
		aasxFileServerBackendProvider.getCrudRepository().save(aasxPackage);
	}

	private void updatePackagesBody(List<String> shellIds, InputStream file, String filename, PackagesBody packagesBody) {
		packagesBody.setAasIds(shellIds);
		packagesBody.setFileName(filename);
		packagesBody.setFile(file);
	}

	private void throwIfAASXPackageIdDoesNotExist(String id) {

		if (!aasxFileServerBackendProvider.getCrudRepository().existsById(id))
			throw new ElementDoesNotExistException(id);
	}

	private boolean containsShellId(PackageDescription packageDesc, String shellId) {
		return packageDesc.getItems().stream().anyMatch(aasId -> aasId.equals(shellId));
	}

	private List<Package> getPackages() {
		Iterable<Package> aasxFileServerPackages = aasxFileServerBackendProvider.getCrudRepository().findAll();
		return StreamSupport.stream(aasxFileServerPackages.spliterator(), false).collect(Collectors.toList());
	}

}
