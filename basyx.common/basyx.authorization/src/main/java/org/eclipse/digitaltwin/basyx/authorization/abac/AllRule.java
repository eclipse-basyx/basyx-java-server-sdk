
package org.eclipse.digitaltwin.basyx.authorization.abac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ATTRIBUTES",
    "RIGHTS",
    "ACCESS",
    "OBJECTS",
    "FORMULA"
})
@Generated("jsonschema2pojo")
public class AllRule {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ATTRIBUTES")
    private List<AttributeItem> attributes = new ArrayList<AttributeItem>();
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("RIGHTS")
    private List<RightsEnum> rights = new ArrayList<RightsEnum>();
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ACCESS")
    private AllRule.Access access;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("OBJECTS")
    private List<ObjectItem> objects = new ArrayList<ObjectItem>();
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("FORMULA")
    private LogicalExpression formula;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public AllRule() {
    }

    /**
     * 
     * @param access
     * @param rights
     * @param objects
     * @param formula
     * @param attributes
     */
    public AllRule(List<AttributeItem> attributes, List<RightsEnum> rights, AllRule.Access access, List<ObjectItem> objects, LogicalExpression formula) {
        super();
        this.attributes = attributes;
        this.rights = rights;
        this.access = access;
        this.objects = objects;
        this.formula = formula;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ATTRIBUTES")
    public List<AttributeItem> getAttributes() {
        return attributes;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ATTRIBUTES")
    public void setAttributes(List<AttributeItem> attributes) {
        this.attributes = attributes;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("RIGHTS")
    public List<RightsEnum> getRights() {
        return rights;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("RIGHTS")
    public void setRights(List<RightsEnum> rights) {
        this.rights = rights;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ACCESS")
    public AllRule.Access getAccess() {
        return access;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ACCESS")
    public void setAccess(AllRule.Access access) {
        this.access = access;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("OBJECTS")
    public List<ObjectItem> getObjects() {
        return objects;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("OBJECTS")
    public void setObjects(List<ObjectItem> objects) {
        this.objects = objects;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("FORMULA")
    public LogicalExpression getFormula() {
        return formula;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("FORMULA")
    public void setFormula(LogicalExpression formula) {
        this.formula = formula;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AllRule.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("attributes");
        sb.append('=');
        sb.append(((this.attributes == null)?"<null>":this.attributes));
        sb.append(',');
        sb.append("rights");
        sb.append('=');
        sb.append(((this.rights == null)?"<null>":this.rights));
        sb.append(',');
        sb.append("access");
        sb.append('=');
        sb.append(((this.access == null)?"<null>":this.access));
        sb.append(',');
        sb.append("objects");
        sb.append('=');
        sb.append(((this.objects == null)?"<null>":this.objects));
        sb.append(',');
        sb.append("formula");
        sb.append('=');
        sb.append(((this.formula == null)?"<null>":this.formula));
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
        result = ((result* 31)+((this.access == null)? 0 :this.access.hashCode()));
        result = ((result* 31)+((this.rights == null)? 0 :this.rights.hashCode()));
        result = ((result* 31)+((this.objects == null)? 0 :this.objects.hashCode()));
        result = ((result* 31)+((this.formula == null)? 0 :this.formula.hashCode()));
        result = ((result* 31)+((this.attributes == null)? 0 :this.attributes.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AllRule) == false) {
            return false;
        }
        AllRule rhs = ((AllRule) other);
        return (((((((this.access == rhs.access)||((this.access!= null)&&this.access.equals(rhs.access)))&&((this.rights == rhs.rights)||((this.rights!= null)&&this.rights.equals(rhs.rights))))&&((this.objects == rhs.objects)||((this.objects!= null)&&this.objects.equals(rhs.objects))))&&((this.formula == rhs.formula)||((this.formula!= null)&&this.formula.equals(rhs.formula))))&&((this.attributes == rhs.attributes)||((this.attributes!= null)&&this.attributes.equals(rhs.attributes))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
    }

    @Generated("jsonschema2pojo")
    public enum Access {

        ALLOW("ALLOW"),
        DISABLED("DISABLED");
        private final String value;
        private final static Map<String, AllRule.Access> CONSTANTS = new HashMap<String, AllRule.Access>();

        static {
            for (AllRule.Access c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        Access(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static AllRule.Access fromValue(String value) {
            AllRule.Access constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
