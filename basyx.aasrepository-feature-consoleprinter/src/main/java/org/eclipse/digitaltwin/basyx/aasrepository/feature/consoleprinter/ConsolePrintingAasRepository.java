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

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.aasrepository.exceptions.ElementDoesNotExistException;

public class ConsolePrintingAasRepository implements AasRepository {
	private AasRepository decorated;

	public ConsolePrintingAasRepository(AasRepository decorated) {
		this.decorated = decorated;
	}

	@Override
	public List<AssetAdministrationShell> getAASList() {
		System.out.println("Getting all shells");
		return decorated.getAASList();
	}

	@Override
	public AssetAdministrationShell getAAS(String aasId) throws ElementDoesNotExistException {
		System.out.println("Getting single shell with id " + aasId);
		return decorated.getAAS(aasId);
	}

	@Override
	public void createAAS(AssetAdministrationShell aas) throws CollidingIdentifierException {
		System.out.println("Creating shell with id " + aas.getId());
		decorated.createAAS(aas);
	}

	@Override
	public void updateAAS(AssetAdministrationShell aas) {
		System.out.println("Updating shell with id " + aas.getId());
		decorated.updateAAS(aas);
	}

	@Override
	public void deleteAAS(String aasId) {
		System.out.println("Deleting shell with id " + aasId);
		decorated.deleteAAS(aasId);
	}

}
