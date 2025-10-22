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

package org.eclipse.digitaltwin.basyx.aasservice.backend;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingSubmodelReferenceException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

import java.util.List;

/**
 * This interface provides backend-level operations for managing
 * {@link AssetAdministrationShell}s
 *
 * @author mateusmolina
 */
public interface AasOperations {

    /**
     * Retrieves all Asset Administration Shells
     *
     * @param assetIds  List of specific asset IDs to filter AASs
     * @param idShort   idShort to filter AASs
     * @param pInfo     the pagination information
     * @return          a {@code CursorResult} containing a list of Asset Administration Shells
     */
    CursorResult<List<AssetAdministrationShell>> getShells(List<SpecificAssetId> assetIds, String idShort, PaginationInfo pInfo);

    /**
     * Retrieves all Submodel References for the given AAS.
     *
     * @param aasId the identifier of the Asset Administration Shell
     * @param pInfo the pagination information 
     * @return a {@code CursorResult} containing a list of Submodel References
     * @throws ElementDoesNotExistException if the Asset Administration Shell with the specified
     *                                      {@code aasId} does not exist
     */
    CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) throws ElementDoesNotExistException;

    /**
     * Adds a Submodel Reference to the specified AAS.
     *
     * @param aasId             the identifier of the Asset Administration Shell
     * @param submodelReference the reference to be added
     * @throws ElementDoesNotExistException        if the Asset Administration Shell with the specified
     *                                             {@code aasId} does not exist
     * @throws CollidingSubmodelReferenceException if the provided submodel reference conflicts with an existing
     *                                             reference
     */
    void addSubmodelReference(String aasId, Reference submodelReference) throws ElementDoesNotExistException, CollidingSubmodelReferenceException;

    /**
     * Removes a Submodel Reference from the specified AAS.
     *
     * @param aasId      the identifier of the Asset Administration Shell
     * @param submodelId the identifier of the submodel to remove
     * @throws ElementDoesNotExistException if the Asset Administration Shell with the specified
     *                                      {@code aasId} does not exist
     */
    void removeSubmodelReference(String aasId, String submodelId) throws ElementDoesNotExistException;

    /**
     * Sets the asset information of the specified AAS.
     *
     * @param aasId            the identifier of the Asset Administration Shell
     * @param assetInformation the asset information to be set
     * @throws ElementDoesNotExistException if the Asset Administration Shell with the specified
     *                                      {@code aasId} does not exist
     */
    void setAssetInformation(String aasId, AssetInformation assetInformation) throws ElementDoesNotExistException;

    /**
     * Retrieves the asset information of the specified AAS.
     *
     * @param aasId the identifier of the Asset Administration Shell
     * @return the asset information of the AAS, or  if not found
     * @throws ElementDoesNotExistException if the Asset Administration Shell with the specified
     *                                      {@code aasId} does not exist
     */
    AssetInformation getAssetInformation(String aasId) throws ElementDoesNotExistException;

    /**
     * Retrieves all AASs that match the given asset IDs and idShort.
     *
     * @param assetIds the list of specific asset IDs to filter AASs
     * @param idShort  the idShort to filter AASs
     * @return an iterable collection of AssetAdministrationShells that match the criteria
     */
    Iterable<AssetAdministrationShell> getAllAas(List<SpecificAssetId> assetIds, String idShort);
}