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

import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ROUTE",
    "IDENTIFIABLE",
    "REFERABLE",
    "FRAGMENT",
    "DESCRIPTOR"
})
@Generated("jsonschema2pojo")
public class ObjectItem {

    @JsonProperty("ROUTE")
    private String route;
    @JsonProperty("IDENTIFIABLE")
    private String identifiable;
    @JsonProperty("REFERABLE")
    private String referable;
    @JsonProperty("FRAGMENT")
    private String fragment;
    @JsonProperty("DESCRIPTOR")
    private String descriptor;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ObjectItem() {
    }

    /**
     * 
     * @param fragment
     * @param route
     * @param referable
     * @param identifiable
     * @param descriptor
     */
    public ObjectItem(String route, String identifiable, String referable, String fragment, String descriptor) {
        super();
        this.route = route;
        this.identifiable = identifiable;
        this.referable = referable;
        this.fragment = fragment;
        this.descriptor = descriptor;
    }

    @JsonProperty("ROUTE")
    public String getRoute() {
        return route;
    }

    @JsonProperty("ROUTE")
    public void setRoute(String route) {
        this.route = route;
    }

    @JsonProperty("IDENTIFIABLE")
    public String getIdentifiable() {
        return identifiable;
    }

    @JsonProperty("IDENTIFIABLE")
    public void setIdentifiable(String identifiable) {
        this.identifiable = identifiable;
    }

    @JsonProperty("REFERABLE")
    public String getReferable() {
        return referable;
    }

    @JsonProperty("REFERABLE")
    public void setReferable(String referable) {
        this.referable = referable;
    }

    @JsonProperty("FRAGMENT")
    public String getFragment() {
        return fragment;
    }

    @JsonProperty("FRAGMENT")
    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    @JsonProperty("DESCRIPTOR")
    public String getDescriptor() {
        return descriptor;
    }

    @JsonProperty("DESCRIPTOR")
    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ObjectItem.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("route");
        sb.append('=');
        sb.append(((this.route == null)?"<null>":this.route));
        sb.append(',');
        sb.append("identifiable");
        sb.append('=');
        sb.append(((this.identifiable == null)?"<null>":this.identifiable));
        sb.append(',');
        sb.append("referable");
        sb.append('=');
        sb.append(((this.referable == null)?"<null>":this.referable));
        sb.append(',');
        sb.append("fragment");
        sb.append('=');
        sb.append(((this.fragment == null)?"<null>":this.fragment));
        sb.append(',');
        sb.append("descriptor");
        sb.append('=');
        sb.append(((this.descriptor == null)?"<null>":this.descriptor));
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
        result = ((result* 31)+((this.identifiable == null)? 0 :this.identifiable.hashCode()));
        result = ((result* 31)+((this.fragment == null)? 0 :this.fragment.hashCode()));
        result = ((result* 31)+((this.route == null)? 0 :this.route.hashCode()));
        result = ((result* 31)+((this.referable == null)? 0 :this.referable.hashCode()));
        result = ((result* 31)+((this.descriptor == null)? 0 :this.descriptor.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ObjectItem) == false) {
            return false;
        }
        ObjectItem rhs = ((ObjectItem) other);
        return ((((((this.identifiable == rhs.identifiable)||((this.identifiable!= null)&&this.identifiable.equals(rhs.identifiable)))&&((this.fragment == rhs.fragment)||((this.fragment!= null)&&this.fragment.equals(rhs.fragment))))&&((this.route == rhs.route)||((this.route!= null)&&this.route.equals(rhs.route))))&&((this.referable == rhs.referable)||((this.referable!= null)&&this.referable.equals(rhs.referable))))&&((this.descriptor == rhs.descriptor)||((this.descriptor!= null)&&this.descriptor.equals(rhs.descriptor))));
    }

}
