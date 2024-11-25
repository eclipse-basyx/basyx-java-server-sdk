
package org.eclipse.digitaltwin.basyx.authorization.abac;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "$and",
    "$or",
    "$not",
    "$eq",
    "$ne",
    "$gt",
    "$ge",
    "$lt",
    "$le",
    "$contains",
    "$starts-with",
    "$ends-with",
    "$regex",
    "$cast",
    "additionalProperties"
})
@Generated("jsonschema2pojo")
public class LogicalExpression__1 {

    @JsonProperty("$and")
    private List<Object> $and = new ArrayList<Object>();
    @JsonProperty("$or")
    private List<Object> $or = new ArrayList<Object>();
    @JsonProperty("$not")
    private Object $not;
    @JsonProperty("$eq")
    private Object $eq;
    @JsonProperty("$ne")
    private Object $ne;
    @JsonProperty("$gt")
    private Object $gt;
    @JsonProperty("$ge")
    private Object $ge;
    @JsonProperty("$lt")
    private Object $lt;
    @JsonProperty("$le")
    private Object $le;
    @JsonProperty("$contains")
    private Object $contains;
    @JsonProperty("$starts-with")
    private Object $startsWith;
    @JsonProperty("$ends-with")
    private Object $endsWith;
    @JsonProperty("$regex")
    private Object $regex;
    @JsonProperty("$cast")
    private Object $cast;
    @JsonProperty("additionalProperties")
    private Object additionalProperties;

    @JsonProperty("$and")
    public List<Object> get$and() {
        return $and;
    }

    @JsonProperty("$and")
    public void set$and(List<Object> $and) {
        this.$and = $and;
    }

    @JsonProperty("$or")
    public List<Object> get$or() {
        return $or;
    }

    @JsonProperty("$or")
    public void set$or(List<Object> $or) {
        this.$or = $or;
    }

    @JsonProperty("$not")
    public Object get$not() {
        return $not;
    }

    @JsonProperty("$not")
    public void set$not(Object $not) {
        this.$not = $not;
    }

    @JsonProperty("$eq")
    public Object get$eq() {
        return $eq;
    }

    @JsonProperty("$eq")
    public void set$eq(Object $eq) {
        this.$eq = $eq;
    }

    @JsonProperty("$ne")
    public Object get$ne() {
        return $ne;
    }

    @JsonProperty("$ne")
    public void set$ne(Object $ne) {
        this.$ne = $ne;
    }

    @JsonProperty("$gt")
    public Object get$gt() {
        return $gt;
    }

    @JsonProperty("$gt")
    public void set$gt(Object $gt) {
        this.$gt = $gt;
    }

    @JsonProperty("$ge")
    public Object get$ge() {
        return $ge;
    }

    @JsonProperty("$ge")
    public void set$ge(Object $ge) {
        this.$ge = $ge;
    }

    @JsonProperty("$lt")
    public Object get$lt() {
        return $lt;
    }

    @JsonProperty("$lt")
    public void set$lt(Object $lt) {
        this.$lt = $lt;
    }

    @JsonProperty("$le")
    public Object get$le() {
        return $le;
    }

    @JsonProperty("$le")
    public void set$le(Object $le) {
        this.$le = $le;
    }

    @JsonProperty("$contains")
    public Object get$contains() {
        return $contains;
    }

    @JsonProperty("$contains")
    public void set$contains(Object $contains) {
        this.$contains = $contains;
    }

    @JsonProperty("$starts-with")
    public Object get$startsWith() {
        return $startsWith;
    }

    @JsonProperty("$starts-with")
    public void set$startsWith(Object $startsWith) {
        this.$startsWith = $startsWith;
    }

    @JsonProperty("$ends-with")
    public Object get$endsWith() {
        return $endsWith;
    }

    @JsonProperty("$ends-with")
    public void set$endsWith(Object $endsWith) {
        this.$endsWith = $endsWith;
    }

    @JsonProperty("$regex")
    public Object get$regex() {
        return $regex;
    }

    @JsonProperty("$regex")
    public void set$regex(Object $regex) {
        this.$regex = $regex;
    }

    @JsonProperty("$cast")
    public Object get$cast() {
        return $cast;
    }

    @JsonProperty("$cast")
    public void set$cast(Object $cast) {
        this.$cast = $cast;
    }

    @JsonProperty("additionalProperties")
    public Object getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonProperty("additionalProperties")
    public void setAdditionalProperties(Object additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(LogicalExpression__1 .class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("$and");
        sb.append('=');
        sb.append(((this.$and == null)?"<null>":this.$and));
        sb.append(',');
        sb.append("$or");
        sb.append('=');
        sb.append(((this.$or == null)?"<null>":this.$or));
        sb.append(',');
        sb.append("$not");
        sb.append('=');
        sb.append(((this.$not == null)?"<null>":this.$not));
        sb.append(',');
        sb.append("$eq");
        sb.append('=');
        sb.append(((this.$eq == null)?"<null>":this.$eq));
        sb.append(',');
        sb.append("$ne");
        sb.append('=');
        sb.append(((this.$ne == null)?"<null>":this.$ne));
        sb.append(',');
        sb.append("$gt");
        sb.append('=');
        sb.append(((this.$gt == null)?"<null>":this.$gt));
        sb.append(',');
        sb.append("$ge");
        sb.append('=');
        sb.append(((this.$ge == null)?"<null>":this.$ge));
        sb.append(',');
        sb.append("$lt");
        sb.append('=');
        sb.append(((this.$lt == null)?"<null>":this.$lt));
        sb.append(',');
        sb.append("$le");
        sb.append('=');
        sb.append(((this.$le == null)?"<null>":this.$le));
        sb.append(',');
        sb.append("$contains");
        sb.append('=');
        sb.append(((this.$contains == null)?"<null>":this.$contains));
        sb.append(',');
        sb.append("$startsWith");
        sb.append('=');
        sb.append(((this.$startsWith == null)?"<null>":this.$startsWith));
        sb.append(',');
        sb.append("$endsWith");
        sb.append('=');
        sb.append(((this.$endsWith == null)?"<null>":this.$endsWith));
        sb.append(',');
        sb.append("$regex");
        sb.append('=');
        sb.append(((this.$regex == null)?"<null>":this.$regex));
        sb.append(',');
        sb.append("$cast");
        sb.append('=');
        sb.append(((this.$cast == null)?"<null>":this.$cast));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
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
        result = ((result* 31)+((this.$and == null)? 0 :this.$and.hashCode()));
        result = ((result* 31)+((this.$ge == null)? 0 :this.$ge.hashCode()));
        result = ((result* 31)+((this.$endsWith == null)? 0 :this.$endsWith.hashCode()));
        result = ((result* 31)+((this.$or == null)? 0 :this.$or.hashCode()));
        result = ((result* 31)+((this.$regex == null)? 0 :this.$regex.hashCode()));
        result = ((result* 31)+((this.$startsWith == null)? 0 :this.$startsWith.hashCode()));
        result = ((result* 31)+((this.$cast == null)? 0 :this.$cast.hashCode()));
        result = ((result* 31)+((this.$lt == null)? 0 :this.$lt.hashCode()));
        result = ((result* 31)+((this.$contains == null)? 0 :this.$contains.hashCode()));
        result = ((result* 31)+((this.$eq == null)? 0 :this.$eq.hashCode()));
        result = ((result* 31)+((this.$gt == null)? 0 :this.$gt.hashCode()));
        result = ((result* 31)+((this.$ne == null)? 0 :this.$ne.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.$not == null)? 0 :this.$not.hashCode()));
        result = ((result* 31)+((this.$le == null)? 0 :this.$le.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof LogicalExpression__1) == false) {
            return false;
        }
        LogicalExpression__1 rhs = ((LogicalExpression__1) other);
        return ((((((((((((((((this.$and == rhs.$and)||((this.$and!= null)&&this.$and.equals(rhs.$and)))&&((this.$ge == rhs.$ge)||((this.$ge!= null)&&this.$ge.equals(rhs.$ge))))&&((this.$endsWith == rhs.$endsWith)||((this.$endsWith!= null)&&this.$endsWith.equals(rhs.$endsWith))))&&((this.$or == rhs.$or)||((this.$or!= null)&&this.$or.equals(rhs.$or))))&&((this.$regex == rhs.$regex)||((this.$regex!= null)&&this.$regex.equals(rhs.$regex))))&&((this.$startsWith == rhs.$startsWith)||((this.$startsWith!= null)&&this.$startsWith.equals(rhs.$startsWith))))&&((this.$cast == rhs.$cast)||((this.$cast!= null)&&this.$cast.equals(rhs.$cast))))&&((this.$lt == rhs.$lt)||((this.$lt!= null)&&this.$lt.equals(rhs.$lt))))&&((this.$contains == rhs.$contains)||((this.$contains!= null)&&this.$contains.equals(rhs.$contains))))&&((this.$eq == rhs.$eq)||((this.$eq!= null)&&this.$eq.equals(rhs.$eq))))&&((this.$gt == rhs.$gt)||((this.$gt!= null)&&this.$gt.equals(rhs.$gt))))&&((this.$ne == rhs.$ne)||((this.$ne!= null)&&this.$ne.equals(rhs.$ne))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.$not == rhs.$not)||((this.$not!= null)&&this.$not.equals(rhs.$not))))&&((this.$le == rhs.$le)||((this.$le!= null)&&this.$le.equals(rhs.$le))));
    }

}
