
package org.eclipse.digitaltwin.basyx.authorization.abac;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * AAS access rules
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "AllRules"
})
@Generated("jsonschema2pojo")
public class AccessRuleJsonSchema {

    @JsonProperty("AllRules")
    private List<AllRule> allRules = new ArrayList<AllRule>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public AccessRuleJsonSchema() {
    }

    /**
     * 
     * @param allRules
     */
    public AccessRuleJsonSchema(List<AllRule> allRules) {
        super();
        this.allRules = allRules;
    }

    @JsonProperty("AllRules")
    public List<AllRule> getAllRules() {
        return allRules;
    }

    @JsonProperty("AllRules")
    public void setAllRules(List<AllRule> allRules) {
        this.allRules = allRules;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AccessRuleJsonSchema.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("allRules");
        sb.append('=');
        sb.append(((this.allRules == null)?"<null>":this.allRules));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.allRules == null)? 0 :this.allRules.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AccessRuleJsonSchema) == false) {
            return false;
        }
        AccessRuleJsonSchema rhs = ((AccessRuleJsonSchema) other);
        return ((this.allRules == rhs.allRules)||((this.allRules!= null)&&this.allRules.equals(rhs.allRules)));
    }

}
