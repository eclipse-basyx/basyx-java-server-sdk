
package org.eclipse.digitaltwin.basyx.authorization.abac;

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "strModel",
    "strVal",
    "numVal",
    "strAsNum"
})
@Generated("jsonschema2pojo")
public class Value {

    @JsonProperty("strModel")
    private String strModel;
    @JsonProperty("strVal")
    private String strVal;
    @JsonProperty("numVal")
    private Double numVal;
    @JsonProperty("strAsNum")
    private StringValue strAsNum;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Value() {
    }

    /**
     * 
     * @param numVal
     * @param strVal
     * @param strAsNum
     * @param strModel
     */
    public Value(String strModel, String strVal, Double numVal, StringValue strAsNum) {
        super();
        this.strModel = strModel;
        this.strVal = strVal;
        this.numVal = numVal;
        this.strAsNum = strAsNum;
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

    @JsonProperty("strAsNum")
    public StringValue getStrAsNum() {
        return strAsNum;
    }

    @JsonProperty("strAsNum")
    public void setStrAsNum(StringValue strAsNum) {
        this.strAsNum = strAsNum;
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
        sb.append("strAsNum");
        sb.append('=');
        sb.append(((this.strAsNum == null)?"<null>":this.strAsNum));
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
        result = ((result* 31)+((this.strAsNum == null)? 0 :this.strAsNum.hashCode()));
        result = ((result* 31)+((this.strModel == null)? 0 :this.strModel.hashCode()));
        result = ((result* 31)+((this.numVal == null)? 0 :this.numVal.hashCode()));
        result = ((result* 31)+((this.strVal == null)? 0 :this.strVal.hashCode()));
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
        return (((((this.strAsNum == rhs.strAsNum)||((this.strAsNum!= null)&&this.strAsNum.equals(rhs.strAsNum)))&&((this.strModel == rhs.strModel)||((this.strModel!= null)&&this.strModel.equals(rhs.strModel))))&&((this.numVal == rhs.numVal)||((this.numVal!= null)&&this.numVal.equals(rhs.numVal))))&&((this.strVal == rhs.strVal)||((this.strVal!= null)&&this.strVal.equals(rhs.strVal))));
    }

}
