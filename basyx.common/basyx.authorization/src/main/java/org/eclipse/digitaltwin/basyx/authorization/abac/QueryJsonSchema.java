
package org.eclipse.digitaltwin.basyx.authorization.abac;

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
    "select",
    "filter",
    "option"
})
@Generated("jsonschema2pojo")
public class QueryJsonSchema {

    @JsonProperty("select")
    private String select;
    @JsonProperty("filter")
    private LogicalExpression filter;
    @JsonProperty("option")
    private OptionStatement option;

    /**
     * No args constructor for use in serialization
     * 
     */
    public QueryJsonSchema() {
    }

    /**
     * 
     * @param filter
     * @param select
     * @param option
     */
    public QueryJsonSchema(String select, LogicalExpression filter, OptionStatement option) {
        super();
        this.select = select;
        this.filter = filter;
        this.option = option;
    }

    @JsonProperty("select")
    public String getSelect() {
        return select;
    }

    @JsonProperty("select")
    public void setSelect(String select) {
        this.select = select;
    }

    @JsonProperty("filter")
    public LogicalExpression getFilter() {
        return filter;
    }

    @JsonProperty("filter")
    public void setFilter(LogicalExpression filter) {
        this.filter = filter;
    }

    @JsonProperty("option")
    public OptionStatement getOption() {
        return option;
    }

    @JsonProperty("option")
    public void setOption(OptionStatement option) {
        this.option = option;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(QueryJsonSchema.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("select");
        sb.append('=');
        sb.append(((this.select == null)?"<null>":this.select));
        sb.append(',');
        sb.append("filter");
        sb.append('=');
        sb.append(((this.filter == null)?"<null>":this.filter));
        sb.append(',');
        sb.append("option");
        sb.append('=');
        sb.append(((this.option == null)?"<null>":this.option));
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
        result = ((result* 31)+((this.select == null)? 0 :this.select.hashCode()));
        result = ((result* 31)+((this.option == null)? 0 :this.option.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof QueryJsonSchema) == false) {
            return false;
        }
        QueryJsonSchema rhs = ((QueryJsonSchema) other);
        return ((((this.filter == rhs.filter)||((this.filter!= null)&&this.filter.equals(rhs.filter)))&&((this.select == rhs.select)||((this.select!= null)&&this.select.equals(rhs.select))))&&((this.option == rhs.option)||((this.option!= null)&&this.option.equals(rhs.option))));
    }

}
