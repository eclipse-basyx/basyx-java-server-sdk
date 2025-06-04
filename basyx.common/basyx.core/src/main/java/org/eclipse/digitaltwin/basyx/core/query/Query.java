
package org.eclipse.digitaltwin.basyx.core.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "$select",
    "$condition"
})
public class Query {

    @JsonProperty("$select")
    private String $select;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("$condition")
    private LogicalExpression $condition;

    @JsonProperty("$select")
    public String get$select() {
        return $select;
    }

    @JsonProperty("$select")
    public void set$select(String $select) {
        this.$select = $select;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("$condition")
    public LogicalExpression get$condition() {
        return $condition;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("$condition")
    public void set$condition(LogicalExpression $condition) {
        this.$condition = $condition;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Query.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("$select");
        sb.append('=');
        sb.append(((this.$select == null)?"<null>":this.$select));
        sb.append(',');
        sb.append("$condition");
        sb.append('=');
        sb.append(((this.$condition == null)?"<null>":this.$condition));
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
        result = ((result* 31)+((this.$condition == null)? 0 :this.$condition.hashCode()));
        result = ((result* 31)+((this.$select == null)? 0 :this.$select.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Query) == false) {
            return false;
        }
        Query rhs = ((Query) other);
        return (((this.$condition == rhs.$condition)||((this.$condition!= null)&&this.$condition.equals(rhs.$condition)))&&((this.$select == rhs.$select)||((this.$select!= null)&&this.$select.equals(rhs.$select))));
    }

}
