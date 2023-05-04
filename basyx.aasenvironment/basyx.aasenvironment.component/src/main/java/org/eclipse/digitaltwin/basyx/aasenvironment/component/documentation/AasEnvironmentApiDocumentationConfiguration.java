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

package org.eclipse.digitaltwin.basyx.aasenvironment.component.documentation;

import org.eclipse.digitaltwin.basyx.aasenvironment.component.AasEnvironmentComponent;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.http.documentation.RepositoryApiDocumentationConfiguration;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Info;

/**
 * API documentation configuration for {@link AasEnvironmentComponent} i.e.
 * aggregation of API documents from {@link AasRepository},
 * {@link SubmodelRepository} and {@link ConceptDescriptionRepository}
 * 
 * @author danish
 *
 */
@Configuration
public class AasEnvironmentApiDocumentationConfiguration extends RepositoryApiDocumentationConfiguration {

	private static final String TITLE = "BaSyx AAS Environment Component";
	private static final String DESCRIPTION = "AAS Environment API";

	@Override
	protected Info apiInfo() {
		return new Info().title(TITLE).description(DESCRIPTION).version(VERSION).contact(apiContact())
				.license(apiLicence());
	}

}
