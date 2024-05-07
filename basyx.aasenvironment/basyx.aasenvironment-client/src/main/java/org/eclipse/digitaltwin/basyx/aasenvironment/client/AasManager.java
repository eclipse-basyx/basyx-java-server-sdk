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

package org.eclipse.digitaltwin.basyx.aasenvironment.client;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;

/**
 * Interface for a component that manages Asset Administration Shell (AAS) and
 * Submodel Repositories/Registries in sync
 *
 * @author mateusmolina
 *
 */
public interface AasManager {

	/**
	 * Retrieves an AAS in an AAS registry by its identifier.
	 *
	 * @param identifier
	 *            The identifier of the AAS to retrieve.
	 * @return The retrieved AAS object.
	 */
	public AssetAdministrationShell getAas(String identifier);

	/**
	 * Retrieves a Submodel in a Submodel registry by its identifier.
	 *
	 * @param identifier
	 *            The identifier of the submodel to retrieve.
	 * @return The retrieved Submodel object.
	 */
	public Submodel getSubmodel(String identifier);

	/**
	 * Retrieves a Submodel associated with a specified AAS.
	 *
	 * @param aasIdentifier
	 *            The identifier of the AAS.
	 * @param smIdentifier
	 *            The identifier of the submodel.
	 * @return The retrieved Submodel object associated with the specified AAS.
	 */
	public Submodel getSubmodelOfAas(String aasIdentifier, String smIdentifier);

	/**
	 * Deletes an AAS by its identifier.
	 *
	 * @param identifier
	 *            The identifier of the AAS to delete.
	 */
	public void deleteAas(String identifier);

	/**
	 * Deletes a submodel associated with a specified AAS.
	 *
	 * @param aasIdentifier
	 *            The identifier of the AAS.
	 * @param smIdentifier
	 *            The identifier of the submodel to delete.
	 */
	public void deleteSubmodelOfAas(String aasIdentifier, String smIdentifier);

	/**
	 * Creates a new AAS
	 *
	 * @param aas
	 *            The AAS object to create.
	 */
	public void createAas(AssetAdministrationShell aas);

	/**
	 * Creates a submodel under a specified AAS.
	 *
	 * @param aasIdentifier
	 *            The identifier of the AAS.
	 * @param submodel
	 *            The Submodel object to create under the specified AAS.
	 */
	public void createSubmodelInAas(String aasIdentifier, Submodel submodel);


}
