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


/**
 * Common JSON Schema for AAS Queries and Access Rules
 * <p>
 * This schema contains all classes that are shared between the AAS Query Language and the AAS Access Rule Language.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Query",
    "AllAccessPermissionRules"
})
@Generated("jsonschema2pojo")
public class Schema {

    @JsonProperty("Query")
    private Query query;
    @JsonProperty("AllAccessPermissionRules")
    private AllAccessPermissionRules allAccessPermissionRules;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Schema() {
    }

    /**
     * 
     * @param allAccessPermissionRules
     * @param query
     */
    public Schema(Query query, AllAccessPermissionRules allAccessPermissionRules) {
        super();
        this.query = query;
        this.allAccessPermissionRules = allAccessPermissionRules;
    }

    @JsonProperty("Query")
    public Query getQuery() {
        return query;
    }

    @JsonProperty("Query")
    public void setQuery(Query query) {
        this.query = query;
    }

    @JsonProperty("AllAccessPermissionRules")
    public AllAccessPermissionRules getAllAccessPermissionRules() {
        return allAccessPermissionRules;
    }

    @JsonProperty("AllAccessPermissionRules")
    public void setAllAccessPermissionRules(AllAccessPermissionRules allAccessPermissionRules) {
        this.allAccessPermissionRules = allAccessPermissionRules;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Schema.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("query");
        sb.append('=');
        sb.append(((this.query == null)?"<null>":this.query));
        sb.append(',');
        sb.append("allAccessPermissionRules");
        sb.append('=');
        sb.append(((this.allAccessPermissionRules == null)?"<null>":this.allAccessPermissionRules));
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
        result = ((result* 31)+((this.allAccessPermissionRules == null)? 0 :this.allAccessPermissionRules.hashCode()));
        result = ((result* 31)+((this.query == null)? 0 :this.query.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Schema) == false) {
            return false;
        }
        Schema rhs = ((Schema) other);
        return (((this.allAccessPermissionRules == rhs.allAccessPermissionRules)||((this.allAccessPermissionRules!= null)&&this.allAccessPermissionRules.equals(rhs.allAccessPermissionRules)))&&((this.query == rhs.query)||((this.query!= null)&&this.query.equals(rhs.query))));
    }

}
