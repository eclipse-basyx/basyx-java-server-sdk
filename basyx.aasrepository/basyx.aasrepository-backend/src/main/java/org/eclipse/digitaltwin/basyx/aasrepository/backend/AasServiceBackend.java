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
package org.eclipse.digitaltwin.basyx.aasrepository.backend;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * AASService Backend Fragment
 * 
 * #TODO this could be moved to the AASService package and reused there and
 * underlying AasService Implementation
 * 
 * @author mateusmolina
 *
 */
public interface AasServiceBackend {

	/**
	 * Retrieves all Submodel References
	 * 
	 * @return A List containing all Submodel References
	 */
	public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo);

	/**
	 * Adds a Submodel Reference
	 */
	public void addSubmodelReference(String aasId, Reference submodelReference);

	/**
	 * Removes a Submodel Reference
	 */
	public void removeSubmodelReference(String aasId, String submodelId);

	/**
	 * Sets the asset-information of the AAS contained in the server
	 */
	public void setAssetInformation(String aasId, AssetInformation aasInfo);

	/**
	 * Retrieves the asset-information of the AAS contained in the server
	 * 
	 * @return the Asset-Information of the AAS
	 */
	public AssetInformation getAssetInformation(String aasId);

	/**
	 * Get Thumbnail of the specific aas
	 * 
	 * @return the file of the thumbnail
	 */
	public File getThumbnail(String aasId);

	/**
	 * Set Thumbnail of the AAS
	 * 
	 * @param fileName
	 *            name of the thumbnail file with extension
	 * @param contentType
	 *            content type of the file
	 * @param inputStream
	 *            inputstream of the thumbnail file
	 */
	public void setThumbnail(String aasId, String fileName, String contentType, InputStream inputStream);

	/**
	 * Delete the thumbnail file of the AAS
	 * 
	 */
	public void deleteThumbnail(String aasId);
}
