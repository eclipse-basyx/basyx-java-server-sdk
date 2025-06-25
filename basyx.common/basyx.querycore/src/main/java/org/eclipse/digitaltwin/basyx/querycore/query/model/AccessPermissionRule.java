
package org.eclipse.digitaltwin.basyx.querycore.query.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ACL",
    "USEACL",
    "OBJECTS",
    "USEOBJECTS",
    "FORMULA",
    "USEFORMULA",
    "FRAGMENT",
    "FILTER",
    "USEFILTER"
})
public class AccessPermissionRule {

    @JsonProperty("ACL")
    private Acl acl;
    @JsonProperty("USEACL")
    private String useacl;
    @JsonProperty("OBJECTS")
    private List<ObjectItem> objects = new ArrayList<ObjectItem>();
    @JsonProperty("USEOBJECTS")
    private List<String> useobjects = new ArrayList<String>();
    @JsonProperty("FORMULA")
    private LogicalExpression formula;
    @JsonProperty("USEFORMULA")
    private String useformula;
    @JsonProperty("FRAGMENT")
    private String fragment;
    @JsonProperty("FILTER")
    private LogicalExpression filter;
    @JsonProperty("USEFILTER")
    private String usefilter;

    @JsonProperty("ACL")
    public Acl getAcl() {
        return acl;
    }

    @JsonProperty("ACL")
    public void setAcl(Acl acl) {
        this.acl = acl;
    }

    @JsonProperty("USEACL")
    public String getUseacl() {
        return useacl;
    }

    @JsonProperty("USEACL")
    public void setUseacl(String useacl) {
        this.useacl = useacl;
    }

    @JsonProperty("OBJECTS")
    public List<ObjectItem> getObjects() {
        return objects;
    }

    @JsonProperty("OBJECTS")
    public void setObjects(List<ObjectItem> objects) {
        this.objects = objects;
    }

    @JsonProperty("USEOBJECTS")
    public List<String> getUseobjects() {
        return useobjects;
    }

    @JsonProperty("USEOBJECTS")
    public void setUseobjects(List<String> useobjects) {
        this.useobjects = useobjects;
    }

    @JsonProperty("FORMULA")
    public LogicalExpression getFormula() {
        return formula;
    }

    @JsonProperty("FORMULA")
    public void setFormula(LogicalExpression formula) {
        this.formula = formula;
    }

    @JsonProperty("USEFORMULA")
    public String getUseformula() {
        return useformula;
    }

    @JsonProperty("USEFORMULA")
    public void setUseformula(String useformula) {
        this.useformula = useformula;
    }

    @JsonProperty("FRAGMENT")
    public String getFragment() {
        return fragment;
    }

    @JsonProperty("FRAGMENT")
    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    @JsonProperty("FILTER")
    public LogicalExpression getFilter() {
        return filter;
    }

    @JsonProperty("FILTER")
    public void setFilter(LogicalExpression filter) {
        this.filter = filter;
    }

    @JsonProperty("USEFILTER")
    public String getUsefilter() {
        return usefilter;
    }

    @JsonProperty("USEFILTER")
    public void setUsefilter(String usefilter) {
        this.usefilter = usefilter;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AccessPermissionRule.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("acl");
        sb.append('=');
        sb.append(((this.acl == null)?"<null>":this.acl));
        sb.append(',');
        sb.append("useacl");
        sb.append('=');
        sb.append(((this.useacl == null)?"<null>":this.useacl));
        sb.append(',');
        sb.append("objects");
        sb.append('=');
        sb.append(((this.objects == null)?"<null>":this.objects));
        sb.append(',');
        sb.append("useobjects");
        sb.append('=');
        sb.append(((this.useobjects == null)?"<null>":this.useobjects));
        sb.append(',');
        sb.append("formula");
        sb.append('=');
        sb.append(((this.formula == null)?"<null>":this.formula));
        sb.append(',');
        sb.append("useformula");
        sb.append('=');
        sb.append(((this.useformula == null)?"<null>":this.useformula));
        sb.append(',');
        sb.append("fragment");
        sb.append('=');
        sb.append(((this.fragment == null)?"<null>":this.fragment));
        sb.append(',');
        sb.append("filter");
        sb.append('=');
        sb.append(((this.filter == null)?"<null>":this.filter));
        sb.append(',');
        sb.append("usefilter");
        sb.append('=');
        sb.append(((this.usefilter == null)?"<null>":this.usefilter));
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
        result = ((result* 31)+((this.fragment == null)? 0 :this.fragment.hashCode()));
        result = ((result* 31)+((this.usefilter == null)? 0 :this.usefilter.hashCode()));
        result = ((result* 31)+((this.useobjects == null)? 0 :this.useobjects.hashCode()));
        result = ((result* 31)+((this.objects == null)? 0 :this.objects.hashCode()));
        result = ((result* 31)+((this.useacl == null)? 0 :this.useacl.hashCode()));
        result = ((result* 31)+((this.formula == null)? 0 :this.formula.hashCode()));
        result = ((result* 31)+((this.acl == null)? 0 :this.acl.hashCode()));
        result = ((result* 31)+((this.useformula == null)? 0 :this.useformula.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AccessPermissionRule) == false) {
            return false;
        }
        AccessPermissionRule rhs = ((AccessPermissionRule) other);
        return ((((((((((this.filter == rhs.filter)||((this.filter!= null)&&this.filter.equals(rhs.filter)))&&((this.fragment == rhs.fragment)||((this.fragment!= null)&&this.fragment.equals(rhs.fragment))))&&((this.usefilter == rhs.usefilter)||((this.usefilter!= null)&&this.usefilter.equals(rhs.usefilter))))&&((this.useobjects == rhs.useobjects)||((this.useobjects!= null)&&this.useobjects.equals(rhs.useobjects))))&&((this.objects == rhs.objects)||((this.objects!= null)&&this.objects.equals(rhs.objects))))&&((this.useacl == rhs.useacl)||((this.useacl!= null)&&this.useacl.equals(rhs.useacl))))&&((this.formula == rhs.formula)||((this.formula!= null)&&this.formula.equals(rhs.formula))))&&((this.acl == rhs.acl)||((this.acl!= null)&&this.acl.equals(rhs.acl))))&&((this.useformula == rhs.useformula)||((this.useformula!= null)&&this.useformula.equals(rhs.useformula))));
    }

}
