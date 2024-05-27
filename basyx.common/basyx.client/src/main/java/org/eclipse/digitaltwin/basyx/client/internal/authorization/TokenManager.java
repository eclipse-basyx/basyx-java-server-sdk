package org.eclipse.digitaltwin.basyx.client.internal.authorization;

import java.io.IOException;

import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.Grant;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;

public class TokenManager {
	
	private String tokenEndpoint;
	private Grant grant;
	private String accessToken;
    private String refreshToken;
    private long accessTokenExpiryTime;
    private long refreshTokenExpiryTime;
	
	public TokenManager(String tokenEndpoint, Grant grant) {
		super();
		this.tokenEndpoint = tokenEndpoint;
		this.grant = grant;
	}

	public String getTokenEndpoint() {
		return tokenEndpoint;
	}

	public Grant getGrant() {
		return this.grant;
	}
	
	public synchronized String getAccessToken() throws IOException {

        if (accessToken != null && System.currentTimeMillis() < accessTokenExpiryTime) {
            return accessToken;
        }

        if (refreshToken != null && System.currentTimeMillis() < refreshTokenExpiryTime) {
            try {
				return requestAccessToken(grant.getAccessTokenResponse(tokenEndpoint, refreshToken));
			} catch (IOException e) {
				throw new RuntimeException("Error occurred while retrieving access token" + e.getMessage());
			}
        }

        try {
			return requestAccessToken(grant.getAccessTokenResponse(tokenEndpoint));
		} catch (IOException e) {
			throw new RuntimeException("Error occurred while retrieving access token" + e.getMessage());
		}
    }
	
	private String requestAccessToken(AccessTokenResponse accessTokenResponse) throws IOException {        
        AccessToken accessTokenObj = accessTokenResponse.getTokens().getAccessToken();
        accessToken = accessTokenObj.getValue();
        accessTokenExpiryTime = accessTokenObj.getLifetime();
        
        RefreshToken refreshTokenObj = accessTokenResponse.getTokens().getRefreshToken();
        
        if (refreshTokenObj != null) {
        	refreshToken = refreshTokenObj.getValue();
            refreshTokenExpiryTime = System.currentTimeMillis() + (30L * 24L * 60L * 60L * 1000L);
        }
        
        return accessToken;
    }
	
}
