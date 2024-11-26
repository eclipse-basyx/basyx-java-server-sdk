package org.eclipse.digitaltwin.basyx.authorization.abac;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class AllRulesWrapper {

    @JsonProperty("AllRules")
    private List<AllRule> allRules;

    public List<AllRule> getAllRules() {
        return allRules;
    }

    public void setAllRules(List<AllRule> allRules) {
        this.allRules = allRules;
    }
}

