/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.submodelrepository.client;

import java.io.IOException;
import java.net.http.HttpRequest;

import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.submodelrepository.client.internal.SubmodelRepositoryApi;
import org.eclipse.digitaltwin.basyx.submodelservice.client.AuthorizedConnectedSubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;

/**
 * Provides access to an Authorized Submodel Repository on a remote server
 * 
 * @author danish
 */
public class AuthorizedConnectedSubmodelRepository extends ConnectedSubmodelRepository {
	
	private TokenManager tokenManager;

	public AuthorizedConnectedSubmodelRepository(String repoUrl, TokenManager tokenManager) {
		super(repoUrl, new SubmodelRepositoryApi(repoUrl, getRequestBuilder(tokenManager)));
		this.tokenManager = tokenManager;
	}
	
	public TokenManager getTokenManager() {
		return tokenManager;
	}
	
	@Override
	public ConnectedSubmodelService getConnectedSubmodelService(String submodelId) {
		try {
			getSubmodel(submodelId);
			return new AuthorizedConnectedSubmodelService(getSubmodelUrl(submodelId), tokenManager);
		} catch (ApiException e) {
			throw mapExceptionSubmodelAccess(getSubmodelUrl(submodelId), e);
		}
	}
	
	private static HttpRequest.Builder getRequestBuilder(TokenManager tokenManager) {
		try {
			return HttpRequest.newBuilder()
			        .header("Authorization", "Bearer " + tokenManager.getAccessToken());
		} catch (IOException e) {
			throw new RuntimeException("Unable to request access token");
		}
	}

}
