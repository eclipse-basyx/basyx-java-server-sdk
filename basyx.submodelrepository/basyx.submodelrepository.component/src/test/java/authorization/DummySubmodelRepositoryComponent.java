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


package authorization;

import org.eclipse.digitaltwin.basyx.submodelrepository.InMemorySubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

//@SpringBootApplication(scanBasePackages = "org.eclipse.digitaltwin.basyx", exclude = {org.eclipse.digitaltwin.basyx.submodelrepository.http.DummySubmodelRepositoryComponent.class})
@SpringBootApplication(scanBasePackages = "org.eclipse.digitaltwin.basyx")
//@SpringBootTest
//@PropertySource("classpath:foo.properties")
//@PropertySource
//@PropertySource(value = "classpath:foo.properties")
//@WebMvcTest
//@TestPropertySource(properties = { "basyx.backend = InMemory" })
//@TestPropertySource(properties = { "basyx.submodelrepository.feature.authorization.enabled = false" })
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration
public class DummySubmodelRepositoryComponent {
	private Environment environment;
	public DummySubmodelRepositoryComponent(Environment environment) {
		System.out.println("hello world");
	}

	@Primary
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		Filter filter = new MockFilter();
		http.addFilterBefore(filter, BearerTokenAuthenticationFilter.class);
		return http.build();
	}

	public static class MockFilter implements Filter {
		@Override
		public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
			if (servletRequest instanceof HttpServletRequest) {
				String testAuthorizationRole = ((HttpServletRequest) servletRequest).getHeader("Test-Authorization-Role");
				if (testAuthorizationRole != null) {
					Authentication authentication = new JwtAuthenticationToken(new Jwt("foo", Instant.now(), Instant.now().plus(Duration.ofHours(2)), Map.of("kid", "bar", "typ", "JWT", "alg", "RS256"), Map.of("roles", testAuthorizationRole)));
					SecurityContext securityContext = new SecurityContextImpl(authentication);
					SecurityContextHolder.setContext(securityContext);
				}
			}
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}
}
