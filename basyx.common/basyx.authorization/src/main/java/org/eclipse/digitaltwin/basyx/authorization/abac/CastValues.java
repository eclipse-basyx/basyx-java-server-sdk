
package org.eclipse.digitaltwin.basyx.authorization.abac;

import java.util.Date;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "numVal",
    "hexVal",
    "dateTimeVal",
    "timeVal"
})
@Generated("jsonschema2pojo")
public class CastValues {

    @JsonProperty("numVal")
    private Double numVal;
    @JsonProperty("hexVal")
    private String hexVal;
    @JsonProperty("dateTimeVal")
    private Date dateTimeVal;
    @JsonProperty("timeVal")
    private String timeVal;

    /**
     * No args constructor for use in serialization
     * 
     */
    public CastValues() {
    }

    /**
     * 
     * @param numVal
     * @param hexVal
     * @param timeVal
     * @param dateTimeVal
     */
    public CastValues(Double numVal, String hexVal, Date dateTimeVal, String timeVal) {
        super();
        this.numVal = numVal;
        this.hexVal = hexVal;
        this.dateTimeVal = dateTimeVal;
        this.timeVal = timeVal;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(CastValues.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
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
        result = ((result* 31)+((this.hexVal == null)? 0 :this.hexVal.hashCode()));
        result = ((result* 31)+((this.timeVal == null)? 0 :this.timeVal.hashCode()));
        result = ((result* 31)+((this.numVal == null)? 0 :this.numVal.hashCode()));
        result = ((result* 31)+((this.dateTimeVal == null)? 0 :this.dateTimeVal.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof CastValues) == false) {
            return false;
        }
        CastValues rhs = ((CastValues) other);
        return (((((this.hexVal == rhs.hexVal)||((this.hexVal!= null)&&this.hexVal.equals(rhs.hexVal)))&&((this.timeVal == rhs.timeVal)||((this.timeVal!= null)&&this.timeVal.equals(rhs.timeVal))))&&((this.numVal == rhs.numVal)||((this.numVal!= null)&&this.numVal.equals(rhs.numVal))))&&((this.dateTimeVal == rhs.dateTimeVal)||((this.dateTimeVal!= null)&&this.dateTimeVal.equals(rhs.dateTimeVal))));
    }

}
