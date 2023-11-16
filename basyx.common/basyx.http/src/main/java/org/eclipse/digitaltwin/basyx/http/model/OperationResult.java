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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.springframework.validation.annotation.Validated;

/**
 * OperationResult
 */
@Validated
@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-08-10T13:45:22.063686165Z[GMT]")

public class OperationResult extends BaseOperationResult {

	@JsonProperty("inoutputArguments")
	@Valid
	private List<OperationVariable> inoutputArguments = null;

	@JsonProperty("outputArguments")
	@Valid
	private List<OperationVariable> outputArguments = null;

	public OperationResult inoutputArguments(List<OperationVariable> inoutputArguments) {
		this.inoutputArguments = inoutputArguments;
		return this;
	}

	public OperationResult addInoutputArgumentsItem(OperationVariable inoutputArgumentsItem) {
		if (this.inoutputArguments == null) {
			this.inoutputArguments = new ArrayList<OperationVariable>();
		}
		this.inoutputArguments.add(inoutputArgumentsItem);
		return this;
	}

	/**
	 * Get inoutputArguments
	 *
	 * @return inoutputArguments
	 **/
	@Schema(description = "")
	@Valid
	public List<OperationVariable> getInoutputArguments() {
		return inoutputArguments;
	}

	public void setInoutputArguments(List<OperationVariable> inoutputArguments) {
		this.inoutputArguments = inoutputArguments;
	}

	public OperationResult outputArguments(List<OperationVariable> outputArguments) {
		this.outputArguments = outputArguments;
		return this;
	}

	public OperationResult addOutputArgumentsItem(OperationVariable outputArgumentsItem) {
		if (this.outputArguments == null) {
			this.outputArguments = new ArrayList<OperationVariable>();
		}
		this.outputArguments.add(outputArgumentsItem);
		return this;
	}

	/**
	 * Get outputArguments
	 *
	 * @return outputArguments
	 **/
	@Schema(description = "")
	@Valid
	public List<OperationVariable> getOutputArguments() {
		return outputArguments;
	}

	public void setOutputArguments(List<OperationVariable> outputArguments) {
		this.outputArguments = outputArguments;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		OperationResult operationResult = (OperationResult) o;
		return Objects.equals(this.inoutputArguments, operationResult.inoutputArguments) && Objects.equals(this.outputArguments, operationResult.outputArguments) && super.equals(o);
	}

	@Override
	public int hashCode() {
		return Objects.hash(inoutputArguments, outputArguments, super.hashCode());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class OperationResult {\n");
		sb.append("    ").append(toIndentedString(super.toString())).append("\n");
		sb.append("    inoutputArguments: ").append(toIndentedString(inoutputArguments)).append("\n");
		sb.append("    outputArguments: ").append(toIndentedString(outputArguments)).append("\n");
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
