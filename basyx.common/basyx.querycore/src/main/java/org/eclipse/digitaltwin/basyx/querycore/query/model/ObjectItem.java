
package org.eclipse.digitaltwin.basyx.querycore.query.model;

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
