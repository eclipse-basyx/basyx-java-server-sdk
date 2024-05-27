package org.eclipse.digitaltwin.basyx.client.internal.authorization.credential;

public class PasswordCredential {
	
	private String username;
	private String password;
	
	public PasswordCredential(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
