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


package org.eclipse.digitaltwin.basyx.aasrepository.feature.consoleprinter;

import java.util.Collection;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;

public class ConsolePrintingAasRepository implements AasRepository {
	private AasRepository decorated;

	public ConsolePrintingAasRepository(AasRepository decorated) {
		this.decorated = decorated;
	}

	@Override
	public Collection<AssetAdministrationShell> getAllAas() {
		System.out.println("Getting all shells");
		return decorated.getAllAas();
	}

	@Override
	public AssetAdministrationShell getAas(String aasId) throws ElementDoesNotExistException {
		System.out.println("Getting single shell with id " + aasId);
		return decorated.getAas(aasId);
	}

	@Override
	public void createAas(AssetAdministrationShell aas) throws CollidingIdentifierException {
		System.out.println("Creating shell with id " + aas.getId());
		decorated.createAas(aas);
	}

	@Override
	public void updateAas(AssetAdministrationShell aas) {
		System.out.println("Updating shell with id " + aas.getId());
		decorated.updateAas(aas);
	}

	@Override
	public void deleteAas(String aasId) {
		System.out.println("Deleting shell with id " + aasId);
		decorated.deleteAas(aasId);
	}

	@Override
	public List<Reference> getSubmodelReferences(String aasId) {
		System.out.println("Getting Submodel References of Shell with id " + aasId);
		return decorated.getSubmodelReferences(aasId);
	}

	@Override
	public void addSubmodelReference(String aasId, Reference submodelReference) {
		String id = getSubmodelId(submodelReference);
		System.out.println("Adding Submodel Reference (ID: " + id + ") to Shell with id " + aasId);
		decorated.addSubmodelReference(aasId, submodelReference);
	}

	@Override
	public void removeSubmodelReference(String aasId, String submodelId) {
		System.out.println("Removing Submodel Reference (ID:" + submodelId + ") from Shell with id " + aasId);
		decorated.removeSubmodelReference(aasId, submodelId);
	}

	private String getSubmodelId(Reference submodelReference) {
		return submodelReference.getKeys().stream().filter(key -> key.getType().equals(KeyTypes.SUBMODEL)).findFirst()
				.get().getValue();
	}

}
