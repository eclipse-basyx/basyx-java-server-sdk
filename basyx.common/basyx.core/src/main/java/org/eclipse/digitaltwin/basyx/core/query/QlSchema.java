
package org.eclipse.digitaltwin.basyx.core.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Common JSON Schema for AAS Queries and Access Rules
 * <p>
 * This schema contains all classes that are shared between the AAS Query Language and the AAS Access Rule Language.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Query",
    "AllAccessPermissionRules"
})
public class QlSchema {

    @JsonProperty("Query")
    private AASQuery query;
    @JsonProperty("AllAccessPermissionRules")
    private AllAccessPermissionRules allAccessPermissionRules;

    @JsonProperty("Query")
    public AASQuery getQuery() {
        return query;
    }

    @JsonProperty("Query")
    public void setQuery(AASQuery query) {
        this.query = query;
    }

    @JsonProperty("AllAccessPermissionRules")
    public AllAccessPermissionRules getAllAccessPermissionRules() {
        return allAccessPermissionRules;
    }

    @JsonProperty("AllAccessPermissionRules")
    public void setAllAccessPermissionRules(AllAccessPermissionRules allAccessPermissionRules) {
        this.allAccessPermissionRules = allAccessPermissionRules;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(QlSchema.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("query");
        sb.append('=');
        sb.append(((this.query == null)?"<null>":this.query));
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
        result = ((result* 31)+((this.allAccessPermissionRules == null)? 0 :this.allAccessPermissionRules.hashCode()));
        result = ((result* 31)+((this.query == null)? 0 :this.query.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof QlSchema) == false) {
            return false;
        }
        QlSchema rhs = ((QlSchema) other);
        return (((this.allAccessPermissionRules == rhs.allAccessPermissionRules)||((this.allAccessPermissionRules!= null)&&this.allAccessPermissionRules.equals(rhs.allAccessPermissionRules)))&&((this.query == rhs.query)||((this.query!= null)&&this.query.equals(rhs.query))));
    }

}
