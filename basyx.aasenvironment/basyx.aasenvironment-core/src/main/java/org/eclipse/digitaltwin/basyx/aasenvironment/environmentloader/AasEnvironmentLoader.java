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

package org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.InMemoryFile;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.aasenvironment.FileElementPathCollector;
import org.eclipse.digitaltwin.basyx.aasenvironment.IdShortPathBuilder;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.IdentifiableUploader.DelegatingIdentifiableRepository;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.IdentifiableUploader.IdentifiableRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loader for AAS Environment
 *
 * @author fried, mateusmolina, despen, witt, jungjan, danish
 *
 */
public class AasEnvironmentLoader {
	private Logger logger = LoggerFactory.getLogger(AasEnvironmentLoader.class);
	private IndentifiableAssertion checker = new IndentifiableAssertion();

	private AasRepository aasRepository;
	private SubmodelRepository submodelRepository;
	private ConceptDescriptionRepository conceptDescriptionRepository;

	public AasEnvironmentLoader(AasRepository aasRepository, SubmodelRepository submodelRepository, ConceptDescriptionRepository conceptDescriptionRepository) {
		this.aasRepository = aasRepository;
		this.submodelRepository = submodelRepository;
		this.conceptDescriptionRepository = conceptDescriptionRepository;
	}

	public void loadEnvironment(CompleteEnvironment completeEnvironment) {
		Environment environment = completeEnvironment.getEnvironment();

		if (environment == null)
			return;

		checker.assertNoDuplicateIds(environment);

		createShellsOnRepositoryFromEnvironment(environment);
		createSubmodelsOnRepositoryFromEnvironment(environment, completeEnvironment.getRelatedFiles());
		createConceptDescriptionsOnRepositoryFromEnvironment(environment);
	}

	private void createConceptDescriptionsOnRepositoryFromEnvironment(Environment environment) {
		IdentifiableRepository<ConceptDescription> repo = new DelegatingIdentifiableRepository<ConceptDescription>(conceptDescriptionRepository::getConceptDescription, conceptDescriptionRepository::updateConceptDescription,
				conceptDescriptionRepository::createConceptDescription);
		IdentifiableUploader<ConceptDescription> uploader = new IdentifiableUploader<ConceptDescription>(repo);
		for (ConceptDescription conceptDescription : environment.getConceptDescriptions()) {
			boolean success = uploader.upload(conceptDescription);
			logSuccessConceptDescription(conceptDescription.getId(), success);
		}
	}

	private void createSubmodelsOnRepositoryFromEnvironment(Environment environment, List<InMemoryFile> relatedFiles) {
		List<Submodel> submodels = environment.getSubmodels();

		createSubmodelsOnRepository(submodels);

		if (relatedFiles == null || relatedFiles.isEmpty())
			return;

		for (Submodel submodel : submodels) {
			List<List<SubmodelElement>> idShortElementPathsOfAllFileSMEs = new FileElementPathCollector(submodel).collect();

			idShortElementPathsOfAllFileSMEs.stream().forEach(fileSMEIdShortPath -> setFileToFileElement(submodel.getId(), fileSMEIdShortPath, relatedFiles));
		}
	}

	private void setFileToFileElement(String submodelId, List<SubmodelElement> fileSMEIdShortPathElements, List<InMemoryFile> relatedFiles) {
		String fileSMEIdShortPath = new IdShortPathBuilder(new ArrayList<>(fileSMEIdShortPathElements)).build();

		org.eclipse.digitaltwin.aas4j.v3.model.File fileSME = (org.eclipse.digitaltwin.aas4j.v3.model.File) submodelRepository.getSubmodelElement(submodelId, fileSMEIdShortPath);

		InMemoryFile inMemoryFile = getAssociatedInMemoryFile(relatedFiles, fileSME.getValue());

		if (inMemoryFile == null) {
			logger.info("Unable to set file to the SubmodelElement File with IdShortPath '{}' because it does not exist in the AASX file.", fileSMEIdShortPath);

			return;
		}

		submodelRepository.setFileValue(submodelId, fileSMEIdShortPath, getFileName(inMemoryFile.getPath()), new ByteArrayInputStream(inMemoryFile.getFileContent()));
	}

	private String getFileName(String path) {
		return FilenameUtils.getName(path);
	}

	private InMemoryFile getAssociatedInMemoryFile(List<InMemoryFile> relatedFiles, String value) {

		Optional<InMemoryFile> inMemoryFile = relatedFiles.stream().filter(file -> file.getPath().equals(value)).findAny();

		if (inMemoryFile.isEmpty())
			return null;

		return inMemoryFile.get();
	}

	private void createShellsOnRepositoryFromEnvironment(Environment environment) {
		IdentifiableRepository<AssetAdministrationShell> repo = new DelegatingIdentifiableRepository<AssetAdministrationShell>(aasRepository::getAas, aasRepository::updateAas, aasRepository::createAas);
		IdentifiableUploader<AssetAdministrationShell> uploader = new IdentifiableUploader<>(repo);
		for (AssetAdministrationShell shell : environment.getAssetAdministrationShells()) {
			boolean success = uploader.upload(shell);
			logSuccess("shell", shell.getId(), success);
		}
	}

	private void createSubmodelsOnRepository(List<Submodel> submodels) {
		IdentifiableRepository<Submodel> repo = new DelegatingIdentifiableRepository<Submodel>(submodelRepository::getSubmodel, submodelRepository::updateSubmodel, submodelRepository::createSubmodel);
		IdentifiableUploader<Submodel> uploader = new IdentifiableUploader<>(repo);
		for (Submodel submodel : submodels) {
			boolean success = uploader.upload(submodel);
			logSuccess("submodel", submodel.getId(), success);
		}
	}

	private void logSuccess(String resourceName, String id, boolean success) {
		if (success) {
			logger.info("Uploading " + resourceName + " " + id + " was successful!");
		} else {
			logger.warn("Uploading " + resourceName + " " + id + " was not successful!");
		}
	}

	private void logSuccessConceptDescription(String conceptDescriptionId, boolean success) {
		if (!success) {
			logger.warn("Colliding Ids detected for ConceptDescription: " + conceptDescriptionId + ". If they are not identical, this is an error. Please note that the already existing ConceptDescription was not updated.");
		} else {
			logSuccess("conceptDescription", conceptDescriptionId, success);
		}
	}
}
