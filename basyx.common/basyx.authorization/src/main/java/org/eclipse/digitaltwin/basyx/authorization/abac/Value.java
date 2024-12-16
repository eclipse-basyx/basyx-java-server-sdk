
package org.eclipse.digitaltwin.basyx.authorization.abac;

import java.util.Date;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "strModel",
    "strVal",
    "numVal",
    "hexVal",
    "dateTimeVal",
    "timeVal",
    "castToString",
    "attribute"
})
@Generated("jsonschema2pojo")
public class Value {

    @JsonProperty("strModel")
    private String strModel;
    @JsonProperty("strVal")
    private String strVal;
    @JsonProperty("numVal")
    private Double numVal;
    @JsonProperty("hexVal")
    private String hexVal;
    @JsonProperty("dateTimeVal")
    private Date dateTimeVal;
    @JsonProperty("timeVal")
    private String timeVal;
    @JsonProperty("castToString")
    private CastValues castToString;
    @JsonProperty("attribute")
    private AttributeItem attribute;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Value() {
    }

    /**
     * 
     * @param castToString
     * @param numVal
     * @param strVal
     * @param hexVal
     * @param timeVal
     * @param strModel
     * @param attribute
     * @param dateTimeVal
     */
    public Value(String strModel, String strVal, Double numVal, String hexVal, Date dateTimeVal, String timeVal, CastValues castToString, AttributeItem attribute) {
        super();
        this.strModel = strModel;
        this.strVal = strVal;
        this.numVal = numVal;
        this.hexVal = hexVal;
        this.dateTimeVal = dateTimeVal;
        this.timeVal = timeVal;
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

    @JsonProperty("numVal")
    public Double getNumVal() {
        return numVal;
    }

    @JsonProperty("numVal")
    public void setNumVal(Double numVal) {
        this.numVal = numVal;
    }

    @JsonProperty("hexVal")
    public String getHexVal() {
        return hexVal;
    }

    @JsonProperty("hexVal")
    public void setHexVal(String hexVal) {
        this.hexVal = hexVal;
    }

    @JsonProperty("dateTimeVal")
    public Date getDateTimeVal() {
        return dateTimeVal;
    }

    @JsonProperty("dateTimeVal")
    public void setDateTimeVal(Date dateTimeVal) {
        this.dateTimeVal = dateTimeVal;
    }

    @JsonProperty("timeVal")
    public String getTimeVal() {
        return timeVal;
    }

    @JsonProperty("timeVal")
    public void setTimeVal(String timeVal) {
        this.timeVal = timeVal;
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
        sb.append(Value.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("strModel");
        sb.append('=');
        sb.append(((this.strModel == null)?"<null>":this.strModel));
        sb.append(',');
        sb.append("strVal");
        sb.append('=');
        sb.append(((this.strVal == null)?"<null>":this.strVal));
        sb.append(',');
        sb.append("numVal");
        sb.append('=');
        sb.append(((this.numVal == null)?"<null>":this.numVal));
        sb.append(',');
        sb.append("hexVal");
        sb.append('=');
        sb.append(((this.hexVal == null)?"<null>":this.hexVal));
        sb.append(',');
        sb.append("dateTimeVal");
        sb.append('=');
        sb.append(((this.dateTimeVal == null)?"<null>":this.dateTimeVal));
        sb.append(',');
        sb.append("timeVal");
        sb.append('=');
        sb.append(((this.timeVal == null)?"<null>":this.timeVal));
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
        result = ((result* 31)+((this.numVal == null)? 0 :this.numVal.hashCode()));
        result = ((result* 31)+((this.strVal == null)? 0 :this.strVal.hashCode()));
        result = ((result* 31)+((this.hexVal == null)? 0 :this.hexVal.hashCode()));
        result = ((result* 31)+((this.timeVal == null)? 0 :this.timeVal.hashCode()));
        result = ((result* 31)+((this.strModel == null)? 0 :this.strModel.hashCode()));
        result = ((result* 31)+((this.attribute == null)? 0 :this.attribute.hashCode()));
        result = ((result* 31)+((this.dateTimeVal == null)? 0 :this.dateTimeVal.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Value) == false) {
            return false;
        }
        Value rhs = ((Value) other);
        return (((((((((this.castToString == rhs.castToString)||((this.castToString!= null)&&this.castToString.equals(rhs.castToString)))&&((this.numVal == rhs.numVal)||((this.numVal!= null)&&this.numVal.equals(rhs.numVal))))&&((this.strVal == rhs.strVal)||((this.strVal!= null)&&this.strVal.equals(rhs.strVal))))&&((this.hexVal == rhs.hexVal)||((this.hexVal!= null)&&this.hexVal.equals(rhs.hexVal))))&&((this.timeVal == rhs.timeVal)||((this.timeVal!= null)&&this.timeVal.equals(rhs.timeVal))))&&((this.strModel == rhs.strModel)||((this.strModel!= null)&&this.strModel.equals(rhs.strModel))))&&((this.attribute == rhs.attribute)||((this.attribute!= null)&&this.attribute.equals(rhs.attribute))))&&((this.dateTimeVal == rhs.dateTimeVal)||((this.dateTimeVal!= null)&&this.dateTimeVal.equals(rhs.dateTimeVal))));
    }

}
