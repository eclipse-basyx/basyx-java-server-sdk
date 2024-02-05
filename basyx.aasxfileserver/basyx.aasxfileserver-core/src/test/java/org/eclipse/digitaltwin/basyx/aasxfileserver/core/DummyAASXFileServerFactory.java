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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServer;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.PackageDescription;

/**
 * Factory for creating AASX Packages for tests
 * 
 * @author chaithra
 *
 */
public class DummyAASXFileServerFactory {

	public static final List<String> FIRST_SHELL_IDS = Arrays.asList("AAS_ID_1", "AAS_ID_2");
	public static final String FIRST_FILENAME = "test_file1.txt";
	public static final byte[] FIRST_BYTEARRAY = { 65, 66, 67, 68, 69 };
	public static final InputStream FIRST_FILE = new ByteArrayInputStream(FIRST_BYTEARRAY);

	public static final List<String> SECOND_SHELL_IDS = Arrays.asList("AAS_ID_3", "AAS_ID_4");
	public static final String SECOND_FILENAME = "test_file2.txt";
	public static final byte[] SECOND_BYTEARRAY = { 75, 76, 77, 78, 79 };
	public static final InputStream SECOND_FILE = new ByteArrayInputStream(SECOND_BYTEARRAY);

	public static PackageDescription createFirstDummyAASXPackageOnServer(AASXFileServer server) {
		return server.createAASXPackage(FIRST_SHELL_IDS, FIRST_FILE, FIRST_FILENAME);
	}

	public static PackageDescription createSecondDummyAASXPackageOnServer(AASXFileServer server) {
		return server.createAASXPackage(SECOND_SHELL_IDS, SECOND_FILE, SECOND_FILENAME);
	}

	public static Collection<PackageDescription> createMultipleDummyAASXPackagesOnServer(AASXFileServer server) {
		PackageDescription firstPackage = createFirstDummyAASXPackageOnServer(server);
		PackageDescription secondPackage = createSecondDummyAASXPackageOnServer(server);

		ArrayList<PackageDescription> packages = new ArrayList<>();
		packages.add(firstPackage);
		packages.add(secondPackage);

		return packages;
	}

	public static PackageDescription createDummyPackageDescription(String packageId, List<String> shellIds) {
		PackageDescription expectedDescription1 = new PackageDescription();
		expectedDescription1.setPackageId(packageId);
		expectedDescription1.setAasIds(shellIds);

		return expectedDescription1;
	}
}
