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

package org.eclipse.digitaltwin.basyx.http.documentation;

import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * API documentation configuration for repositories
 * 
 * @author danish
 *
 */
public class RepositoryApiDocumentationConfiguration {

	private static final String TITLE = "BaSyx Repository";
	private static final String DESCRIPTION = "BaSyx Components API";
	protected static final String VERSION = "2.0";
	private static final String LICENSE = "MIT Licence";
	private static final String LICENSE_URL = "https://opensource.org/licenses/mit-license.php";
	private static final String CONTACT_URL = "https://www.eclipse.org/basyx/";
	private static final String CONTACT_EMAIL = "basyx-dev@eclipse.org";
	private static final String CONTACT_NAME = "The BaSyx Developers";

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(apiInfo());
	}
	
	@Bean
	public ModelResolver modelResolver(Jackson2ObjectMapperBuilder builder) {
		return new ModelResolver(builder.build());
	}

	protected Info apiInfo() {
		return new Info().title(TITLE).description(DESCRIPTION).version(VERSION).contact(apiContact())
				.license(apiLicence());
	}

	protected License apiLicence() {
		return new License().name(LICENSE).url(LICENSE_URL);
	}

	protected Contact apiContact() {
		return new Contact().name(CONTACT_NAME).email(CONTACT_EMAIL).url(CONTACT_URL);
	}

}
