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

package org.eclipse.digitaltwin.basyx.aasrepository.client;

import org.eclipse.digitaltwin.basyx.aasrepository.client.internal.AssetAdministrationShellRepositoryApi;
import org.eclipse.digitaltwin.basyx.aasservice.client.ConnectedAasService;
import org.eclipse.digitaltwin.basyx.aasservice.client.AuthorizedConnectedAasService;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;

/**
 * Provides access to an Authorized Aas Repository on a remote server
 * 
 * @author danish
 */
public class AuthorizedConnectedAasRepository extends ConnectedAasRepository {

	private TokenManager tokenManager;

	public AuthorizedConnectedAasRepository(String repoUrl, TokenManager tokenManager) {
		super(repoUrl, new AssetAdministrationShellRepositoryApi(repoUrl, tokenManager));
		this.tokenManager = tokenManager;
	}
	
	public TokenManager getTokenManager() {
		return tokenManager;
	}

	@Override
	public ConnectedAasService getConnectedAasService(String aasId) {
		try {
			getAas(aasId);
			return new AuthorizedConnectedAasService(getAasUrl(aasId), tokenManager);
		} catch (ApiException e) {
			throw mapExceptionAasAccess(aasId, e);
		}
	}

}
