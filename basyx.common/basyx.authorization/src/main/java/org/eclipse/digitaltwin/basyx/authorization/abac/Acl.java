
package org.eclipse.digitaltwin.basyx.authorization.abac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ATTRIBUTES",
    "USEATTRIBUTES",
    "RIGHTS",
    "ACCESS"
})
@Generated("jsonschema2pojo")
public class Acl {

    @JsonProperty("ATTRIBUTES")
    private List<AttributeItem> attributes = new ArrayList<AttributeItem>();
    @JsonProperty("USEATTRIBUTES")
    private String useattributes;
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
    private Acl.Access access;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Acl() {
    }

    /**
     * 
     * @param access
     * @param rights
     * @param useattributes
     * @param attributes
     */
    public Acl(List<AttributeItem> attributes, String useattributes, List<RightsEnum> rights, Acl.Access access) {
        super();
        this.attributes = attributes;
        this.useattributes = useattributes;
        this.rights = rights;
        this.access = access;
    }

    @JsonProperty("ATTRIBUTES")
    public List<AttributeItem> getAttributes() {
        return attributes;
    }

    @JsonProperty("ATTRIBUTES")
    public void setAttributes(List<AttributeItem> attributes) {
        this.attributes = attributes;
    }

    @JsonProperty("USEATTRIBUTES")
    public String getUseattributes() {
        return useattributes;
    }

    @JsonProperty("USEATTRIBUTES")
    public void setUseattributes(String useattributes) {
        this.useattributes = useattributes;
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
    public Acl.Access getAccess() {
        return access;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("ACCESS")
    public void setAccess(Acl.Access access) {
        this.access = access;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Acl.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("attributes");
        sb.append('=');
        sb.append(((this.attributes == null)?"<null>":this.attributes));
        sb.append(',');
        sb.append("useattributes");
        sb.append('=');
        sb.append(((this.useattributes == null)?"<null>":this.useattributes));
        sb.append(',');
        sb.append("rights");
        sb.append('=');
        sb.append(((this.rights == null)?"<null>":this.rights));
        sb.append(',');
        sb.append("access");
        sb.append('=');
        sb.append(((this.access == null)?"<null>":this.access));
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
        result = ((result* 31)+((this.useattributes == null)? 0 :this.useattributes.hashCode()));
        result = ((result* 31)+((this.attributes == null)? 0 :this.attributes.hashCode()));
        result = ((result* 31)+((this.access == null)? 0 :this.access.hashCode()));
        result = ((result* 31)+((this.rights == null)? 0 :this.rights.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Acl) == false) {
            return false;
        }
        Acl rhs = ((Acl) other);
        return (((((this.useattributes == rhs.useattributes)||((this.useattributes!= null)&&this.useattributes.equals(rhs.useattributes)))&&((this.attributes == rhs.attributes)||((this.attributes!= null)&&this.attributes.equals(rhs.attributes))))&&((this.access == rhs.access)||((this.access!= null)&&this.access.equals(rhs.access))))&&((this.rights == rhs.rights)||((this.rights!= null)&&this.rights.equals(rhs.rights))));
    }

    @Generated("jsonschema2pojo")
    public enum Access {

        ALLOW("ALLOW"),
        DISABLED("DISABLED");
        private final String value;
        private final static Map<String, Acl.Access> CONSTANTS = new HashMap<String, Acl.Access>();

        static {
            for (Acl.Access c: values()) {
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
        public static Acl.Access fromValue(String value) {
            Acl.Access constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
