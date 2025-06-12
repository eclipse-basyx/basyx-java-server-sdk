
package org.eclipse.digitaltwin.basyx.querycore.query;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "$field",
    "$strVal",
    "$attribute",
    "$numVal",
    "$hexVal",
    "$dateTimeVal",
    "$timeVal",
    "$boolean",
    "$strCast",
    "$numCast",
    "$hexCast",
    "$boolCast",
    "$dateTimeCast",
    "$timeCast",
    "$dayOfWeek",
    "$dayOfMonth",
    "$month",
    "$year"
})
public class Value {

    @JsonProperty("$field")
    private String $field;
    @JsonProperty("$strVal")
    private String $strVal;
    @JsonProperty("$attribute")
    private AttributeItem $attribute;
    @JsonProperty("$numVal")
    private Double $numVal;
    @JsonProperty("$hexVal")
    private String $hexVal;
    @JsonProperty("$dateTimeVal")
    private Date $dateTimeVal;
    @JsonProperty("$timeVal")
    private String $timeVal;
    @JsonProperty("$boolean")
    private Boolean $boolean;
    @JsonProperty("$strCast")
    private Value $strCast;
    @JsonProperty("$numCast")
    private Value $numCast;
    @JsonProperty("$hexCast")
    private Value $hexCast;
    @JsonProperty("$boolCast")
    private Value $boolCast;
    @JsonProperty("$dateTimeCast")
    private Value $dateTimeCast;
    @JsonProperty("$timeCast")
    private Value $timeCast;
    @JsonProperty("$dayOfWeek")
    private Date $dayOfWeek;
    @JsonProperty("$dayOfMonth")
    private Date $dayOfMonth;
    @JsonProperty("$month")
    private Date $month;
    @JsonProperty("$year")
    private Date $year;

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

    @JsonProperty("$attribute")
    public AttributeItem get$attribute() {
        return $attribute;
    }

    @JsonProperty("$attribute")
    public void set$attribute(AttributeItem $attribute) {
        this.$attribute = $attribute;
    }

    @JsonProperty("$numVal")
    public Double get$numVal() {
        return $numVal;
    }

    @JsonProperty("$numVal")
    public void set$numVal(Double $numVal) {
        this.$numVal = $numVal;
    }

    @JsonProperty("$hexVal")
    public String get$hexVal() {
        return $hexVal;
    }

    @JsonProperty("$hexVal")
    public void set$hexVal(String $hexVal) {
        this.$hexVal = $hexVal;
    }

    @JsonProperty("$dateTimeVal")
    public Date get$dateTimeVal() {
        return $dateTimeVal;
    }

    @JsonProperty("$dateTimeVal")
    public void set$dateTimeVal(Date $dateTimeVal) {
        this.$dateTimeVal = $dateTimeVal;
    }

    @JsonProperty("$timeVal")
    public String get$timeVal() {
        return $timeVal;
    }

    @JsonProperty("$timeVal")
    public void set$timeVal(String $timeVal) {
        this.$timeVal = $timeVal;
    }

    @JsonProperty("$boolean")
    public Boolean get$boolean() {
        return $boolean;
    }

    @JsonProperty("$boolean")
    public void set$boolean(Boolean $boolean) {
        this.$boolean = $boolean;
    }

    @JsonProperty("$strCast")
    public Value get$strCast() {
        return $strCast;
    }

    @JsonProperty("$strCast")
    public void set$strCast(Value $strCast) {
        this.$strCast = $strCast;
    }

    @JsonProperty("$numCast")
    public Value get$numCast() {
        return $numCast;
    }

    @JsonProperty("$numCast")
    public void set$numCast(Value $numCast) {
        this.$numCast = $numCast;
    }

    @JsonProperty("$hexCast")
    public Value get$hexCast() {
        return $hexCast;
    }

    @JsonProperty("$hexCast")
    public void set$hexCast(Value $hexCast) {
        this.$hexCast = $hexCast;
    }

    @JsonProperty("$boolCast")
    public Value get$boolCast() {
        return $boolCast;
    }

    @JsonProperty("$boolCast")
    public void set$boolCast(Value $boolCast) {
        this.$boolCast = $boolCast;
    }

    @JsonProperty("$dateTimeCast")
    public Value get$dateTimeCast() {
        return $dateTimeCast;
    }

    @JsonProperty("$dateTimeCast")
    public void set$dateTimeCast(Value $dateTimeCast) {
        this.$dateTimeCast = $dateTimeCast;
    }

    @JsonProperty("$timeCast")
    public Value get$timeCast() {
        return $timeCast;
    }

    @JsonProperty("$timeCast")
    public void set$timeCast(Value $timeCast) {
        this.$timeCast = $timeCast;
    }

    @JsonProperty("$dayOfWeek")
    public Date get$dayOfWeek() {
        return $dayOfWeek;
    }

    @JsonProperty("$dayOfWeek")
    public void set$dayOfWeek(Date $dayOfWeek) {
        this.$dayOfWeek = $dayOfWeek;
    }

    @JsonProperty("$dayOfMonth")
    public Date get$dayOfMonth() {
        return $dayOfMonth;
    }

    @JsonProperty("$dayOfMonth")
    public void set$dayOfMonth(Date $dayOfMonth) {
        this.$dayOfMonth = $dayOfMonth;
    }

    @JsonProperty("$month")
    public Date get$month() {
        return $month;
    }

    @JsonProperty("$month")
    public void set$month(Date $month) {
        this.$month = $month;
    }

    @JsonProperty("$year")
    public Date get$year() {
        return $year;
    }

    @JsonProperty("$year")
    public void set$year(Date $year) {
        this.$year = $year;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Value.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("$field");
        sb.append('=');
        sb.append(((this.$field == null)?"<null>":this.$field));
        sb.append(',');
        sb.append("$strVal");
        sb.append('=');
        sb.append(((this.$strVal == null)?"<null>":this.$strVal));
        sb.append(',');
        sb.append("$attribute");
        sb.append('=');
        sb.append(((this.$attribute == null)?"<null>":this.$attribute));
        sb.append(',');
        sb.append("$numVal");
        sb.append('=');
        sb.append(((this.$numVal == null)?"<null>":this.$numVal));
        sb.append(',');
        sb.append("$hexVal");
        sb.append('=');
        sb.append(((this.$hexVal == null)?"<null>":this.$hexVal));
        sb.append(',');
        sb.append("$dateTimeVal");
        sb.append('=');
        sb.append(((this.$dateTimeVal == null)?"<null>":this.$dateTimeVal));
        sb.append(',');
        sb.append("$timeVal");
        sb.append('=');
        sb.append(((this.$timeVal == null)?"<null>":this.$timeVal));
        sb.append(',');
        sb.append("$boolean");
        sb.append('=');
        sb.append(((this.$boolean == null)?"<null>":this.$boolean));
        sb.append(',');
        sb.append("$strCast");
        sb.append('=');
        sb.append(((this.$strCast == null)?"<null>":this.$strCast));
        sb.append(',');
        sb.append("$numCast");
        sb.append('=');
        sb.append(((this.$numCast == null)?"<null>":this.$numCast));
        sb.append(',');
        sb.append("$hexCast");
        sb.append('=');
        sb.append(((this.$hexCast == null)?"<null>":this.$hexCast));
        sb.append(',');
        sb.append("$boolCast");
        sb.append('=');
        sb.append(((this.$boolCast == null)?"<null>":this.$boolCast));
        sb.append(',');
        sb.append("$dateTimeCast");
        sb.append('=');
        sb.append(((this.$dateTimeCast == null)?"<null>":this.$dateTimeCast));
        sb.append(',');
        sb.append("$timeCast");
        sb.append('=');
        sb.append(((this.$timeCast == null)?"<null>":this.$timeCast));
        sb.append(',');
        sb.append("$dayOfWeek");
        sb.append('=');
        sb.append(((this.$dayOfWeek == null)?"<null>":this.$dayOfWeek));
        sb.append(',');
        sb.append("$dayOfMonth");
        sb.append('=');
        sb.append(((this.$dayOfMonth == null)?"<null>":this.$dayOfMonth));
        sb.append(',');
        sb.append("$month");
        sb.append('=');
        sb.append(((this.$month == null)?"<null>":this.$month));
        sb.append(',');
        sb.append("$year");
        sb.append('=');
        sb.append(((this.$year == null)?"<null>":this.$year));
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
        result = ((result* 31)+((this.$boolean == null)? 0 :this.$boolean.hashCode()));
        result = ((result* 31)+((this.$attribute == null)? 0 :this.$attribute.hashCode()));
        result = ((result* 31)+((this.$strCast == null)? 0 :this.$strCast.hashCode()));
        result = ((result* 31)+((this.$numVal == null)? 0 :this.$numVal.hashCode()));
        result = ((result* 31)+((this.$dateTimeVal == null)? 0 :this.$dateTimeVal.hashCode()));
        result = ((result* 31)+((this.$timeVal == null)? 0 :this.$timeVal.hashCode()));
        result = ((result* 31)+((this.$field == null)? 0 :this.$field.hashCode()));
        result = ((result* 31)+((this.$dayOfWeek == null)? 0 :this.$dayOfWeek.hashCode()));
        result = ((result* 31)+((this.$boolCast == null)? 0 :this.$boolCast.hashCode()));
        result = ((result* 31)+((this.$hexCast == null)? 0 :this.$hexCast.hashCode()));
        result = ((result* 31)+((this.$dayOfMonth == null)? 0 :this.$dayOfMonth.hashCode()));
        result = ((result* 31)+((this.$year == null)? 0 :this.$year.hashCode()));
        result = ((result* 31)+((this.$hexVal == null)? 0 :this.$hexVal.hashCode()));
        result = ((result* 31)+((this.$timeCast == null)? 0 :this.$timeCast.hashCode()));
        result = ((result* 31)+((this.$dateTimeCast == null)? 0 :this.$dateTimeCast.hashCode()));
        result = ((result* 31)+((this.$month == null)? 0 :this.$month.hashCode()));
        result = ((result* 31)+((this.$numCast == null)? 0 :this.$numCast.hashCode()));
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
        return (((((((((((((((((((this.$strVal == rhs.$strVal)||((this.$strVal!= null)&&this.$strVal.equals(rhs.$strVal)))&&((this.$boolean == rhs.$boolean)||((this.$boolean!= null)&&this.$boolean.equals(rhs.$boolean))))&&((this.$attribute == rhs.$attribute)||((this.$attribute!= null)&&this.$attribute.equals(rhs.$attribute))))&&((this.$strCast == rhs.$strCast)||((this.$strCast!= null)&&this.$strCast.equals(rhs.$strCast))))&&((this.$numVal == rhs.$numVal)||((this.$numVal!= null)&&this.$numVal.equals(rhs.$numVal))))&&((this.$dateTimeVal == rhs.$dateTimeVal)||((this.$dateTimeVal!= null)&&this.$dateTimeVal.equals(rhs.$dateTimeVal))))&&((this.$timeVal == rhs.$timeVal)||((this.$timeVal!= null)&&this.$timeVal.equals(rhs.$timeVal))))&&((this.$field == rhs.$field)||((this.$field!= null)&&this.$field.equals(rhs.$field))))&&((this.$dayOfWeek == rhs.$dayOfWeek)||((this.$dayOfWeek!= null)&&this.$dayOfWeek.equals(rhs.$dayOfWeek))))&&((this.$boolCast == rhs.$boolCast)||((this.$boolCast!= null)&&this.$boolCast.equals(rhs.$boolCast))))&&((this.$hexCast == rhs.$hexCast)||((this.$hexCast!= null)&&this.$hexCast.equals(rhs.$hexCast))))&&((this.$dayOfMonth == rhs.$dayOfMonth)||((this.$dayOfMonth!= null)&&this.$dayOfMonth.equals(rhs.$dayOfMonth))))&&((this.$year == rhs.$year)||((this.$year!= null)&&this.$year.equals(rhs.$year))))&&((this.$hexVal == rhs.$hexVal)||((this.$hexVal!= null)&&this.$hexVal.equals(rhs.$hexVal))))&&((this.$timeCast == rhs.$timeCast)||((this.$timeCast!= null)&&this.$timeCast.equals(rhs.$timeCast))))&&((this.$dateTimeCast == rhs.$dateTimeCast)||((this.$dateTimeCast!= null)&&this.$dateTimeCast.equals(rhs.$dateTimeCast))))&&((this.$month == rhs.$month)||((this.$month!= null)&&this.$month.equals(rhs.$month))))&&((this.$numCast == rhs.$numCast)||((this.$numCast!= null)&&this.$numCast.equals(rhs.$numCast))));
    }

}
