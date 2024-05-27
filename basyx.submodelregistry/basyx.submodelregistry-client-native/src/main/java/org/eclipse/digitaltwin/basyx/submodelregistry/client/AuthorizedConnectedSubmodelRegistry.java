package org.eclipse.digitaltwin.basyx.submodelregistry.client;

import java.io.IOException;
import java.net.http.HttpRequest;

import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;

public class AuthorizedConnectedSubmodelRegistry extends SubmodelRegistryApi {

	public AuthorizedConnectedSubmodelRegistry(String basePath, TokenManager tokenManager) {
		super(basePath, getRequestBuilder(tokenManager));
	}

	private static HttpRequest.Builder getRequestBuilder(TokenManager tokenManager) {
		try {
			return HttpRequest.newBuilder().header("Authorization", "Bearer " + tokenManager.getAccessToken());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to request access token");
		}
	}

}
