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
package org.eclipse.digitaltwin.basyx.aasregistry.service.tests;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.eclipse.digitaltwin.basyx.aasregistry.service.configuration.ServletHeaderConfiguration.HeaderDefinition;
import org.eclipse.digitaltwin.basyx.aasregistry.service.configuration.ServletHeaderConfiguration.MappingsHeaderApplier;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class FilterTest {

	private final FilterConfig filterConfig = Mockito.mock(FilterConfig.class);
	private final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
	private final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
	private final FilterChain chain = Mockito.mock(FilterChain.class);

	@Test
	public void whenFilterIsApplied_thenOtherFiltersAreAlsoInvoked() throws ServletException, IOException {
		Mockito.when(request.getServletPath()).thenReturn("/registry/shell-descriptors");
		Mockito.when(request.getMethod()).thenReturn("GET");

		MappingsHeaderApplier headers = new MappingsHeaderApplier(List.of());
		headers.init(filterConfig);
		headers.doFilter(request, response, chain);
		Mockito.verify(chain, Mockito.times(1)).doFilter(request, response);
	}

	@Test
	public void whenInitIsCalled_thenHeaderDefsAreAdjusted() throws ServletException, IOException {
		Mockito.when(request.getServletPath()).thenReturn("/registry/shell-descriptors");
		Mockito.when(request.getMethod()).thenReturn("GET");

		HeaderDefinition def1 = newDef1();
		HeaderDefinition def2 = newDef2();
		MappingsHeaderApplier headers = new MappingsHeaderApplier(List.of(def1, def2));
		headers.init(filterConfig);

		assertThat(def1.getPath()).isEqualTo("/registry/shell-descriptors");
		assertThat(def2.getPath()).isEqualTo("/registry/shell-descriptors");
		assertThat(def1.getMethods()).isEqualTo(new String[] { "GET", "OPTIONS" });
		assertThat(def2.getMethods()).isEqualTo(new String[] { "OPTIONS", "POST" });
	}

	@Test
	public void whenFiltering_thenValuesAreApplied() throws ServletException, IOException {
		Mockito.when(request.getServletPath()).thenReturn("/registry/shell-descriptors");
		Mockito.when(request.getMethod()).thenReturn("OPTIONS");

		HeaderDefinition def1 = newDef1();
		HeaderDefinition def2 = newDef2();
		MappingsHeaderApplier headers = new MappingsHeaderApplier(List.of(def1, def2));
		headers.init(filterConfig);
		headers.doFilter(request, response, chain);

		Mockito.verify(response, Mockito.times(1)).addHeader("Header1", "Value1");
		Mockito.verify(response, Mockito.times(1)).addHeader("Header2", "Value2");
		Mockito.verify(response, Mockito.times(2)).addHeader(Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void whenFilteringWithUndefinedHeaders_thenNoAdditionalHeadersAreApplied() throws ServletException, IOException {
		Mockito.when(request.getServletPath()).thenReturn("/registry/shell-descriptors");
		Mockito.when(request.getMethod()).thenReturn("DELETE");

		HeaderDefinition def1 = newDef1();
		HeaderDefinition def2 = newDef2();
		MappingsHeaderApplier headers = new MappingsHeaderApplier(List.of(def1, def2));
		headers.init(filterConfig);
		headers.doFilter(request, response, chain);

		Mockito.verify(response, Mockito.never()).addHeader(Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void whenMethodsNotDefined_thenAllAreMatching() throws IOException, ServletException {
		Mockito.when(request.getServletPath()).thenReturn("/registry/shell-descriptors");
		Mockito.when(request.getMethod()).thenReturn("GET");

		HeaderDefinition def1 = newDef("/registry/shell-descriptors", null, Map.of("Header1", "Value1"));
		HeaderDefinition def2 = newDef("/registry/shell-descriptors", new String[0], Map.of("Header2", "Value2"));
		MappingsHeaderApplier headers = new MappingsHeaderApplier(List.of(def1, def2));
		headers.init(filterConfig);
		headers.doFilter(request, response, chain);

		Mockito.verify(response, Mockito.times(1)).addHeader("Header1", "Value1");
		Mockito.verify(response, Mockito.times(1)).addHeader("Header2", "Value2");
		Mockito.verify(response, Mockito.times(2)).addHeader(Mockito.anyString(), Mockito.anyString());
	}

	private HeaderDefinition newDef2() {
		return newDef("/registry/shell-descriptors", new String[] { "POST", "OPTIONS" }, Map.of("Header2", "Value2"));
	}

	private HeaderDefinition newDef1() {
		return newDef("/registry/shell-descriptors/", new String[] { "OPTIONS", "GET" }, Map.of("Header1", "Value1"));
	}

	private HeaderDefinition newDef(String path, String[] methods, java.util.Map<String, String> headers) {
		HeaderDefinition def = new HeaderDefinition();
		def.setPath(path);
		def.setMethods(methods);
		def.setValues(headers);
		return def;
	}

}
