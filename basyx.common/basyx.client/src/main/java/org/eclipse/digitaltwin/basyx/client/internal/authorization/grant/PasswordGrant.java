package org.eclipse.digitaltwin.basyx.client.internal.authorization.grant;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.eclipse.digitaltwin.basyx.client.internal.authorization.credential.ClientCredential;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.credential.PasswordCredential;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.ResourceOwnerPasswordCredentialsGrant;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.RefreshTokenGrant;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;

public class PasswordGrant implements Grant {
	
	private final PasswordCredential passwordCredential;
	private final ClientCredential clientCredential;
	private Collection<String> scopes;
	
	public PasswordGrant(PasswordCredential passwordCredential, ClientCredential clientCredential) {
		this.passwordCredential = passwordCredential;
		this.clientCredential = clientCredential;
	}

	public PasswordGrant(PasswordCredential passwordCredential, ClientCredential clientCredential, Collection<String> scopes) {
		this(passwordCredential, clientCredential);
		this.scopes = scopes;
	}

	@Override
	public AccessTokenResponse getAccessTokenResponse(String tokenEndpoint) {

		AuthorizationGrant passwordGrant = new ResourceOwnerPasswordCredentialsGrant(passwordCredential.getUsername(), new Secret(passwordCredential.getPassword()));

		ClientAuthentication clientAuth = new ClientSecretBasic(new ClientID(clientCredential.getClientId()), new Secret(clientCredential.getClientSecret()));
		
		URI tokenEndpointUri;
		try {
			tokenEndpointUri = new URI(tokenEndpoint);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Error occurred while retrieving access token" + e.getMessage());
		}

		TokenRequest request = new TokenRequest(tokenEndpointUri, clientAuth, passwordGrant, Scope.parse(scopes));

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

}