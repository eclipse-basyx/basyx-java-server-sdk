/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.http.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.Objects;
import org.springframework.validation.annotation.Validated;

/**
 * Base class for Paginated results
 */
@Validated
@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-05-22T07:02:56.105163402Z[GMT]")

public class PagedResult {

	@JsonProperty("paging_metadata")
	private PagedResultPagingMetadata pagingMetadata = null;

	public PagedResult pagingMetadata(PagedResultPagingMetadata pagingMetadata) {
		this.pagingMetadata = pagingMetadata;
		return this;
	}

	/**
	 * Get pagingMetadata
	 *
	 * @return pagingMetadata
	 **/
	@Schema(description = "")

	@Valid
	public PagedResultPagingMetadata getPagingMetadata() {
		return pagingMetadata;
	}

	public void setPagingMetadata(PagedResultPagingMetadata pagingMetadata) {
		this.pagingMetadata = pagingMetadata;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PagedResult pagedResult = (PagedResult) o;
		return Objects.equals(this.pagingMetadata, pagedResult.pagingMetadata);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pagingMetadata);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class PagedResult {\n");

		sb.append("    pagingMetadata: ").append(toIndentedString(pagingMetadata)).append("\n");
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
