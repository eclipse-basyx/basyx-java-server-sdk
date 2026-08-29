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
package org.eclipse.digitaltwin.basyx.aasenvironment;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.CrudAasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasBackend;
import org.eclipse.digitaltwin.basyx.aasenvironment.base.DefaultAASEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.preconfiguration.AasEnvironmentPreconfigurationLoader;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.backend.CrudConceptDescriptionRepositoryFactory;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.backend.InMemoryConceptDescriptionBackend;
import org.eclipse.digitaltwin.basyx.core.exceptions.ZipBombException;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.CrudSubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelBackend;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

/**
 * Tests the behavior of {@link AasEnvironmentPreconfigurationLoader}
 * 
 * @author sonnenberg, mateusmolina
 *
 */
public class PreconfigurationLoaderTextualResourceTest extends AasEnvironmentLoaderTest {
	private static final String TEST_ENVIRONMENT_INVALID_AASX = "/org/eclipse/digitaltwin/basyx/aasenvironment/invalid_environment.aasx";

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Override
	protected void loadRepositories(List<String> pathsToLoad) throws IOException, InvalidFormatException, DeserializationException, ZipBombException {
		AasEnvironmentPreconfigurationLoader envLoader = new AasEnvironmentPreconfigurationLoader(rLoader, pathsToLoad);
		envLoader.loadPreconfiguredEnvironments(new DefaultAASEnvironment(aasRepository, submodelRepository, conceptDescriptionRepository));
	}

	@Test
	public void testWithEmptyResource_NoElementsAreDeployed() throws InvalidFormatException, IOException, DeserializationException, ZipBombException {
		loadRepositories(List.of());
		Assert.assertTrue(aasRepository.getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().isEmpty());
		Assert.assertTrue(submodelRepository.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().isEmpty());
		Assert.assertTrue(conceptDescriptionRepository.getAllConceptDescriptions(PaginationInfo.NO_LIMIT).getResult().isEmpty());

		Mockito.verify(aasRepository, Mockito.never()).createAas(Mockito.any());
		Mockito.verify(aasRepository, Mockito.never()).updateAas(Mockito.anyString(), Mockito.any());

		Mockito.verify(submodelRepository, Mockito.never()).createSubmodel(Mockito.any());
		Mockito.verify(submodelRepository, Mockito.never()).createSubmodel(Mockito.any());
	}

	@Test
	public void testWithInvalidAasxResource_ValidResourcesAreStillDeployed() throws InvalidFormatException, IOException, DeserializationException, ZipBombException {
		loadRepositories(List.of(TEST_ENVIRONMENT_JSON, TEST_ENVIRONMENT_INVALID_AASX));

		Assert.assertEquals(2, aasRepository.getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(2, submodelRepository.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(2, conceptDescriptionRepository.getAllConceptDescriptions(PaginationInfo.NO_LIMIT).getResult().size());
	}

	@Test
	public void testWithTempDirectoryContainingValidAndCorruptAasx_ValidLoadsAndCorruptIsSkipped() throws IOException, SerializationException {
		File preconfigurationDirectory = temporaryFolder.newFolder("preconfiguration");
		createValidAasxFile(preconfigurationDirectory);

		File corruptAasx = new File(preconfigurationDirectory, "corrupt_environment.aasx");
		Files.write(corruptAasx.toPath(), "this is not a valid aasx package".getBytes(StandardCharsets.UTF_8));

		try {
			loadRepositories(List.of(preconfigurationDirectory.toURI().toString()));
		} catch (IOException | InvalidFormatException | DeserializationException | ZipBombException e) {
			Assert.fail("Expected no exception when loading directory with a corrupt aasx file, but got: " + e.getMessage());
		}

		Assert.assertEquals(1, aasRepository.getAllAas(null, null, PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(1, submodelRepository.getAllSubmodels(PaginationInfo.NO_LIMIT).getResult().size());
		Assert.assertEquals(0, conceptDescriptionRepository.getAllConceptDescriptions(PaginationInfo.NO_LIMIT).getResult().size());
	}

	private void createValidAasxFile(File targetDirectory) throws IOException, SerializationException {
		SubmodelRepository serializationSubmodelRepo = CrudSubmodelRepositoryFactory.builder()
				.backend(new InMemorySubmodelBackend())
				.fileRepository(new InMemoryFileRepository())
				.create();
		AasRepository serializationAasRepo = CrudAasRepositoryFactory.builder()
				.backend(new InMemoryAasBackend())
				.fileRepository(new InMemoryFileRepository())
				.create();
		ConceptDescriptionRepository serializationConceptDescriptionRepo = CrudConceptDescriptionRepositoryFactory.builder()
				.backend(new InMemoryConceptDescriptionBackend())
				.create();

		Submodel submodel = new DefaultSubmodel.Builder()
				.id("valid-submodel-id")
				.idShort("validSubmodel")
				.build();
		AssetAdministrationShell shell = new DefaultAssetAdministrationShell.Builder()
				.id("valid-shell-id")
				.idShort("validShell")
				.assetInformation(new DefaultAssetInformation.Builder()
						.assetKind(AssetKind.INSTANCE)
						.globalAssetId("valid-submodel-id")
						.build())
				.build();
		Collection<Submodel> submodels = List.of(submodel);
		Collection<AssetAdministrationShell> shells = List.of(shell);

		submodels.forEach(serializationSubmodelRepo::createSubmodel);
		shells.forEach(serializationAasRepo::createAas);

		AasEnvironment serializerEnvironment = new DefaultAASEnvironment(serializationAasRepo, serializationSubmodelRepo, serializationConceptDescriptionRepo);
		byte[] validAasxBytes = serializerEnvironment.createAASXAASEnvironmentSerialization(
				shells.stream().map(AssetAdministrationShell::getId).collect(Collectors.toList()),
				submodels.stream().map(Submodel::getId).collect(Collectors.toList()),
				false);

		Files.write(new File(targetDirectory, "valid_environment.aasx").toPath(), validAasxBytes);
	}
}
