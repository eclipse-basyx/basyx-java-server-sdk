
package org.eclipse.digitaltwin.basyx.authorization.abac;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Common JSON Schema for AAS Queries and Access Rules
 * <p>
 * This schema contains all classes that are shared between the aAS Query Language and the AAS Access Rule Language.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "filter",
    "AllAccessPermissionRules"
})
@Generated("jsonschema2pojo")
public class Schema {

    @JsonProperty("filter")
    private LogicalExpression filter;
    @JsonProperty("AllAccessPermissionRules")
    private List<AllAccessPermissionRule> allAccessPermissionRules = new ArrayList<AllAccessPermissionRule>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Schema() {
    }

    /**
     * 
     * @param filter
     * @param allAccessPermissionRules
     */
    public Schema(LogicalExpression filter, List<AllAccessPermissionRule> allAccessPermissionRules) {
        super();
        this.filter = filter;
        this.allAccessPermissionRules = allAccessPermissionRules;
    }

    @JsonProperty("filter")
    public LogicalExpression getFilter() {
        return filter;
    }

    @JsonProperty("filter")
    public void setFilter(LogicalExpression filter) {
        this.filter = filter;
    }

    @JsonProperty("AllAccessPermissionRules")
    public List<AllAccessPermissionRule> getAllAccessPermissionRules() {
        return allAccessPermissionRules;
    }

    @JsonProperty("AllAccessPermissionRules")
    public void setAllAccessPermissionRules(List<AllAccessPermissionRule> allAccessPermissionRules) {
        this.allAccessPermissionRules = allAccessPermissionRules;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Schema.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("filter");
        sb.append('=');
        sb.append(((this.filter == null)?"<null>":this.filter));
        sb.append(',');
        sb.append("allAccessPermissionRules");
        sb.append('=');
        sb.append(((this.allAccessPermissionRules == null)?"<null>":this.allAccessPermissionRules));
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
        result = ((result* 31)+((this.filter == null)? 0 :this.filter.hashCode()));
        result = ((result* 31)+((this.allAccessPermissionRules == null)? 0 :this.allAccessPermissionRules.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Schema) == false) {
            return false;
        }
        Schema rhs = ((Schema) other);
        return (((this.filter == rhs.filter)||((this.filter!= null)&&this.filter.equals(rhs.filter)))&&((this.allAccessPermissionRules == rhs.allAccessPermissionRules)||((this.allAccessPermissionRules!= null)&&this.allAccessPermissionRules.equals(rhs.allAccessPermissionRules))));
    }

}
