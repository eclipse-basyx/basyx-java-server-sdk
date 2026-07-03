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

package org.eclipse.digitaltwin.basyx.submodelservice.feature.authorization;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.basyx.authorization.rbac.Action;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.core.exceptions.InsufficientPermissionException;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class AuthorizedSubmodelServiceFileStreamTest {

	private static final String SUBMODEL_ID = "smId";
	private static final String FILE_ID_SHORT_PATH = "document";
	private static final String REPOSITORY_FILE_ID = "file-repository-id";
	private static final byte[] FILE_CONTENT = "content".getBytes();

	@Test
	void getFileByPathAsStreamAuthorizesByFileIdShortPath() throws Exception {
		SubmodelService decorated = mock(SubmodelService.class);
		when(decorated.getFileByPathAsStream(FILE_ID_SHORT_PATH)).thenReturn(new ByteArrayInputStream(FILE_CONTENT));
		RbacPermissionResolver<SubmodelTargetInformation> permissionResolver = permissionResolverAllowing(FILE_ID_SHORT_PATH);

		AuthorizedSubmodelService service = new AuthorizedSubmodelService(decorated, permissionResolver, SUBMODEL_ID);

		try (InputStream result = service.getFileByPathAsStream(FILE_ID_SHORT_PATH)) {
			assertArrayEquals(FILE_CONTENT, result.readAllBytes());
		}

		ArgumentCaptor<SubmodelTargetInformation> targetInformation = ArgumentCaptor.forClass(SubmodelTargetInformation.class);
		verify(permissionResolver).hasPermission(eq(Action.READ), targetInformation.capture());
		assertEquals(List.of(SUBMODEL_ID), targetInformation.getValue().getSubmodelIds());
		assertEquals(List.of(FILE_ID_SHORT_PATH), targetInformation.getValue().getSubmodelElementIdShortPaths());
		verify(decorated).getFileByPathAsStream(FILE_ID_SHORT_PATH);
		verify(decorated, never()).getFileByFilePath(anyString());
	}

	@Test
	void getFileByPathAsStreamDoesNotAuthorizeByRepositoryFileId() throws Exception {
		SubmodelService decorated = mock(SubmodelService.class);
		RbacPermissionResolver<SubmodelTargetInformation> permissionResolver = permissionResolverAllowing(REPOSITORY_FILE_ID);

		AuthorizedSubmodelService service = new AuthorizedSubmodelService(decorated, permissionResolver, SUBMODEL_ID);

		assertThrows(InsufficientPermissionException.class, () -> service.getFileByPathAsStream(FILE_ID_SHORT_PATH));
		verify(decorated, never()).getFileByPathAsStream(anyString());
		verify(decorated, never()).getFileByFilePath(anyString());
	}

	private static RbacPermissionResolver<SubmodelTargetInformation> permissionResolverAllowing(String allowedIdShortPath) {
		@SuppressWarnings("unchecked")
		RbacPermissionResolver<SubmodelTargetInformation> permissionResolver = mock(RbacPermissionResolver.class);
		when(permissionResolver.hasPermission(eq(Action.READ), any(SubmodelTargetInformation.class))).thenAnswer(invocation -> {
			SubmodelTargetInformation targetInformation = invocation.getArgument(1);
			return List.of(SUBMODEL_ID).equals(targetInformation.getSubmodelIds()) && List.of(allowedIdShortPath).equals(targetInformation.getSubmodelElementIdShortPaths());
		});
		return permissionResolver;
	}

}
