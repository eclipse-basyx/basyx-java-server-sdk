
package org.eclipse.digitaltwin.basyx.authorization.abac;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "queryParameter"
})
@Generated("jsonschema2pojo")
public class QueriesJsonSchema {
	
	@JsonProperty("RIGHTS")
    private List<RightsEnum> rights = new ArrayList<RightsEnum>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("queryParameter")
    private LogicalExpression__1 queryParameter;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("queryParameter")
    public LogicalExpression__1 getQueryParameter() {
        return queryParameter;
    }
    
    @JsonProperty("RIGHTS")
    public List<RightsEnum> getRights() {
        return rights;
    }
    
    @JsonProperty("RIGHTS")
    public void setRights(List<RightsEnum> rights) {
        this.rights = rights;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("queryParameter")
    public void setQueryParameter(LogicalExpression__1 queryParameter) {
        this.queryParameter = queryParameter;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(QueriesJsonSchema.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("queryParameter");
        sb.append('=');
        sb.append(((this.queryParameter == null)?"<null>":this.queryParameter));
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
        result = ((result* 31)+((this.queryParameter == null)? 0 :this.queryParameter.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof QueriesJsonSchema) == false) {
            return false;
        }
        QueriesJsonSchema rhs = ((QueriesJsonSchema) other);
        return ((this.queryParameter == rhs.queryParameter)||((this.queryParameter!= null)&&this.queryParameter.equals(rhs.queryParameter)));
    }

}
