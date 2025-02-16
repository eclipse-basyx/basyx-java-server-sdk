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
    "DEFATTRIBUTES",
    "DEFACLS",
    "DEFOBJECTS",
    "DEFFORMULAS",
    "rules"
})
@Generated("jsonschema2pojo")
public class AllAccessPermissionRules {

    @JsonProperty("DEFATTRIBUTES")
    private List<Defattribute> defattributes = new ArrayList<Defattribute>();
    @JsonProperty("DEFACLS")
    private List<Defacl> defacls = new ArrayList<Defacl>();
    @JsonProperty("DEFOBJECTS")
    private List<Defobject> defobjects = new ArrayList<Defobject>();
    @JsonProperty("DEFFORMULAS")
    private List<Defformula> defformulas = new ArrayList<Defformula>();
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("rules")
    private List<AccessPermissionRule> rules = new ArrayList<AccessPermissionRule>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public AllAccessPermissionRules() {
    }

    /**
     * 
     * @param defobjects
     * @param rules
     * @param defformulas
     * @param defattributes
     * @param defacls
     */
    public AllAccessPermissionRules(List<Defattribute> defattributes, List<Defacl> defacls, List<Defobject> defobjects, List<Defformula> defformulas, List<AccessPermissionRule> rules) {
        super();
        this.defattributes = defattributes;
        this.defacls = defacls;
        this.defobjects = defobjects;
        this.defformulas = defformulas;
        this.rules = rules;
    }

    @JsonProperty("DEFATTRIBUTES")
    public List<Defattribute> getDefattributes() {
        return defattributes;
    }

    @JsonProperty("DEFATTRIBUTES")
    public void setDefattributes(List<Defattribute> defattributes) {
        this.defattributes = defattributes;
    }

    @JsonProperty("DEFACLS")
    public List<Defacl> getDefacls() {
        return defacls;
    }

    @JsonProperty("DEFACLS")
    public void setDefacls(List<Defacl> defacls) {
        this.defacls = defacls;
    }

    @JsonProperty("DEFOBJECTS")
    public List<Defobject> getDefobjects() {
        return defobjects;
    }

    @JsonProperty("DEFOBJECTS")
    public void setDefobjects(List<Defobject> defobjects) {
        this.defobjects = defobjects;
    }

    @JsonProperty("DEFFORMULAS")
    public List<Defformula> getDefformulas() {
        return defformulas;
    }

    @JsonProperty("DEFFORMULAS")
    public void setDefformulas(List<Defformula> defformulas) {
        this.defformulas = defformulas;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("rules")
    public List<AccessPermissionRule> getRules() {
        return rules;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("rules")
    public void setRules(List<AccessPermissionRule> rules) {
        this.rules = rules;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AllAccessPermissionRules.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("defattributes");
        sb.append('=');
        sb.append(((this.defattributes == null)?"<null>":this.defattributes));
        sb.append(',');
        sb.append("defacls");
        sb.append('=');
        sb.append(((this.defacls == null)?"<null>":this.defacls));
        sb.append(',');
        sb.append("defobjects");
        sb.append('=');
        sb.append(((this.defobjects == null)?"<null>":this.defobjects));
        sb.append(',');
        sb.append("defformulas");
        sb.append('=');
        sb.append(((this.defformulas == null)?"<null>":this.defformulas));
        sb.append(',');
        sb.append("rules");
        sb.append('=');
        sb.append(((this.rules == null)?"<null>":this.rules));
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
        result = ((result* 31)+((this.rules == null)? 0 :this.rules.hashCode()));
        result = ((result* 31)+((this.defobjects == null)? 0 :this.defobjects.hashCode()));
        result = ((result* 31)+((this.defformulas == null)? 0 :this.defformulas.hashCode()));
        result = ((result* 31)+((this.defattributes == null)? 0 :this.defattributes.hashCode()));
        result = ((result* 31)+((this.defacls == null)? 0 :this.defacls.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AllAccessPermissionRules) == false) {
            return false;
        }
        AllAccessPermissionRules rhs = ((AllAccessPermissionRules) other);
        return ((((((this.rules == rhs.rules)||((this.rules!= null)&&this.rules.equals(rhs.rules)))&&((this.defobjects == rhs.defobjects)||((this.defobjects!= null)&&this.defobjects.equals(rhs.defobjects))))&&((this.defformulas == rhs.defformulas)||((this.defformulas!= null)&&this.defformulas.equals(rhs.defformulas))))&&((this.defattributes == rhs.defattributes)||((this.defattributes!= null)&&this.defattributes.equals(rhs.defattributes))))&&((this.defacls == rhs.defacls)||((this.defacls!= null)&&this.defacls.equals(rhs.defacls))));
    }

}
