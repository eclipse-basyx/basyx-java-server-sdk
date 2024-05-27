package org.eclipse.digitaltwin.basyx.client.internal.authorization.grant;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;

public interface Grant {
	
	AccessTokenResponse getAccessTokenResponse(String tokenEndpoint);
	
	AccessTokenResponse getAccessTokenResponse(String tokenEndpoint, String refreshToken);

}
