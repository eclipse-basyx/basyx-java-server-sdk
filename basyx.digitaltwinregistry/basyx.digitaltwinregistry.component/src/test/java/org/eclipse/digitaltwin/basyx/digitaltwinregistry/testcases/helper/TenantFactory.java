package org.eclipse.digitaltwin.basyx.digitaltwinregistry.testcases.helper;

public class TenantFactory {

   private static final String TENANT_ONE = "TENANT_ONE";
   private static final String TENANT_TWO = "TENANT_TWO";
   private static final String TENANT_THREE = "TENANT_THREE";

   private final Tenant tenantOne;
   private final Tenant tenantTwo;
   private final Tenant tenantThree;

   public TenantFactory( String publicClientId ) {
      this.tenantOne = new Tenant( publicClientId, TENANT_ONE );
      this.tenantTwo = new Tenant( publicClientId, TENANT_TWO );
      this.tenantThree = new Tenant( publicClientId, TENANT_THREE );
   }

   public Tenant tenantOne() {
      return tenantOne;
   }

   public Tenant tenantTwo() {
      return tenantTwo;
   }

   public Tenant tenantThree() {
      return tenantThree;
   }

}