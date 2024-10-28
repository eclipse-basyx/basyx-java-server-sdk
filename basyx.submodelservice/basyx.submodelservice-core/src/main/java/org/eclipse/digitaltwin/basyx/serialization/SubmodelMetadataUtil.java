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

package org.eclipse.digitaltwin.basyx.serialization;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.core.exceptions.FailedToDeepCopyException;

public final class SubmodelMetadataUtil {

    private SubmodelMetadataUtil() {
    }

    /**
     * Returns a new submodel that only contains the metadata of the given submodel.
     *
     * @param submodel
     * @return A new submodel with the metadata of the given submodel.
     */
    public static Submodel extractMetadata(Submodel submodel) {
        Submodel submodelDeepCopy = deepCopy(submodel);

        submodelDeepCopy.setSubmodelElements(null);

        return submodelDeepCopy;
    }

    /**
     * Deep copy a submodel.
     * 
     * @param submodel
     * @return A deep copy of the submodel.
     * 
     * @throws FailedToDeepCopyException
     * 
     */
    private static Submodel deepCopy(Submodel submodel) {
        try {
            String submodelAsJSON = new JsonSerializer().write(submodel);

            return new JsonDeserializer().read(submodelAsJSON, Submodel.class);

        } catch (DeserializationException | SerializationException e) {
            throw new FailedToDeepCopyException(submodel.getId(), e);
        }
    }
}
