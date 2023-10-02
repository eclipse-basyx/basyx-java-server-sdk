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
package org.eclipse.digitaltwin.basyx.submodelservice.authorization;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;

public interface PermissionResolver<SubmodelElementFilterType> {
    public void deleteSubmodelElement(Submodel submodel, String idShortPath);

    public FilterInfo<SubmodelElementFilterType> getGetSubmodelElementsFilterInfo(Submodel submodel);

    public void getSubmodelElement(Submodel submodel, String idShortPath);

    public void getSubmodelElementValue(Submodel submodel, String idShortPath);

    public FilterInfo<SubmodelElementFilterType> getSubmodelValueOnlyFilterInfo(Submodel submodel);

    public void setSubmodelElementValue(Submodel submodel, String idShortPath, SubmodelElementValue body);

    public void createSubmodelElement(Submodel submodel, SubmodelElement body);

    public void createSubmodelElement(Submodel submodel, String idShortPath, SubmodelElement body);

    public void getSubmodelMetaData(Submodel submodel);

    public void getSubmodel();
}
