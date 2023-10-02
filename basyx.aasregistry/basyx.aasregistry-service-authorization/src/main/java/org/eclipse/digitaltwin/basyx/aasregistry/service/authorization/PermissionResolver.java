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
package org.eclipse.digitaltwin.basyx.aasregistry.service.authorization;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.authorization.Action;
import org.eclipse.digitaltwin.basyx.authorization.ISubjectInfo;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;

import javax.validation.Valid;

public interface PermissionResolver<AssetAdministrationShellDescriptorFilterType, SubmodelDescriptorFilterType> {

    public void deleteAssetAdministrationShellDescriptorById(String aasIdentifier);

    public void deleteSubmodelDescriptorByIdThroughSuperpath(String aasIdentifier, String submodelIdentifier);

    public FilterInfo<AssetAdministrationShellDescriptorFilterType> getGetAllAssetAdministrationShellDescriptorsFilterInfo();

    public FilterInfo<SubmodelDescriptorFilterType> getGetAllSubmodelDescriptorsThroughSuperpathFilterInfo();

    public void getAssetAdministrationShellDescriptorById(String aasIdentifier);

    public void getSubmodelDescriptorByIdThroughSuperpath(String aasIdentifier, String submodelIdentifier);

    public void postSubmodelDescriptorThroughSuperpath(String aasIdentifier, SubmodelDescriptor body);

    public void putAssetAdministrationShellDescriptorById(String aasIdentifier, AssetAdministrationShellDescriptor body);

    public void postAssetAdministrationShellDescriptor(@Valid AssetAdministrationShellDescriptor body);

    public void putSubmodelDescriptorByIdThroughSuperpath(String aasIdentifier, String submodelIdentifier, SubmodelDescriptor descriptor);

    public FilterInfo<AssetAdministrationShellDescriptorFilterType> getDeleteAllShellDescriptorsFilterInfo();
}
