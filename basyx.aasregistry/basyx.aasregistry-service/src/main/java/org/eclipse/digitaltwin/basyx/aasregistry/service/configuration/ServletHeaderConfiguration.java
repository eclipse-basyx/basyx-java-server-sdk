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

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

@Configuration
@lombok.Data
@ConfigurationProperties(prefix = "servlet")
public class ServletHeaderConfiguration {

	private List<HeaderDefinition> headers;

	@Bean
	public Filter headerFilter() {
		return new MappingsHeaderApplier(headers);
	}

	public static final class MappingsHeaderApplier implements Filter {

		private AntPathMatcher matcher = new AntPathMatcher();

		private final List<HeaderDefinition> headerDefs;

		private final Map<CacheKey, Map<String, String>> resolvedHeadersByPath = new ConcurrentHashMap<>();

		public MappingsHeaderApplier(List<HeaderDefinition> headerDefs) {
			if (headerDefs == null) {
				headerDefs = List.of();
			}
			this.headerDefs = headerDefs;
		}

		@Override
		public void init(FilterConfig filterConfig) throws ServletException {
			for (HeaderDefinition eachDef : headerDefs) {
				prepareHeaderDefinition(eachDef);
			}
		}

		private void prepareHeaderDefinition(HeaderDefinition eachDef) {
			String pattern = eachDef.getPath();
			pattern = removeTrailingSlash(pattern);
			eachDef.setPath(pattern);
			if (eachDef.getMethods() != null) {
				Arrays.sort(eachDef.getMethods());
			}
		}

		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			String path = httpRequest.getServletPath();
			String method = httpRequest.getMethod();
			CacheKey cacheKey = new CacheKey(path, method);
			Map<String, String> resolved = resolvedHeadersByPath.computeIfAbsent(cacheKey, key -> resolveHeaders(path, method));
			resolved.forEach(httpResponse::addHeader);
			chain.doFilter(request, httpResponse);
		}

		private Map<String, String> resolveHeaders(String path, String method) {
			path = removeTrailingSlash(path);
			Map<String, String> headers = new HashMap<>();
			for (HeaderDefinition eachDef : headerDefs) {
				String[] methods = eachDef.getMethods();
				if (areMethodMatching(methods, method)) {
					applyHeadersIfMatching(eachDef, path, headers);
				}
			}
			return headers;
		}

		private boolean areMethodMatching(String[] methods, String method) {
			boolean applyForAllMethods = methods == null || methods.length == 0;
			return applyForAllMethods || Arrays.binarySearch(methods, method) >= 0;
		}

		private void applyHeadersIfMatching(HeaderDefinition eachDef, String path, Map<String, String> headers) {
			String pattern = eachDef.getPath();
			if (matcher.match(pattern, path)) {
				// next defs will override previous definitions if multiple match
				headers.putAll(eachDef.getValues());
			}
		}

		private String removeTrailingSlash(String path) {
			if (path.endsWith("/")) {
				return path.substring(0, path.length() - 1);
			}
			return path;
		}
	}

	@Data
	private static final class CacheKey {

		private final String path;

		private final String method;

	}

	@Data
	public static final class HeaderDefinition {

		private String path;

		private Map<String, String> values;

		private String[] methods;

	}
}
