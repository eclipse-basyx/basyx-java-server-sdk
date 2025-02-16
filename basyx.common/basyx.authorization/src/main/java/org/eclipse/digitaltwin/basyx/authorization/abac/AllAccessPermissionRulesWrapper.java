package org.eclipse.digitaltwin.basyx.authorization.abac;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AllAccessPermissionRulesWrapper {
    @JsonProperty("AllAccessPermissionRules")
    private AllAccessPermissionRules allAccessPermissionRules;

    public AllAccessPermissionRules getAllAccessPermissionRules() {
        return allAccessPermissionRules;
    }

    public void setAllAccessPermissionRules(AllAccessPermissionRules allAccessPermissionRules) {
        this.allAccessPermissionRules = allAccessPermissionRules;
    }
}
