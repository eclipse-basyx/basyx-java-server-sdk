package org.eclipse.digitaltwin.basyx.aasregistry.main.client;

import java.io.IOException;
import java.net.http.HttpRequest;

import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;

public class AuthorizedConnectedAasRegistry extends RegistryAndDiscoveryInterfaceApi {

	public AuthorizedConnectedAasRegistry(String basePath, TokenManager tokenManager) {
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
