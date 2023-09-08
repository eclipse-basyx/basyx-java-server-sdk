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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.digitaltwin.basyx.aasxfileserver.AasxFileServer;
import org.eclipse.digitaltwin.basyx.aasxfileserver.PackageDescription;

/**
 * Factory for creating AASX Packages for tests
 * 
 * @author chaithra
 *
 */
public class DummyAasxFileServerFactory {

    public static PackageDescription createFirstDummyAASXPackage(AasxFileServer server) {
        return server.createAASXPackage(
                AasxFileServerSuiteHelper.FIRST_AAS_IDS,
                AasxFileServerSuiteHelper.FIRST_FILE,
                AasxFileServerSuiteHelper.FIRST_FILENAME);
    }

    public static PackageDescription createSecondDummyAASXPackage(AasxFileServer server) {
        return server.createAASXPackage(
                AasxFileServerSuiteHelper.SECOND_AAS_IDS,
                AasxFileServerSuiteHelper.SECOND_FILE,
                AasxFileServerSuiteHelper.SECOND_FILENAME);
    }

    public static Collection<PackageDescription> getAllDummyAASXPackages(AasxFileServer server) {
        PackageDescription firstPackage = createFirstDummyAASXPackage(server);
        PackageDescription secondPackage = createSecondDummyAASXPackage(server);

        ArrayList<PackageDescription> packages = new ArrayList<>();
        packages.add(firstPackage);
        packages.add(secondPackage);

        return packages;
    }
}






