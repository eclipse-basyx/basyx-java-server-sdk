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

package org.eclipse.digitaltwin.basyx.aasenvironment.component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.AASXDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class AasEnvironmentPreconfigurationLoader {

	private List<String> filesToLoad;
	private ResourceLoader resourceLoader;

	public AasEnvironmentPreconfigurationLoader(ResourceLoader resourceLoader, List<String> filesToLoad) {
		this.resourceLoader = resourceLoader;
		this.filesToLoad = filesToLoad;
	}

	public boolean shouldLoadPreconfiguredEnvironment() {
		return filesToLoad != null;
	}

	public void loadPrefconfiguredEnvironment(AasRepository aasRepository, SubmodelRepository submodelRepository,
			ConceptDescriptionRepository conceptDescriptionRepository)
			throws IOException, DeserializationException, InvalidFormatException {
		for (String filePath : filesToLoad) {

			InputStream fileStream = getFileInputStream(filePath);
			Environment environment = getEnvironmentFromInputStream(filePath, fileStream);
			loadEnvironmentFromFile(aasRepository, submodelRepository, conceptDescriptionRepository, environment);

		}
	}

	private void loadEnvironmentFromFile(AasRepository aasRepository, SubmodelRepository submodelRepository,
			ConceptDescriptionRepository conceptDescriptionRepository, Environment environment) {
		if (isEnvironmentLoaded(environment)) {
			createShellsOnRepositoryFromEnvironment(aasRepository, environment);
			createSubmodelsOnRepositoryFromEnvironment(submodelRepository, environment);
			createConceptDescriptionsOnRepositoryFromEnvironment(conceptDescriptionRepository, environment);
		}
	}

	private void createConceptDescriptionsOnRepositoryFromEnvironment(
			ConceptDescriptionRepository conceptDescriptionRepository, Environment environment) {
		for (ConceptDescription conceptDescription : environment.getConceptDescriptions()) {
			conceptDescriptionRepository.createConceptDescription(conceptDescription);
		}
	}

	private void createSubmodelsOnRepositoryFromEnvironment(SubmodelRepository submodelRepository,
			Environment environment) {
		for (Submodel submodel : environment.getSubmodels()) {
			submodelRepository.createSubmodel(submodel);
		}
	}

	private void createShellsOnRepositoryFromEnvironment(AasRepository aasRepository, Environment environment) {
		for (AssetAdministrationShell aas : environment.getAssetAdministrationShells()) {
			aasRepository.createAas(aas);
		}
	}

	private Environment getEnvironmentFromInputStream(String filePath, InputStream fileStream)
			throws DeserializationException, InvalidFormatException, IOException {
		Environment environment = null;
		if (isJsonFile(filePath)) {
			JsonDeserializer deserializer = new JsonDeserializer();
			environment = deserializer.read(fileStream);
		} else if (isXmlFile(filePath)) {
			XmlDeserializer deserializer = new XmlDeserializer();
			environment = deserializer.read(fileStream);
		} else if (isAasxFile(filePath)) {
			AASXDeserializer deserializer = new AASXDeserializer(fileStream);
			environment = deserializer.read();
		}
		return environment;
	}

	private static boolean isJsonFile(String filePath) {
		return filePath.endsWith(".json");
	}

	private static boolean isXmlFile(String filePath) {
		return filePath.endsWith(".xml");
	}

	private static boolean isAasxFile(String filePath) {
		return filePath.endsWith(".aasx");
	}

	private InputStream getFileInputStream(String filePath) throws IOException {
		Resource resource = resourceLoader.getResource(filePath);
		return resource.getInputStream();
	}

	private boolean isEnvironmentLoaded(Environment environment) {
		return environment != null;
	}

}
