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

package org.eclipse.digitaltwin.basyx.aasxfileserver.backend;

import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.digitaltwin.aas4j.v3.model.PackageDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultPackageDescription;
import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServer;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.Package;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.PackagesBody;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileMetadata;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.springframework.data.repository.CrudRepository;

/**
 * Default Implementation for the {@link AASXFileServer} based on Spring
 * {@link CrudRepository}
 * 
 * @author zielstor, fried, mateusmolina
 *
 */
public class CrudAASXFileServer implements AASXFileServer {

	static final String AASX_CONTENT_TYPE = "application/asset-administration-shell-package";

	private final PackageBackend packageBackend;
	private final FileRepository fileRepository;
	private final String aasxFileServerName;

	/**
	 * Constructor
	 * 
	 * @param packageBackend The backend provider
	 * @param fileRepository The file repository
	 * @param aasxFileServerName The AASX file server name
	 */
	public CrudAASXFileServer(PackageBackend packageBackend, FileRepository fileRepository, String aasxFileServerName) {
		this.packageBackend = packageBackend;
		this.fileRepository = fileRepository;
		this.aasxFileServerName = aasxFileServerName;
	}

	@Override
	public CursorResult<List<PackageDescription>> getAllAASXPackageIds(String shellId, PaginationInfo pInfo) {
		List<PackageDescription> packageDescriptions = getPackages().map(Package::getPackageDescription).toList();

		if (!(shellId == null || shellId.isBlank()))
			packageDescriptions = packageDescriptions.stream().filter(packageDesc -> containsShellId(packageDesc, shellId)).toList();

		TreeMap<String, PackageDescription> packageDescriptionMap = packageDescriptions.stream().collect(Collectors.toMap(PackageDescription::getPackageId, submodel -> submodel, (a, b) -> a, TreeMap::new));

		PaginationSupport<PackageDescription> paginationSupport = new PaginationSupport<>(packageDescriptionMap, PackageDescription::getPackageId);

		return paginationSupport.getPaged(pInfo);
	}

	@Override
	public InputStream getAASXByPackageId(String packageId) throws ElementDoesNotExistException {
		return packageBackend.findById(packageId).map(this::getISFromPackage).orElseThrow(ElementDoesNotExistException::new);
	}

	@Override
	public void updateAASXByPackageId(String packageId, List<String> shellIds, InputStream file, String filename) throws ElementDoesNotExistException {
		deleteAASXByPackageId(packageId);

		PackageDescription packageDescription = createPackageDescription(shellIds, packageId);

		String filepath = fileRepository.save(new FileMetadata(filename, AASX_CONTENT_TYPE, file));

		PackagesBody packagesBody = createPackagesBody(shellIds, filename, filepath);

		Package pkg = createPackage(packageDescription, packagesBody);

		packageBackend.save(pkg);
	}

	@Override
	public PackageDescription createAASXPackage(List<String> shellIds, InputStream file, String fileName) {

		String newpackageId = generateShortUUID();

		PackageDescription packageDescription = createPackageDescription(shellIds, newpackageId);

		String filepath = fileRepository.save(new FileMetadata(fileName, AASX_CONTENT_TYPE, file));

		PackagesBody packagesBody = createPackagesBody(shellIds, fileName, filepath);

		Package pkg = createPackage(packageDescription, packagesBody);

		packageBackend.save(pkg);

		return packageDescription;
	}

	@Override
	public void deleteAASXByPackageId(String packageId) throws ElementDoesNotExistException {
		packageBackend.findById(packageId).map(this::fullyDeletePackage).orElseThrow(ElementDoesNotExistException::new);
	}

	@Override
	public String getName() {
		return aasxFileServerName == null ? AASXFileServer.super.getName() : aasxFileServerName;
	}

	private InputStream getISFromPackage(Package pkg) {
		return fileRepository.find(pkg.getPackagesBody().getFilePath());
	}

	/**
	 * 
	 * @param pkg
	 * @return id of the deleted package
	 */
	private String fullyDeletePackage(Package pkg) {
		final String pkgId = pkg.getPackageId();

		fileRepository.delete(pkg.getPackagesBody().getFilePath());
		packageBackend.deleteById(pkgId);

		return pkgId;
	}

	private boolean containsShellId(PackageDescription packageDesc, String shellId) {
		return packageDesc.getAasIds().stream().anyMatch(aasId -> aasId.equals(shellId));
	}

	private Stream<Package> getPackages() {
		Iterable<Package> aasxFileServerPackages = packageBackend.findAll();
		return StreamSupport.stream(aasxFileServerPackages.spliterator(), false);
	}

	private static PackageDescription createPackageDescription(List<String> shellIds, String newPackageId) {
		PackageDescription packageDescription = new DefaultPackageDescription();
		packageDescription.setPackageId(newPackageId);
		packageDescription.setAasIds(shellIds);

		return packageDescription;
	}

	private static PackagesBody createPackagesBody(List<String> shellIds, String fileName, String filePath) {
		PackagesBody packagesBody = new PackagesBody();
		packagesBody.aasIds(shellIds);
		packagesBody.fileName(fileName);
		packagesBody.setFilePath(filePath);

		return packagesBody;
	}

	private static Package createPackage(PackageDescription packageDescription, PackagesBody packagesBody) {
		return new Package(packageDescription.getPackageId(), packageDescription, packagesBody);
	}

	private static String generateShortUUID() {
		UUID uuid = UUID.randomUUID();
		return Base64.getUrlEncoder().withoutPadding().encodeToString(uuid.toString().getBytes());
	}

}
