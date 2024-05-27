package org.eclipse.digitaltwin.basyx.client.internal.authorization.credential;

public class ClientCredential {
	
	private String clientId;
	private String clientSecret;
	
	public ClientCredential(String clientId) {
		super();
		this.clientId = clientId;
	}
	
	public ClientCredential(String clientId, String clientSecret) {
		this(clientId);
		this.clientSecret = clientSecret;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

}
