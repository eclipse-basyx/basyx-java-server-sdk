package org.eclipse.digitaltwin.basyx.authorization.abac;

import javax.annotation.processing.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"type",
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
public class SimpleExpression implements LogicalComponent {

	@JsonProperty("type")
    private Object type;
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

    @JsonProperty("type")
    public Object getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Object type) {
        this.type = type;
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
}
