
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
    "FORMULA",
    "FILTER"
})
@Generated("jsonschema2pojo")
public class AllAccessPermissionRule {

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
    private AllAccessPermissionRule.Access access;
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
    @JsonProperty("FILTER")
    private LogicalExpression filter;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public AllAccessPermissionRule() {
    }

    /**
     * 
     * @param filter
     * @param access
     * @param rights
     * @param objects
     * @param formula
     * @param attributes
     */
    public AllAccessPermissionRule(List<AttributeItem> attributes, List<RightsEnum> rights, AllAccessPermissionRule.Access access, List<ObjectItem> objects, LogicalExpression formula, LogicalExpression filter) {
        super();
        this.attributes = attributes;
        this.rights = rights;
        this.access = access;
        this.objects = objects;
        this.formula = formula;
        this.filter = filter;
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
    public AllAccessPermissionRule.Access getAccess() {
        return access;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ACCESS")
    public void setAccess(AllAccessPermissionRule.Access access) {
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

    @JsonProperty("FILTER")
    public LogicalExpression getFilter() {
        return filter;
    }

    @JsonProperty("FILTER")
    public void setFilter(LogicalExpression filter) {
        this.filter = filter;
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
        sb.append(AllAccessPermissionRule.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
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
        sb.append("filter");
        sb.append('=');
        sb.append(((this.filter == null)?"<null>":this.filter));
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
        result = ((result* 31)+((this.filter == null)? 0 :this.filter.hashCode()));
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
        if ((other instanceof AllAccessPermissionRule) == false) {
            return false;
        }
        AllAccessPermissionRule rhs = ((AllAccessPermissionRule) other);
        return ((((((((this.filter == rhs.filter)||((this.filter!= null)&&this.filter.equals(rhs.filter)))&&((this.access == rhs.access)||((this.access!= null)&&this.access.equals(rhs.access))))&&((this.rights == rhs.rights)||((this.rights!= null)&&this.rights.equals(rhs.rights))))&&((this.objects == rhs.objects)||((this.objects!= null)&&this.objects.equals(rhs.objects))))&&((this.formula == rhs.formula)||((this.formula!= null)&&this.formula.equals(rhs.formula))))&&((this.attributes == rhs.attributes)||((this.attributes!= null)&&this.attributes.equals(rhs.attributes))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
    }

    @Generated("jsonschema2pojo")
    public enum Access {

        ALLOW("ALLOW"),
        DISABLED("DISABLED");
        private final String value;
        private final static Map<String, AllAccessPermissionRule.Access> CONSTANTS = new HashMap<String, AllAccessPermissionRule.Access>();

        static {
            for (AllAccessPermissionRule.Access c: values()) {
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
        public static AllAccessPermissionRule.Access fromValue(String value) {
            AllAccessPermissionRule.Access constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
