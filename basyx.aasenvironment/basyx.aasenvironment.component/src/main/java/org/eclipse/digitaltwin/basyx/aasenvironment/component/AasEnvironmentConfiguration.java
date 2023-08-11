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
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.DeserializationException;
import org.eclipse.digitaltwin.basyx.aasenvironment.AasEnvironmentSerialization;
import org.eclipse.digitaltwin.basyx.aasenvironment.base.DefaultAASEnvironmentSerialization;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

/**
 * Configuration for aas environment for dependency injection
 * 
 * @author zhangzai
 *
 */
@Configuration
public class AasEnvironmentConfiguration {

	@Autowired
	ResourceLoader resourceLoader;

	@Value("${basyx.environment:#{null}}")
	private List<String> filesToLoad;

	@Bean
	public AasEnvironmentSerialization createAasEnvironmentSerialization(AasRepository aasRepository,
			SubmodelRepository submodelRepository, ConceptDescriptionRepository conceptDescriptionRepository)
			throws IOException, InvalidFormatException, DeserializationException {
		loadEnvironmentIfNeeded(aasRepository, submodelRepository, conceptDescriptionRepository);
		return new DefaultAASEnvironmentSerialization(aasRepository, submodelRepository, conceptDescriptionRepository);
	}

	private void loadEnvironmentIfNeeded(AasRepository aasRepository, SubmodelRepository submodelRepository,
			ConceptDescriptionRepository conceptDescriptionRepository)
			throws IOException, InvalidFormatException, DeserializationException {
		AasEnvironmentPreconfigurationLoader loader = new AasEnvironmentPreconfigurationLoader(resourceLoader,
				filesToLoad);
		if (loader.shouldLoadPreconfiguredEnvironment()) {
			loader.loadPrefconfiguredEnvironment(aasRepository, submodelRepository, conceptDescriptionRepository);
		}
	}

}
