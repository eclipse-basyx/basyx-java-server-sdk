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

package org.eclipse.digitaltwin.basyx.digitaltwinregistry.component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.LocationBuilder;
import org.eclipse.digitaltwin.basyx.aasregistry.service.configuration.RestConfiguration.StringToEnumConverter;
import org.eclipse.digitaltwin.basyx.http.Aas4JHTTPSerializationExtension;
import org.eclipse.digitaltwin.basyx.http.CorsPathPatternProvider;
import org.eclipse.digitaltwin.basyx.http.SerializationExtension;
import org.reflections.Reflections;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Configuration for aas environment for dependency injection
 * 
 * @author zhangzai, mateusmolina
 *
 */
@Configuration
public class AasEnvironmentConfiguration {

//	@Bean
//	public BeanFactoryPostProcessor dynamicRegistrar() {
//		return beanFactory -> {
//			Reflections reflections = new Reflections("org.eclipse.digitaltwin.basyx");
//			Set<Class<?>> component = reflections.getTypesAnnotatedWith(Component.class);
//			Set<Class<?>> service = reflections.getTypesAnnotatedWith(Service.class);
//			Set<Class<?>> repo = reflections.getTypesAnnotatedWith(Repository.class);
//			Set<Class<?>> controller = reflections.getTypesAnnotatedWith(Controller.class);
//			Set<Class<?>> restController = reflections.getTypesAnnotatedWith(RestController.class);
//			Set<Class<?>> controllerAdvice = reflections.getTypesAnnotatedWith(ControllerAdvice.class);
//			Set<Class<?>> config = reflections.getTypesAnnotatedWith(Configuration.class);
//
//			List<Set<Class<?>>> list = Arrays.asList(component, service, repo, controller, restController, controllerAdvice, config);
//
//			System.out.println("All classes");
//			for (Set<Class<?>> set : list) {
//				for (Class<?> clazz : set) {
////					beanFactory.registerSingleton(clazz.getSimpleName(), BeanUtils.instantiateClass(clazz));
//					System.out.println(clazz.getName());
//				}
//			}
//		};
//	}

	@Bean
	public LocationBuilder locationBuilder() {
		return new DefaultLocationBuilder();
	}

	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
		Jackson2ObjectMapperBuilder builder = jackson2ObjectMapperBuilder.serializationInclusion(JsonInclude.Include.NON_NULL);
		builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return new MappingJackson2HttpMessageConverter(builder.build());
	}

	@Bean
	public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder(List<SerializationExtension> serializationExtensions) {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder().serializationInclusion(JsonInclude.Include.NON_NULL);

		for (SerializationExtension serializationExtension : serializationExtensions) {
			serializationExtension.extend(builder);
		}

		return builder;
	}

	@Bean
	public CorsPathPatternProvider getAasRegistryServiceCorsUrlProvider() {
		return new CorsPathPatternProvider("/shell-descriptors/**");
	}

	@Bean
	public SerializationExtension getExtension() {
		return new Aas4JHTTPSerializationExtension();
	}

	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new StringToEnumConverter());
	}

	public static class StringToEnumConverter implements Converter<String, AssetKind> {
		@Override
		public AssetKind convert(String source) {
			return AssetKind.fromValue(source);
		}
	}

	// @Bean
	// @Primary
	// public static RegistryEventSink getAasEnvironment() {
	// return new RegistryEventLogSink();
	// }
	//
	// @Bean
	// @Primary
	// public static DescriptionApiDelegate getDescriptionApiDelegate() {
	// return new BasyxDescriptionApiDelegate();
	// }
	//
	// @Bean
	// @Primary
	// public static SearchApiDelegate getSearchApiDelegate(AasRegistryStorage
	// storage, RegistryEventSink eventSink) {
	// return new BasyxSearchApiDelegate(storage, eventSink);
	// }
	//
	// @Bean
	//// @Primary
	// @ConditionalOnMissingBean
	// @ConditionalOnProperty()
	// public static AasRegistryStorage getAasRegistryStorage() {
	// return new InMemoryAasRegistryStorage();
	// }
	//
	// @Bean
	// @Primary
	// public static LocationBuilder getLocationBuilder() {
	// return new DefaultLocationBuilder();
	// }
	//
	// @Bean
	// @Primary
	// public static ShellDescriptorsApiDelegate
	// getShellDescriptorsApiDelegate(AasRegistryStorage storage, RegistryEventSink
	// eventSink, LocationBuilder builder) {
	// return new BasyxRegistryApiDelegate(storage, eventSink, builder);
	// }
	//
	// @Bean
	// @Primary
	// public static ShellDescriptorsApi
	// getShellDescriptorsApi(ShellDescriptorsApiDelegate
	// shellDescriptorsApiDelegate) {
	// return new ShellDescriptorsApiController(shellDescriptorsApiDelegate);
	// }
	//
	// @Bean
	// @Primary
	// public static LookupApiController getLookupApiController(AasDiscoveryService
	// aasDiscoveryService, ObjectMapper mapper) {
	// return new LookupApiController(aasDiscoveryService, mapper);
	// }
	//
	// @Bean
	//// @Primary
	// @ConditionalOnMissingBean
	// public static AasDiscoveryService getAasDiscoveryService() {
	// return new CrudAasDiscovery(new AasDiscoveryInMemoryBackendProvider());
	// }

}
