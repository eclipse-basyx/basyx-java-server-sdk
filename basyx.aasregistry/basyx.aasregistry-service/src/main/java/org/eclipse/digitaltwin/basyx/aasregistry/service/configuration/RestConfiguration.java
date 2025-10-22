/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.aasregistry.service.configuration;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.LocationBuilder;
import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.http.BaSyxHTTPConfiguration;
import org.eclipse.digitaltwin.basyx.http.CorsPathPatternProvider;
import org.eclipse.digitaltwin.basyx.http.SerializationExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class RestConfiguration extends BaSyxHTTPConfiguration implements WebMvcConfigurer {

	@Bean
	public LocationBuilder locationBuilder() {
		return new DefaultLocationBuilder();
	}

	@Bean
	public CorsPathPatternProvider getAasRegistryServiceCorsUrlProvider() {
		return new CorsPathPatternProvider("/shell-descriptors/**");
	}

	@Bean
	public CorsPathPatternProvider getAasRegistryServiceDescriptionCorsUrlProvider() {
		return new CorsPathPatternProvider("/description");
	}

	@Bean
	public CorsPathPatternProvider getAasRegistryServiceQueryCorsUrlProvider() {
		return new CorsPathPatternProvider("/query/**");
	}

	@Bean
	public SerializationExtension getExtension() {
		return new Aas4JHTTPSerializationExtension();
	}
	
	@Bean
	public SerializationExtension getDisableExtension() {
		return builder -> builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new StringToEnumConverter());
	}

	public static class StringToEnumConverter implements Converter<String, AssetKind> {
		@Override
		public AssetKind convert(String source) {
			return AssetKind.fromValue(source);
		}
	}
}