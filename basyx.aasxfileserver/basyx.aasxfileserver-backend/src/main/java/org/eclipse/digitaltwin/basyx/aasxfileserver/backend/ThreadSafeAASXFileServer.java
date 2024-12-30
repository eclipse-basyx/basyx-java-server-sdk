
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
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.PackageDescription;
import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServer;
import org.eclipse.digitaltwin.basyx.common.backend.InstanceScopedThreadSafeAccess;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * A thread-safe wrapper for the {@link AASXFileServer}
 * 
 * @author mateusmolina
 */
public class ThreadSafeAASXFileServer implements AASXFileServer {

    private final AASXFileServer decoratedAasxFileServer;
    private final InstanceScopedThreadSafeAccess access = new InstanceScopedThreadSafeAccess();

    public ThreadSafeAASXFileServer(AASXFileServer aasxFileServer) {
        this.decoratedAasxFileServer = aasxFileServer;
    }

    @Override
    public CursorResult<List<PackageDescription>> getAllAASXPackageIds(String shellId, PaginationInfo pInfo) {
        return decoratedAasxFileServer.getAllAASXPackageIds(shellId, pInfo);
    }

    @Override
    public InputStream getAASXByPackageId(String packageId) throws ElementDoesNotExistException {
        return access.read(() -> decoratedAasxFileServer.getAASXByPackageId(packageId), packageId);
    }

    @Override
    public void updateAASXByPackageId(String packageId, List<String> shellIds, InputStream file, String filename) throws ElementDoesNotExistException {
        access.write(() -> decoratedAasxFileServer.updateAASXByPackageId(packageId, shellIds, file, filename), packageId);
    }

    @Override
    public PackageDescription createAASXPackage(List<String> shellIds, InputStream file, String filename) {
        return decoratedAasxFileServer.createAASXPackage(shellIds, file, filename);
    }

    @Override
    public void deleteAASXByPackageId(String packageId) throws ElementDoesNotExistException {
        access.write(() -> decoratedAasxFileServer.deleteAASXByPackageId(packageId), packageId);
    }

    @Override
    public String getName() {
        return decoratedAasxFileServer.getName();
    }

}
