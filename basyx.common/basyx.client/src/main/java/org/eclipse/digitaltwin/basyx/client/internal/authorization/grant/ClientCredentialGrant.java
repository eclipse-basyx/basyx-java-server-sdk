package org.eclipse.digitaltwin.basyx.client.internal.authorization.grant;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.eclipse.digitaltwin.basyx.client.internal.authorization.credential.ClientCredential;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ClientCredentialsGrant;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.RefreshTokenGrant;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;

public class ClientCredentialGrant implements Grant {
	
	private final ClientCredential clientCredential;
	private Collection<String> scopes;
	
	public ClientCredentialGrant(ClientCredential clientCredential) {
		this.clientCredential = clientCredential;
	}

	public ClientCredentialGrant(ClientCredential clientCredential, Collection<String> scopes) {
		this(clientCredential);
		this.scopes = scopes;
	}

	@Override
	public AccessTokenResponse getAccessTokenResponse(String tokenEndpoint) {
		
		if (isPublicClient())
			throw new RuntimeException("The client cannnot be public client with Password Grant");

		AuthorizationGrant clientGrant = new ClientCredentialsGrant();

		ClientAuthentication clientAuth = new ClientSecretBasic(new ClientID(clientCredential.getClientId()), new Secret(clientCredential.getClientSecret()));

		URI tokenEndpointUri;
		try {
			tokenEndpointUri = new URI(tokenEndpoint);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Error occurred while retrieving access token" + e.getMessage());
		}
		
		TokenRequest request = new TokenRequest(tokenEndpointUri, clientAuth, clientGrant, Scope.parse(scopes));

		TokenResponse response;
		try {
			response = TokenResponse.parse(request.toHTTPRequest().send());
		} catch (ParseException | IOException e) {
			throw new RuntimeException("Error occurred while retrieving access token");
		}

		if (! response.indicatesSuccess()) {
		    // We got an error response...
		    TokenErrorResponse errorResponse = response.toErrorResponse();
		    System.out.println("#### ERROR WHILE RETRIEVING TOKEN #####" + errorResponse.toString());
		}

		return response.toSuccessResponse();
	}
	
	@Override
	public AccessTokenResponse getAccessTokenResponse(String tokenEndpoint, String refreshToken) {
		
		if (isPublicClient())
			throw new RuntimeException("The client cannnot be public client with Password Grant");
		
		RefreshToken refreshTokenObj = new RefreshToken(refreshToken);
		
		AuthorizationGrant refreshTokenGrant = new RefreshTokenGrant(refreshTokenObj);

		ClientAuthentication clientAuth = new ClientSecretBasic(new ClientID(clientCredential.getClientId()), new Secret(clientCredential.getClientSecret()));
		
		URI tokenEndpointUri;
		try {
			tokenEndpointUri = new URI(tokenEndpoint);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Error occurred while retrieving access token" + e.getMessage());
		}

		TokenRequest request = new TokenRequest(tokenEndpointUri, clientAuth, refreshTokenGrant);

		TokenResponse response;
		try {
			response = TokenResponse.parse(request.toHTTPRequest().send());
		} catch (ParseException | IOException e) {
			throw new RuntimeException("Error occurred while retrieving access token");
		}
		if (! response.indicatesSuccess()) {
		    // We got an error response...
		    TokenErrorResponse errorResponse = response.toErrorResponse();
		    System.out.println("#### ERROR WHILE RETRIEVING TOKEN #####" + errorResponse.toString());
		}

		return response.toSuccessResponse();
	}
	
	private boolean isPublicClient() {
		return clientCredential.getClientSecret().isBlank();
	}

}
