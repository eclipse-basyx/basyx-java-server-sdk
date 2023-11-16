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
package org.eclipse.digitaltwin.basyx.http.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.Objects;
import org.springframework.validation.annotation.Validated;

/**
 * BaseOperationResult
 */
@Validated
@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-08-10T13:45:22.063686165Z[GMT]")

public class BaseOperationResult extends Result {

	@JsonProperty("executionState")
	private ExecutionState executionState = null;

	@JsonProperty("success")
	private Boolean success = null;

	public BaseOperationResult executionState(ExecutionState executionState) {
		this.executionState = executionState;
		return this;
	}

	/**
	 * Get executionState
	 *
	 * @return executionState
	 **/
	@Schema(description = "")

	@Valid
	public ExecutionState getExecutionState() {
		return executionState;
	}

	public void setExecutionState(ExecutionState executionState) {
		this.executionState = executionState;
	}

	public BaseOperationResult success(Boolean success) {
		this.success = success;
		return this;
	}

	/**
	 * Get success
	 *
	 * @return success
	 **/
	@Schema(description = "")

	public Boolean isSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		BaseOperationResult baseOperationResult = (BaseOperationResult) o;
		return Objects.equals(this.executionState, baseOperationResult.executionState) && Objects.equals(this.success, baseOperationResult.success) && super.equals(o);
	}

	@Override
	public int hashCode() {
		return Objects.hash(executionState, success, super.hashCode());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class BaseOperationResult {\n");
		sb.append("    ").append(toIndentedString(super.toString())).append("\n");
		sb.append("    executionState: ").append(toIndentedString(executionState)).append("\n");
		sb.append("    success: ").append(toIndentedString(success)).append("\n");
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
