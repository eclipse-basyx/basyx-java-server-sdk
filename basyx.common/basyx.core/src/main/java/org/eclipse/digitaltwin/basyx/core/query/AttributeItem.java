
package org.eclipse.digitaltwin.basyx.core.query;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "CLAIM",
    "GLOBAL",
    "REFERENCE"
})
public class AttributeItem {

    @JsonProperty("CLAIM")
    private String claim;
    @JsonProperty("GLOBAL")
    private AttributeItem.Global global;
    @JsonProperty("REFERENCE")
    private String reference;

    @JsonProperty("CLAIM")
    public String getClaim() {
        return claim;
    }

    @JsonProperty("CLAIM")
    public void setClaim(String claim) {
        this.claim = claim;
    }

    @JsonProperty("GLOBAL")
    public AttributeItem.Global getGlobal() {
        return global;
    }

    @JsonProperty("GLOBAL")
    public void setGlobal(AttributeItem.Global global) {
        this.global = global;
    }

    @JsonProperty("REFERENCE")
    public String getReference() {
        return reference;
    }

    @JsonProperty("REFERENCE")
    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AttributeItem.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("claim");
        sb.append('=');
        sb.append(((this.claim == null)?"<null>":this.claim));
        sb.append(',');
        sb.append("global");
        sb.append('=');
        sb.append(((this.global == null)?"<null>":this.global));
        sb.append(',');
        sb.append("reference");
        sb.append('=');
        sb.append(((this.reference == null)?"<null>":this.reference));
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
        result = ((result* 31)+((this.reference == null)? 0 :this.reference.hashCode()));
        result = ((result* 31)+((this.claim == null)? 0 :this.claim.hashCode()));
        result = ((result* 31)+((this.global == null)? 0 :this.global.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AttributeItem) == false) {
            return false;
        }
        AttributeItem rhs = ((AttributeItem) other);
        return ((((this.reference == rhs.reference)||((this.reference!= null)&&this.reference.equals(rhs.reference)))&&((this.claim == rhs.claim)||((this.claim!= null)&&this.claim.equals(rhs.claim))))&&((this.global == rhs.global)||((this.global!= null)&&this.global.equals(rhs.global))));
    }

    public enum Global {

        LOCALNOW("LOCALNOW"),
        UTCNOW("UTCNOW"),
        CLIENTNOW("CLIENTNOW"),
        ANONYMOUS("ANONYMOUS");
        private final String value;
        private final static Map<String, AttributeItem.Global> CONSTANTS = new HashMap<String, AttributeItem.Global>();

        static {
            for (AttributeItem.Global c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        Global(String value) {
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
        public static AttributeItem.Global fromValue(String value) {
            AttributeItem.Global constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
