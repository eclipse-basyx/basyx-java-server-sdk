package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.http.pagination;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.basyx.http.pagination.PagedResult;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;

/**
 * InlineResponse200
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-10-10T10:16:17.046754509Z[GMT]")

public class InlineResponse200 extends PagedResult {
	@JsonProperty("result")
	@Valid
	private List<String> result = null;

	public InlineResponse200 result(List<String> result) {
		this.result = result;
		return this;
	}

	public InlineResponse200 addResultItem(String resultItem) {
		if (this.result == null) {
			this.result = new ArrayList<String>();
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

	public List<String> getResult() {
		return result;
	}

	public void setResult(List<String> result) {
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
		InlineResponse200 inlineResponse200 = (InlineResponse200) o;
		return Objects.equals(this.result, inlineResponse200.result) && super.equals(o);
	}

	@Override
	public int hashCode() {
		return Objects.hash(result, super.hashCode());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class InlineResponse200 {\n");
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
