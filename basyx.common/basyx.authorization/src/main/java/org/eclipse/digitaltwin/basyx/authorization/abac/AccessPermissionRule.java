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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
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
@Generated("jsonschema2pojo")
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

    /**
     * No args constructor for use in serialization
     * 
     */
    public AccessPermissionRule() {
    }

    /**
     * 
     * @param filter
     * @param fragment
     * @param usefilter
     * @param useobjects
     * @param objects
     * @param useacl
     * @param formula
     * @param acl
     * @param useformula
     */
    public AccessPermissionRule(Acl acl, String useacl, List<ObjectItem> objects, List<String> useobjects, LogicalExpression formula, String useformula, String fragment, LogicalExpression filter, String usefilter) {
        super();
        this.acl = acl;
        this.useacl = useacl;
        this.objects = objects;
        this.useobjects = useobjects;
        this.formula = formula;
        this.useformula = useformula;
        this.fragment = fragment;
        this.filter = filter;
        this.usefilter = usefilter;
    }

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
