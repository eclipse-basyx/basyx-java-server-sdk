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
package org.eclipse.digitaltwin.basyx.aasservice;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingSubmodelReferenceException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.lang.NonNull;

/**
 * This interface provides backend-level operations for managing
 * {@link AssetAdministrationShell}s
 * 
 * @author mateusmolina
 *
 */
public interface AasServiceOperations {

	/**
	 * Retrieves all Submodel References for the given AAS.
	 *
	 * @param aasId
	 *            the identifier of the Asset Administration Shell; must not be
	 *            {@code null}
	 * @param pInfo
	 *            the pagination information; must not be {@code null}
	 * @return a {@code CursorResult} containing a list of Submodel References
	 * @throws ElementDoesNotExistException
	 *             if the Asset Administration Shell with the specified
	 *             {@code aasId} does not exist
	 */
	CursorResult<List<Reference>> getSubmodelReferences(@NonNull String aasId, @NonNull PaginationInfo pInfo) throws ElementDoesNotExistException;

	/**
	 * Adds a Submodel Reference to the specified AAS.
	 *
	 * @param aasId
	 *            the identifier of the Asset Administration Shell; must not be
	 *            {@code null}
	 * @param submodelReference
	 *            the reference to be added; must not be {@code null}
	 * @throws ElementDoesNotExistException
	 *             if the Asset Administration Shell with the specified
	 *             {@code aasId} does not exist
	 * @throws CollidingSubmodelReferenceException
	 *             if the provided submodel reference conflicts with an existing
	 *             reference
	 */
	void addSubmodelReference(@NonNull String aasId, @NonNull Reference submodelReference) throws ElementDoesNotExistException, CollidingSubmodelReferenceException;

	/**
	 * Removes a Submodel Reference from the specified AAS.
	 *
	 * @param aasId
	 *            the identifier of the Asset Administration Shell; must not be
	 *            {@code null}
	 * @param submodelId
	 *            the identifier of the submodel to remove; must not be {@code null}
	 * @throws ElementDoesNotExistException
	 *             if the Asset Administration Shell with the specified
	 *             {@code aasId} does not exist
	 */
	void removeSubmodelReference(@NonNull String aasId, @NonNull String submodelId) throws ElementDoesNotExistException;

	/**
	 * Sets the asset information of the specified AAS.
	 *
	 * @param aasId
	 *            the identifier of the Asset Administration Shell; must not be
	 *            {@code null}
	 * @param assetInformation
	 *            the asset information to be set; must not be {@code null}
	 * @throws ElementDoesNotExistException
	 *             if the Asset Administration Shell with the specified
	 *             {@code aasId} does not exist
	 */
	void setAssetInformation(@NonNull String aasId, @NonNull AssetInformation assetInformation) throws ElementDoesNotExistException;

	/**
	 * Retrieves the asset information of the specified AAS.
	 *
	 * @param aasId
	 *            the identifier of the Asset Administration Shell; must not be
	 *            {@code null}
	 * @return the asset information of the AAS, or {@code null} if not found
	 * @throws ElementDoesNotExistException
	 *             if the Asset Administration Shell with the specified
	 *             {@code aasId} does not exist
	 */
	AssetInformation getAssetInformation(@NonNull String aasId) throws ElementDoesNotExistException;

	/**
	 * Retrieves the thumbnail for the specified AAS.
	 *
	 * @param aasId
	 *            the identifier of the Asset Administration Shell; must not be
	 *            {@code null}
	 * @return the file of the thumbnail, or {@code null} if no thumbnail exists
	 * @throws ElementDoesNotExistException
	 *             if the Asset Administration Shell with the specified
	 *             {@code aasId} does not exist
	 */
	File getThumbnail(@NonNull String aasId) throws ElementDoesNotExistException;

	/**
	 * Sets the thumbnail for the specified AAS.
	 *
	 * @param aasId
	 *            the identifier of the Asset Administration Shell; must not be
	 *            {@code null}
	 * @param fileName
	 *            the name of the thumbnail file (including extension); must not be
	 *            {@code null}
	 * @param contentType
	 *            the MIME type of the file; must not be {@code null}
	 * @param inputStream
	 *            the input stream containing the thumbnail data; must not be
	 *            {@code null}
	 * @throws ElementDoesNotExistException
	 *             if the Asset Administration Shell with the specified
	 *             {@code aasId} does not exist
	 */
	void setThumbnail(@NonNull String aasId, @NonNull String fileName, @NonNull String contentType, @NonNull InputStream inputStream) throws ElementDoesNotExistException;

	/**
	 * Deletes the thumbnail of the specified AAS.
	 *
	 * @param aasId
	 *            the identifier of the Asset Administration Shell; must not be
	 *            {@code null}
	 * @throws ElementDoesNotExistException
	 *             if the Asset Administration Shell with the specified
	 *             {@code aasId} does not exist
	 */
	void deleteThumbnail(@NonNull String aasId) throws ElementDoesNotExistException;
}