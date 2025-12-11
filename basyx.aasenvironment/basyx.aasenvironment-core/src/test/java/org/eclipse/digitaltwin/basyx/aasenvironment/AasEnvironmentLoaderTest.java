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

package org.eclipse.digitaltwin.basyx.aasenvironment;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.basyx.aasenvironment.base.DefaultAASEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.CompleteEnvironment;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.CrudAasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasBackend;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.backend.CrudConceptDescriptionRepositoryFactory;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.backend.InMemoryConceptDescriptionBackend;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ZipBombException;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.CrudSubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelBackend;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Tests the behavior of {@link AasEnvironment} loader functionality
 * 
 * @author sonnenberg, mateusmolina, danish
 *
 */
public class AasEnvironmentLoaderTest {

	protected static final String TEST_ENVIRONMENT_JSON = "/org/eclipse/digitaltwin/basyx/aasenvironment/environment.json";
	protected static final String TEST_ENVIRONMENT_VERSION_ON_SECOND_JSON = "/org/eclipse/digitaltwin/basyx/aasenvironment/environment_version_on_second.json";
	protected static final String TEST_ENVIRONMENT_VERSION_AND_REVISION_ON_SECOND_JSON = "/org/eclipse/digitaltwin/basyx/aasenvironment/environment_version_and_revision_on_second.json";

	protected static final String TEST_ENVIRONMENT_SHELLS_ONLY_JSON = "/org/eclipse/digitaltwin/basyx/aasenvironment/environment_with_shells_only.json";
	protected static final String TEST_ENVIRONMENT_SUBMODELS_ONLY_JSON = "/org/eclipse/digitaltwin/basyx/aasenvironment/environment_with_submodels_only.json";

	protected AasRepository aasRepository;
	protected SubmodelRepository submodelRepository;
	protected ConceptDescriptionRepository conceptDescriptionRepository;
	protected DefaultResourceLoader rLoader = new DefaultResourceLoader();

	@Before
	public void setUp() {
		submodelRepository = Mockito.spy(CrudSubmodelRepositoryFactory.builder().backend(new InMemorySubmodelBackend()).fileRepository(new InMemoryFileRepository()).create());
		aasRepository = Mockito.spy(CrudAasRepositoryFactory.builder().backend(new InMemoryAasBackend()).fileRepository(new InMemoryFileRepository()).create());
		conceptDescriptionRepository = Mockito.spy(CrudConceptDescriptionRepositoryFactory.builder().backend(new InMemoryConceptDescriptionBackend()).create());
	}

	protected void loadRepositories(List<String> pathsToLoad) throws IOException, DeserializationException, InvalidFormatException, ZipBombException {
		DefaultAASEnvironment envLoader = new DefaultAASEnvironment(aasRepository, submodelRepository, conceptDescriptionRepository);
		
		for (String path: pathsToLoad) {
			File file = rLoader.getResource(path).getFile();
			envLoader.loadEnvironment(CompleteEnvironment.fromFile(file));
		}
	}

	@Test
	public void testWithResourceFile_AllElementsAreDeployed() throws InvalidFormatException, IOException, DeserializationException, ZipBombException {
		loadRepositories(List.of(TEST_ENVIRONMENT_JSON));

		Assert.assertEquals(2, aasRepository.getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(2, submodelRepository.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(2, conceptDescriptionRepository.getAllConceptDescriptions(PaginationInfo.NO_LIMIT).getResult().size());
	}

	@Test
	public void testDeployedTwiceNoVersion_AllDeployedButNotOverriden() throws InvalidFormatException, IOException, DeserializationException, ZipBombException {
		loadRepositories(List.of(TEST_ENVIRONMENT_JSON));
		loadRepositories(List.of(TEST_ENVIRONMENT_JSON));

		Mockito.verify(aasRepository, Mockito.times(2)).createAas(Mockito.any());
		Mockito.verify(aasRepository, Mockito.times(0)).updateAas(Mockito.anyString(), Mockito.any());

		Mockito.verify(submodelRepository, Mockito.times(2)).createSubmodel(Mockito.any());
		Mockito.verify(submodelRepository, Mockito.times(0)).updateSubmodel(Mockito.anyString(), Mockito.any());

		Assert.assertEquals(2, aasRepository.getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(2, submodelRepository.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(2, conceptDescriptionRepository.getAllConceptDescriptions(PaginationInfo.NO_LIMIT).getResult().size());
	}

	@Test
	public void testDeployedTwiceWithSameVersion_AllDeployedButNotOverriden() throws InvalidFormatException, IOException, DeserializationException, ZipBombException {
		loadRepositories(List.of(TEST_ENVIRONMENT_VERSION_ON_SECOND_JSON));
		loadRepositories(List.of(TEST_ENVIRONMENT_VERSION_ON_SECOND_JSON));

		Mockito.verify(aasRepository, Mockito.times(2)).createAas(Mockito.any());
		Mockito.verify(aasRepository, Mockito.times(0)).updateAas(Mockito.anyString(), Mockito.any());

		Mockito.verify(submodelRepository, Mockito.times(2)).createSubmodel(Mockito.any());
		Mockito.verify(submodelRepository, Mockito.times(0)).updateSubmodel(Mockito.anyString(), Mockito.any());

		Assert.assertEquals(2, aasRepository.getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(2, submodelRepository.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(2, conceptDescriptionRepository.getAllConceptDescriptions(PaginationInfo.NO_LIMIT).getResult().size());
	}

	@Test
	public void testDeployedTwiceNewRevision_ElementsAreOverriden() throws InvalidFormatException, IOException, DeserializationException, ZipBombException {
		loadRepositories(List.of(TEST_ENVIRONMENT_VERSION_ON_SECOND_JSON));
		loadRepositories(List.of(TEST_ENVIRONMENT_VERSION_AND_REVISION_ON_SECOND_JSON));

		Mockito.verify(aasRepository, Mockito.times(2)).createAas(Mockito.any());
		Mockito.verify(aasRepository, Mockito.times(1)).updateAas(Mockito.anyString(), Mockito.any());

		Mockito.verify(submodelRepository, Mockito.times(2)).createSubmodel(Mockito.any());
		Mockito.verify(submodelRepository, Mockito.times(1)).updateSubmodel(Mockito.anyString(), Mockito.any());

		Assert.assertEquals(2, aasRepository.getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(2, submodelRepository.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(2, conceptDescriptionRepository.getAllConceptDescriptions(PaginationInfo.NO_LIMIT).getResult().size());
	}

	@Test
	public void testDuplicateSubmodelIdsInEnvironments_ExceptionIsThrown() throws InvalidFormatException, IOException, DeserializationException {

		String expectedMsg = new CollidingIdentifierException("aas1").getMessage();
		Assert.assertThrows(expectedMsg, CollidingIdentifierException.class, () -> loadRepositories(List.of(TEST_ENVIRONMENT_SHELLS_ONLY_JSON, TEST_ENVIRONMENT_SHELLS_ONLY_JSON)));
	}

	@Test
	public void testDuplicateShellIdsInEnvironments_ExceptionIsThrown() {
		String expectedMsg = new CollidingIdentifierException("sm1").getMessage();
		Assert.assertThrows(expectedMsg, CollidingIdentifierException.class, () -> loadRepositories(List.of(TEST_ENVIRONMENT_SUBMODELS_ONLY_JSON, TEST_ENVIRONMENT_SUBMODELS_ONLY_JSON)));
	}
	
	@Test
	public void testWithResourceFile_NoExceptionsWhenReuploadAfterElementsAreRemoved() throws InvalidFormatException, IOException, DeserializationException, ZipBombException {
		AasEnvironment envLoader = new DefaultAASEnvironment(aasRepository, submodelRepository, conceptDescriptionRepository);
		
		loadRepositoriesWithEnvironment(List.of(TEST_ENVIRONMENT_JSON), envLoader);

		Assert.assertEquals(2, aasRepository.getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(2, submodelRepository.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(2, conceptDescriptionRepository.getAllConceptDescriptions(PaginationInfo.NO_LIMIT).getResult().size());
		
		deleteElementsFromRepos();
		
		loadRepositoriesWithEnvironment(List.of(TEST_ENVIRONMENT_JSON), envLoader);
		
		Assert.assertEquals(2, aasRepository.getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(2, submodelRepository.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(2, conceptDescriptionRepository.getAllConceptDescriptions(PaginationInfo.NO_LIMIT).getResult().size());
	}
	
	@Test
	public void testWithResourceFile_ExceptionIsThrownWhenReuploadWithExistingElements() throws InvalidFormatException, IOException, DeserializationException, ZipBombException {
		AasEnvironment envLoader = new DefaultAASEnvironment(aasRepository, submodelRepository, conceptDescriptionRepository);
		
		loadRepositoriesWithEnvironment(List.of(TEST_ENVIRONMENT_JSON), envLoader);

		Assert.assertEquals(2, aasRepository.getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(2, submodelRepository.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(2, conceptDescriptionRepository.getAllConceptDescriptions(PaginationInfo.NO_LIMIT).getResult().size());
		
		String expectedMsg = new CollidingIdentifierException("aas1").getMessage();
		Assert.assertThrows(expectedMsg, CollidingIdentifierException.class, () -> loadRepositoriesWithEnvironment(List.of(TEST_ENVIRONMENT_JSON), envLoader));
	}
	
	private void loadRepositoriesWithEnvironment(List<String> pathsToLoad, AasEnvironment aasEnvironment) throws IOException, DeserializationException, InvalidFormatException, ZipBombException {
		
		for (String path: pathsToLoad) {
			File file = rLoader.getResource(path).getFile();
			aasEnvironment.loadEnvironment(CompleteEnvironment.fromFile(file));
		}
	}
	
	private void deleteElementsFromRepos() {
		aasRepository.getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().stream().forEach(aas -> aasRepository.deleteAas(aas.getId()));
		submodelRepository.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().stream().forEach(sm -> submodelRepository.deleteSubmodel(sm.getId()));
		conceptDescriptionRepository.getAllConceptDescriptions(PaginationInfo.NO_LIMIT).getResult().stream().forEach(cd -> conceptDescriptionRepository.deleteConceptDescription(cd.getId()));
		
		Assert.assertEquals(0, aasRepository.getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(0, submodelRepository.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(0, conceptDescriptionRepository.getAllConceptDescriptions(PaginationInfo.NO_LIMIT).getResult().size());
	}
	
}