package org.eclipse.digitaltwin.basyx.client.internal.authorization.grant;

public enum GrantType {
    CLIENT_CREDENTIALS("client_credentials"),
    PASSWORD("password");

    private final String grantType;

    GrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getGrantType() {
        return this.grantType;
    }
}