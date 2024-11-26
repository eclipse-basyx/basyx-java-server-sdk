package org.eclipse.digitaltwin.basyx.digitaltwinregistry.testcases.helper;

public class Tenant {
	
	String publicClientId;
    String tenantId;
    
	public Tenant(String publicClientId, String tenantId) {
		super();
		this.publicClientId = publicClientId;
		this.tenantId = tenantId;
	}
	
	public String getPublicClientId() {
		return publicClientId;
	}
	public void setPublicClientId(String publicClientId) {
		this.publicClientId = publicClientId;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

}
