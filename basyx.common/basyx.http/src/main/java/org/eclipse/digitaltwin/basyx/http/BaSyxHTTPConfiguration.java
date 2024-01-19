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


package org.eclipse.digitaltwin.basyx.http;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Configuration class providing all relevant beans for HTTP payload
 * (de-)serialization
 * 
 * @author schnicke
 *
 */
@Configuration
public class BaSyxHTTPConfiguration {
	Logger logger = LoggerFactory.getLogger(BaSyxHTTPConfiguration.class);

	/**
	 * Returns a Jackson2ObjectMapperBuilder that is configured using the passed
	 * list of {@link SerializationExtension}
	 * 
	 * @param serializationExtensions
	 * @return
	 */
	@Bean
	public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder(List<SerializationExtension> serializationExtensions) {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder().serializationInclusion(JsonInclude.Include.NON_NULL);

		for (SerializationExtension serializationExtension : serializationExtensions) {
			serializationExtension.extend(builder);
		}
		
		return builder;
	}

	/**
	 * Collects a list of {@link CorsPathPatternProvider} and uses them to configure
	 * CORS for the passed pathPatterns
	 * 
	 * @param configurationUrlProviders
	 * @param allowedOrigins
	 * @param allowedMethods
	 * @return
	 */
	@Bean
	public WebMvcConfigurer corsConfigurer(List<CorsPathPatternProvider> configurationUrlProviders,
										   @Value("${basyx.cors.allowed-origins:}") String[] allowedOrigins,
										   @Value("${basyx.cors.allowed-methods:}") String[] allowedMethods) {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				if (allowedOrigins.length == 0 && allowedMethods.length == 0)
					return;

				logger.info("---- Configuring CORS ----");

				for (CorsPathPatternProvider provider : configurationUrlProviders) {
					configureOrigins(allowedOrigins, allowedMethods, registry, provider.getPathPattern());
				}
			}

			private void configureOrigins(String[] allowedOrigins, String[] allowedMethods, CorsRegistry registry, String pathPattern) {
				logger.info(pathPattern + " configured with allowedOriginPatterns " + Arrays.toString(allowedOrigins));
				logger.info(allowedMethods.length == 0 ? "No allowed methods configured" : pathPattern + " configured with allowedMethods " + Arrays.toString(allowedMethods));

				registry.addMapping(pathPattern).allowedOriginPatterns(allowedOrigins).allowedMethods(allowedMethods);
			}
		};
	}
}
