/*******************************************************************************
 * Copyright (C) 2026 the Eclipse BaSyx Authors
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

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.StreamSupport;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class TestBaSyxHTTPConfiguration {

	@Test
	public void byteArrayConverterIsUsedForJsonResponses() {
		HttpMessageConverters.ServerBuilder builder = HttpMessageConverters.forServer()
				.registerDefaults()
				.withJsonConverter(new JacksonJsonHttpMessageConverter());

		WebMvcConfigurer configurer = new BaSyxHTTPConfiguration().resourceHttpMessageConverterConfigurer();
		configurer.configureMessageConverters(builder);

		List<HttpMessageConverter<?>> converters = StreamSupport.stream(builder.build().spliterator(), false).toList();

		HttpMessageConverter<?> selectedConverter = converters.stream()
				.filter(converter -> converter.canWrite(byte[].class, MediaType.APPLICATION_JSON))
				.findFirst()
				.orElseThrow();

		assertTrue(selectedConverter instanceof ByteArrayHttpMessageConverter);
	}
}
