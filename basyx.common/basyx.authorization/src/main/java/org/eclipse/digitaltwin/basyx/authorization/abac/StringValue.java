
package org.eclipse.digitaltwin.basyx.authorization.abac;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "$field",
    "$strVal",
    "$strCast",
    "$attribute"
})
@Generated("jsonschema2pojo")
public class StringValue {

    @JsonProperty("$field")
    private String $field;
    @JsonProperty("$strVal")
    private String $strVal;
    @JsonProperty("$strCast")
    private Value $strCast;
    @JsonProperty("$attribute")
    private AttributeItem $attribute;

    /**
     * No args constructor for use in serialization
     * 
     */
    public StringValue() {
    }

    /**
     * 
     * @param $strVal
     * @param $strCast
     * @param $attribute
     * @param $field
     */
    public StringValue(String $field, String $strVal, Value $strCast, AttributeItem $attribute) {
        super();
        this.$field = $field;
        this.$strVal = $strVal;
        this.$strCast = $strCast;
        this.$attribute = $attribute;
    }

    @JsonProperty("$field")
    public String get$field() {
        return $field;
    }

    @JsonProperty("$field")
    public void set$field(String $field) {
        this.$field = $field;
    }

    @JsonProperty("$strVal")
    public String get$strVal() {
        return $strVal;
    }

    @JsonProperty("$strVal")
    public void set$strVal(String $strVal) {
        this.$strVal = $strVal;
    }

    @JsonProperty("$strCast")
    public Value get$strCast() {
        return $strCast;
    }

    @JsonProperty("$strCast")
    public void set$strCast(Value $strCast) {
        this.$strCast = $strCast;
    }

    @JsonProperty("$attribute")
    public AttributeItem get$attribute() {
        return $attribute;
    }

    @JsonProperty("$attribute")
    public void set$attribute(AttributeItem $attribute) {
        this.$attribute = $attribute;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(StringValue.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("$field");
        sb.append('=');
        sb.append(((this.$field == null)?"<null>":this.$field));
        sb.append(',');
        sb.append("$strVal");
        sb.append('=');
        sb.append(((this.$strVal == null)?"<null>":this.$strVal));
        sb.append(',');
        sb.append("$strCast");
        sb.append('=');
        sb.append(((this.$strCast == null)?"<null>":this.$strCast));
        sb.append(',');
        sb.append("$attribute");
        sb.append('=');
        sb.append(((this.$attribute == null)?"<null>":this.$attribute));
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
        result = ((result* 31)+((this.$strVal == null)? 0 :this.$strVal.hashCode()));
        result = ((result* 31)+((this.$field == null)? 0 :this.$field.hashCode()));
        result = ((result* 31)+((this.$strCast == null)? 0 :this.$strCast.hashCode()));
        result = ((result* 31)+((this.$attribute == null)? 0 :this.$attribute.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof StringValue) == false) {
            return false;
        }
        StringValue rhs = ((StringValue) other);
        return (((((this.$strVal == rhs.$strVal)||((this.$strVal!= null)&&this.$strVal.equals(rhs.$strVal)))&&((this.$field == rhs.$field)||((this.$field!= null)&&this.$field.equals(rhs.$field))))&&((this.$strCast == rhs.$strCast)||((this.$strCast!= null)&&this.$strCast.equals(rhs.$strCast))))&&((this.$attribute == rhs.$attribute)||((this.$attribute!= null)&&this.$attribute.equals(rhs.$attribute))));
    }

}
