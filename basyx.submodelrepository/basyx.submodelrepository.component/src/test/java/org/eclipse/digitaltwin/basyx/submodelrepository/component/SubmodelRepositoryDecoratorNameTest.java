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
package org.eclipse.digitaltwin.basyx.submodelrepository.component;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.authorization.AuthorizedSubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration.RegistryIntegrationSubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration.SubmodelRepositoryRegistryLink;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.search.SearchSubmodelRepository;
import org.junit.Test;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

public class SubmodelRepositoryDecoratorNameTest {
	private static final String CONFIGURED_REPOSITORY_NAME = "configured-sm-repo";

	@Test
	public void everyDecoratorPreservesConfiguredRepositoryName() {
		SubmodelRepository decorated = mock(SubmodelRepository.class);
		when(decorated.getName()).thenReturn(CONFIGURED_REPOSITORY_NAME);

		assertEquals(CONFIGURED_REPOSITORY_NAME, new AuthorizedSubmodelRepository(decorated, mock(RbacPermissionResolver.class)).getName());
		assertEquals(CONFIGURED_REPOSITORY_NAME,
				new RegistryIntegrationSubmodelRepository(decorated, mock(SubmodelRepositoryRegistryLink.class), mock(AttributeMapper.class)).getName());
		assertEquals(CONFIGURED_REPOSITORY_NAME, new SearchSubmodelRepository(decorated, mock(ElasticsearchClient.class), "index").getName());
	}

	@Test
	public void mixedDecoratorOrderingPreservesConfiguredRepositoryName() {
		SubmodelRepository decorated = mock(SubmodelRepository.class);
		when(decorated.getName()).thenReturn(CONFIGURED_REPOSITORY_NAME);

		SubmodelRepository authorizationOutside = new AuthorizedSubmodelRepository(
				new RegistryIntegrationSubmodelRepository(
						new SearchSubmodelRepository(decorated, mock(ElasticsearchClient.class), "index"),
						mock(SubmodelRepositoryRegistryLink.class), mock(AttributeMapper.class)),
				mock(RbacPermissionResolver.class));
		SubmodelRepository searchOutside = new SearchSubmodelRepository(
				new AuthorizedSubmodelRepository(
						new RegistryIntegrationSubmodelRepository(decorated, mock(SubmodelRepositoryRegistryLink.class), mock(AttributeMapper.class)),
						mock(RbacPermissionResolver.class)),
				mock(ElasticsearchClient.class), "index");

		assertEquals(CONFIGURED_REPOSITORY_NAME, authorizationOutside.getName());
		assertEquals(CONFIGURED_REPOSITORY_NAME, searchOutside.getName());
	}
}
