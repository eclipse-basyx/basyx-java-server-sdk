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
package org.eclipse.digitaltwin.basyx.aasenvironment.base;

import jakarta.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.AASXSerializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.AasEnvironmentSerialization;
import org.eclipse.digitaltwin.basyx.aasenvironment.ConceptDescriptionIdCollector;
import org.eclipse.digitaltwin.basyx.aasenvironment.MetamodelCloneCreator;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link AasEnvironmentSerialization}
 *
 * @author zhangzai, danish
 */
public class DefaultAASEnvironmentSerialization implements AasEnvironmentSerialization {

	private static Logger logger = LoggerFactory.getLogger(DefaultAASEnvironmentSerialization.class);

	private AasRepository aasRepository;
	private SubmodelRepository submodelRepository;
	private ConceptDescriptionRepository conceptDescriptionRepository;
	private JsonSerializer jsonSerializer = new JsonSerializer();
	private XmlSerializer xmlSerializer = new XmlSerializer();
	private AASXSerializer aasxSerializer = new AASXSerializer();
	private MetamodelCloneCreator cloneCreator = new MetamodelCloneCreator();

	public DefaultAASEnvironmentSerialization(AasRepository aasRepository, SubmodelRepository submodelRepository, ConceptDescriptionRepository conceptDescriptionRepository) {
		this.aasRepository = aasRepository;
		this.submodelRepository = submodelRepository;
		this.conceptDescriptionRepository = conceptDescriptionRepository;
	}

	@Override
	public String createJSONAASEnvironmentSerialization(@Valid List<String> aasIds, @Valid List<String> submodelIds, @Valid boolean includeConceptDescriptions) throws SerializationException {
		Environment aasEnvironment = createEnvironment(aasIds, submodelIds, includeConceptDescriptions);
		return jsonSerializer.write(aasEnvironment);
	}

	@Override
	public String createXMLAASEnvironmentSerialization(@Valid List<String> aasIds, @Valid List<String> submodelIds, @Valid boolean includeConceptDescriptions) throws SerializationException {
		Environment aasEnvironment = createEnvironment(aasIds, submodelIds, includeConceptDescriptions);

		return xmlSerializer.write(aasEnvironment);
	}

	@Override
	public byte[] createAASXAASEnvironmentSerialization(@Valid List<String> aasIds, @Valid List<String> submodelIds, @Valid boolean includeConceptDescriptions) throws SerializationException, IOException {
		Environment aasEnvironment = createEnvironment(aasIds, submodelIds, includeConceptDescriptions);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		aasxSerializer.write(aasEnvironment, null, outputStream);
		return outputStream.toByteArray();
	}

	private Environment createEnvironment(List<String> aasIds, List<String> submodelIds, boolean includeConceptDescriptions) {
		List<AssetAdministrationShell> shells = aasIds.stream().map(aasRepository::getAas).collect(Collectors.toList());
		List<Submodel> submodels = submodelIds.stream().map(submodelRepository::getSubmodel).collect(Collectors.toList());

		Environment aasEnvironment = new DefaultEnvironment();
		aasEnvironment.setAssetAdministrationShells(cloneCreator.cloneAssetAdministrationShells(shells));
		aasEnvironment.setSubmodels(cloneCreator.cloneSubmodels(submodels));

		if (includeConceptDescriptions) {
			includeConceptDescriptions(aasEnvironment);
		}

		return aasEnvironment;
	}

	private void includeConceptDescriptions(Environment aasEnvironment) {
		List<ConceptDescription> conceptDescriptions = cloneCreator.cloneConceptDescriptions(getConceptDescriptions(aasEnvironment));
		aasEnvironment.setConceptDescriptions(conceptDescriptions);
	}

	private List<ConceptDescription> getConceptDescriptions(Environment env) {
		if (conceptDescriptionRepository == null) {
			throw new NullPointerException("The parameter includeConceptDescriptions is set to true but ConceptDescriptionRepository is null");
		}

		Set<String> cdIds = new ConceptDescriptionIdCollector(env).collect();

		return cdIds.stream().map(this::fetchConceptDescriptionFromRepo).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private ConceptDescription fetchConceptDescriptionFromRepo(String conceptDescriptionId) {
		try {
			return conceptDescriptionRepository.getConceptDescription(conceptDescriptionId);
		} catch (ElementDoesNotExistException e) {
			logger.error("Concept description with id {} could not be found in the repository", conceptDescriptionId);
			return null;
		}
	}

}
