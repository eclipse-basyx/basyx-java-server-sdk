/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.authorization.abac;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.processing.Generated;
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
@Generated("jsonschema2pojo")
public class AttributeItem {

    @JsonProperty("CLAIM")
    private String claim;
    @JsonProperty("GLOBAL")
    private AttributeItem.Global global;
    @JsonProperty("REFERENCE")
    private String reference;

    /**
     * No args constructor for use in serialization
     * 
     */
    public AttributeItem() {
    }

    /**
     * 
     * @param reference
     * @param claim
     * @param global
     */
    public AttributeItem(String claim, AttributeItem.Global global, String reference) {
        super();
        this.claim = claim;
        this.global = global;
        this.reference = reference;
    }

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

    @Generated("jsonschema2pojo")
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
