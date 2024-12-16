
package org.eclipse.digitaltwin.basyx.authorization.abac;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "strModel",
    "strVal",
    "castToString",
    "attribute"
})
@Generated("jsonschema2pojo")
public class StringValue {

    @JsonProperty("strModel")
    private String strModel;
    @JsonProperty("strVal")
    private String strVal;
    @JsonProperty("castToString")
    private CastValues castToString;
    @JsonProperty("attribute")
    private AttributeItem attribute;

    /**
     * No args constructor for use in serialization
     * 
     */
    public StringValue() {
    }

    /**
     * 
     * @param castToString
     * @param strVal
     * @param strModel
     * @param attribute
     */
    public StringValue(String strModel, String strVal, CastValues castToString, AttributeItem attribute) {
        super();
        this.strModel = strModel;
        this.strVal = strVal;
        this.castToString = castToString;
        this.attribute = attribute;
    }

    @JsonProperty("strModel")
    public String getStrModel() {
        return strModel;
    }

    @JsonProperty("strModel")
    public void setStrModel(String strModel) {
        this.strModel = strModel;
    }

    @JsonProperty("strVal")
    public String getStrVal() {
        return strVal;
    }

    @JsonProperty("strVal")
    public void setStrVal(String strVal) {
        this.strVal = strVal;
    }

    @JsonProperty("castToString")
    public CastValues getCastToString() {
        return castToString;
    }

    @JsonProperty("castToString")
    public void setCastToString(CastValues castToString) {
        this.castToString = castToString;
    }

    @JsonProperty("attribute")
    public AttributeItem getAttribute() {
        return attribute;
    }

    @JsonProperty("attribute")
    public void setAttribute(AttributeItem attribute) {
        this.attribute = attribute;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(StringValue.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("strModel");
        sb.append('=');
        sb.append(((this.strModel == null)?"<null>":this.strModel));
        sb.append(',');
        sb.append("strVal");
        sb.append('=');
        sb.append(((this.strVal == null)?"<null>":this.strVal));
        sb.append(',');
        sb.append("castToString");
        sb.append('=');
        sb.append(((this.castToString == null)?"<null>":this.castToString));
        sb.append(',');
        sb.append("attribute");
        sb.append('=');
        sb.append(((this.attribute == null)?"<null>":this.attribute));
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
        result = ((result* 31)+((this.castToString == null)? 0 :this.castToString.hashCode()));
        result = ((result* 31)+((this.strModel == null)? 0 :this.strModel.hashCode()));
        result = ((result* 31)+((this.attribute == null)? 0 :this.attribute.hashCode()));
        result = ((result* 31)+((this.strVal == null)? 0 :this.strVal.hashCode()));
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
        return (((((this.castToString == rhs.castToString)||((this.castToString!= null)&&this.castToString.equals(rhs.castToString)))&&((this.strModel == rhs.strModel)||((this.strModel!= null)&&this.strModel.equals(rhs.strModel))))&&((this.attribute == rhs.attribute)||((this.attribute!= null)&&this.attribute.equals(rhs.attribute))))&&((this.strVal == rhs.strVal)||((this.strVal!= null)&&this.strVal.equals(rhs.strVal))));
    }

}
