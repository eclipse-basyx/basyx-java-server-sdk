/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasxfileserver.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.eclipse.digitaltwin.aas4j.v3.model.PackageDescription;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *  Paginated wrapper for {@link org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServer#getAllAASXPackageIds(String, PaginationInfo)}
 *
 * @author zielstor, fried
 */
public class GetPackageDescriptionResult extends PagedResult {

    @JsonProperty("result")
    @Valid
    private List<PackageDescription> result = null;

    public GetPackageDescriptionResult result(List<PackageDescription> result) {
        this.result = result;
        return this;
    }

    public GetPackageDescriptionResult addResultItem(PackageDescription resultItem) {
        if (this.result == null) {
            this.result = new ArrayList<PackageDescription>();
        }
        this.result.add(resultItem);
        return this;
    }

    /**
     * Get result
     *
     * @return result
     **/
    @Schema(description = "")
    @Valid
    public List<PackageDescription> getResult() {
        return result;
    }

    public void setResult(List<PackageDescription> result) {
        this.result = result;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GetPackageDescriptionResult getPackageDescriptionResult = (GetPackageDescriptionResult) o;
        return Objects.equals(this.result, getPackageDescriptionResult.result) && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class GetPackageDescriptionResult {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    result: ").append(toIndentedString(result)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
