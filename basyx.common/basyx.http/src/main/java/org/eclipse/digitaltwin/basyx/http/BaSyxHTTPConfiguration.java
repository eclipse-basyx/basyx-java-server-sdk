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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ClassUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configuration class providing all relevant beans for HTTP payload
 * (de-)serialization
 * 
 * @author schnicke
 *
 */
@Configuration
public class BaSyxHTTPConfiguration {
	private static final String SPRINGDOC_QUERYDSL_CUSTOMIZER_BEAN_NAME = "queryDslQuerydslPredicateOperationCustomizer";

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

	@Bean
	@ConditionalOnMissingBean(ObjectMapper.class)
	public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
		return builder.build();
	}

	@Bean
	@ConditionalOnMissingBean(MappingJackson2HttpMessageConverter.class)
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
		return new MappingJackson2HttpMessageConverter(objectMapper);
	}

	@Bean
	public WebMvcConfigurer resourceHttpMessageConverterConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
				moveResourceHttpMessageConverterToFront(converters);
			}
		};
	}

	private static void moveResourceHttpMessageConverterToFront(List<HttpMessageConverter<?>> converters) {
		for (int i = 0; i < converters.size(); i++) {
			if (converters.get(i) instanceof ResourceHttpMessageConverter) {
				converters.add(0, converters.remove(i));
				return;
			}
		}
	}

	@Bean
	public static BeanDefinitionRegistryPostProcessor springdocQuerydslCompatibilityPostProcessor() {
		return new BeanDefinitionRegistryPostProcessor() {
			@Override
			public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
				if (isLegacySpringDataTypeInformationMissing() && registry.containsBeanDefinition(SPRINGDOC_QUERYDSL_CUSTOMIZER_BEAN_NAME)) {
					registry.removeBeanDefinition(SPRINGDOC_QUERYDSL_CUSTOMIZER_BEAN_NAME);
				}
			}

			@Override
			public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
			}
		};
	}

	private static boolean isLegacySpringDataTypeInformationMissing() {
		return !ClassUtils.isPresent("org.springframework.data.util.TypeInformation", BaSyxHTTPConfiguration.class.getClassLoader());
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

	/**
	 * Creates a {@link CorsConfigurationSource} that can be used by Spring Security
	 * to apply CORS headers even on error responses (e.g., 401, 403).
	 * This ensures consistent CORS behavior between Spring MVC and Spring Security.
	 *
	 * @param configurationUrlProviders
	 * @param allowedOrigins
	 * @param allowedMethods
	 * @return
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource(List<CorsPathPatternProvider> configurationUrlProviders,
															 @Value("${basyx.cors.allowed-origins:}") String[] allowedOrigins,
															 @Value("${basyx.cors.allowed-methods:}") String[] allowedMethods) {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		if (allowedOrigins.length == 0 && allowedMethods.length == 0) {
			return source;
		}

		CorsConfiguration configuration = new CorsConfiguration();

		if (allowedOrigins.length > 0) {
			configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
		}

		if (allowedMethods.length > 0) {
			configuration.setAllowedMethods(Arrays.asList(allowedMethods));
		}

		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		for (CorsPathPatternProvider provider : configurationUrlProviders) {
			logger.info("Registering CORS configuration for path pattern: " + provider.getPathPattern());
			source.registerCorsConfiguration(provider.getPathPattern(), configuration);
		}

		return source;
	}
}
