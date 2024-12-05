
package org.eclipse.digitaltwin.basyx.authorization.abac;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "fields",
    "distinct"
})
@Generated("jsonschema2pojo")
public class SelectStatement {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("fields")
    private List<String> fields = new ArrayList<String>();
    @JsonProperty("distinct")
    private Boolean distinct;

    /**
     * No args constructor for use in serialization
     * 
     */
    public SelectStatement() {
    }

    /**
     * 
     * @param distinct
     * @param fields
     */
    public SelectStatement(List<String> fields, Boolean distinct) {
        super();
        this.fields = fields;
        this.distinct = distinct;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("fields")
    public List<String> getFields() {
        return fields;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("fields")
    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    @JsonProperty("distinct")
    public Boolean getDistinct() {
        return distinct;
    }

    @JsonProperty("distinct")
    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SelectStatement.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("fields");
        sb.append('=');
        sb.append(((this.fields == null)?"<null>":this.fields));
        sb.append(',');
        sb.append("distinct");
        sb.append('=');
        sb.append(((this.distinct == null)?"<null>":this.distinct));
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
        result = ((result* 31)+((this.fields == null)? 0 :this.fields.hashCode()));
        result = ((result* 31)+((this.distinct == null)? 0 :this.distinct.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SelectStatement) == false) {
            return false;
        }
        SelectStatement rhs = ((SelectStatement) other);
        return (((this.fields == rhs.fields)||((this.fields!= null)&&this.fields.equals(rhs.fields)))&&((this.distinct == rhs.distinct)||((this.distinct!= null)&&this.distinct.equals(rhs.distinct))));
    }

}
