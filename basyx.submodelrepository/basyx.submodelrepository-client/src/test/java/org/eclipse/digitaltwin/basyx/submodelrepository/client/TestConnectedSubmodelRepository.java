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

package org.eclipse.digitaltwin.basyx.submodelrepository.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotInvokableException;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.core.SubmodelRepositorySuite;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.DummySubmodelRepositoryComponent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Features of the client not implemented but existing in the test suite are
 * overwritten to pass. This is required to enable reuse of the test suite.
 * Whenever a feature is implemented, the respective test here has to be
 * removed.
 * 
 * @author schnicke
 */
public class TestConnectedSubmodelRepository extends SubmodelRepositorySuite {
	private static ConfigurableApplicationContext appContext;
	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, null);

	@BeforeClass
	public static void startAASRepo() throws Exception {
		appContext = new SpringApplication(DummySubmodelRepositoryComponent.class).run(new String[] {});
	}

	@After
	public void removeSubmodelFromRepo() {
		SubmodelRepository repo = appContext.getBean(SubmodelRepository.class);
		repo.getAllSubmodels(NO_LIMIT_PAGINATION_INFO).getResult().stream().map(s -> s.getId()).forEach(repo::deleteSubmodel);
	}

	@AfterClass
	public static void shutdownAASRepo() {
		appContext.close();
	}

	@Override
	public void getAllSubmodelsPreconfigured() {
		// TODO: Not Implemented
	}

	@Override
	public void getAllSubmodelsEmpty() {
		// TODO: Not Implemented
	}

	@Override
	public void updateExistingSubmodelWithMismatchId() {
		// TODO: Not Implemented
		throw new IdentificationMismatchException();
	}

	@Override
	public void createSubmodelCollectionWithMissingId() {
		// TODO: Not Implemented
		throw new MissingIdentifierException();
	}

	@Override
	public void updateNonFileSME() {
		// TODO: Not Implemented
	}

	@Override
	public void updateNonFileSMEWithFileSME() {
		// TODO: Not Implemented
	}

	@Override
	public void updateFileSMEWithNonFileSME() throws FileNotFoundException, IOException {
		// TODO: Not Implemented
	}

	@Override
	public void updateFileSMEWithFileSME() throws FileNotFoundException, IOException {
		// TODO: Not Implemented
	}

	@Override
	public void updateNonExistingSME() {
		// TODO: Not Implemented
		throw new ElementDoesNotExistException();
	}

	@Override
	public void getSubmodelElements() {
		// TODO: Not Implemented
	}

	@Override
	public void getSubmodelElementsOfNonExistingSubmodel() {
		// TODO: Not Implemented
		throw new ElementDoesNotExistException();
	}

	@Override
	public void getSubmodelElement() {
		// TODO: Not Implemented
	}

	@Override
	public void getNonExistingSubmodelElement() {
		// TODO: Not Implemented
		throw new ElementDoesNotExistException();
	}

	@Override
	public void getSubmodelElementOfNonExistingSubmodel() {
		// TODO: Not Implemented
		throw new ElementDoesNotExistException();
	}

	@Override
	public void getPropertyValue() {
		// TODO: Not Implemented
	}

	@Override
	public void getNonExistingSubmodelElementValue() {
		// TODO: Not Implemented
		throw new ElementDoesNotExistException();
	}

	@Override
	public void getSubmodelElementValueOfNonExistingSubmodel() {
		// TODO: Not Implemented
		throw new ElementDoesNotExistException();
	}

	@Override
	public void setPropertyValue() {
		// TODO: Not Implemented
	}

	@Override
	public void updateFile() {
		// TODO: Not Implemented
	}

	@Override
	public void getFile() {
		// TODO: Not Implemented
	}

	@Override
	public void getNonExistingFile() {
		// TODO: Not Implemented
		throw new FileDoesNotExistException();
	}

	@Override
	public void getFileFromNonFileSME() {
		// TODO: Not Implemented
		throw new ElementNotAFileException();
	}

	@Override
	public void deleteFile() throws ElementDoesNotExistException, ElementNotAFileException, FileNotFoundException, IOException {
		// TODO: Not Implemented
	}

	@Override
	public void deleteNonExistingFile() throws IOException {
		// TODO: Not Implemented
		throw new FileDoesNotExistException();
	}

	@Override
	public void setNonExistingSubmodelElementValue() {
		// TODO: Not Implemented
		throw new ElementDoesNotExistException();
	}

	@Override
	public void setSubmodelElementValueOfNonExistingSubmodel() {
		// TODO: Not Implemented
		throw new ElementDoesNotExistException();
	}

	@Override
	public void createSubmodelElement() {
		// TODO: Not Implemented
	}

	@Override
	public void deleteSubmodeleElement() {
		// TODO: Not Implemented
	}

	@Override
	public void createNestedSubmodelELement() {
		// TODO: Not Implemented
	}

	@Override
	public void deleteNestedSubmodelElementInSubmodelElementCollection() {
		// TODO: Not Implemented
	}

	@Override
	public void deleteNestedSubmodelElementInSubmodelElementList() {
		// TODO: Not Implemented
	}

	@Override
	public void getDefaultSubmodelRepositoryName() {
		// TODO: Not Implemented
	}

	@Override
	public void getPaginatedSubmodel() {
		// TODO: Not Implemented
	}

	@Override
	public void invokeOperation() {
		// TODO: Not Implemented
	}

	@Override
	public void invokeNonOperation() {
		// TODO: Not Implemented
		throw new NotInvokableException();
	}

	@Override
	protected SubmodelRepository getSubmodelRepository() {
		return new ConnectedSubmodelRepositoryWrapper(new ConnectedSubmodelRepository("http://localhost:8080"));
	}

	@Override
	protected SubmodelRepository getSubmodelRepository(Collection<Submodel> submodels) {
		SubmodelRepository repo = appContext.getBean(SubmodelRepository.class);
		submodels.forEach(repo::createSubmodel);
		return getSubmodelRepository();
	}
}
