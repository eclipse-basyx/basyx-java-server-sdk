package org.eclipse.digitaltwin.basyx.submodelregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.PagedResultPagingMetadata;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.openapitools.jackson.nullable.JsonNullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * GetSubmodelDescriptorsResult
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-18T15:01:40.984482400+02:00[Europe/Berlin]")
public class GetSubmodelDescriptorsResult implements Serializable {

  private static final long serialVersionUID = 1L;

  private PagedResultPagingMetadata pagingMetadata;

  @Valid
  private List<@Valid SubmodelDescriptor> result;

  public GetSubmodelDescriptorsResult pagingMetadata(PagedResultPagingMetadata pagingMetadata) {
    this.pagingMetadata = pagingMetadata;
    return this;
  }

  /**
   * Get pagingMetadata
   * @return pagingMetadata
  */
  @Valid 
  @Schema(name = "paging_metadata", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paging_metadata")
  public PagedResultPagingMetadata getPagingMetadata() {
    return pagingMetadata;
  }

  public void setPagingMetadata(PagedResultPagingMetadata pagingMetadata) {
    this.pagingMetadata = pagingMetadata;
  }

  public GetSubmodelDescriptorsResult result(List<@Valid SubmodelDescriptor> result) {
    this.result = result;
    return this;
  }

  public GetSubmodelDescriptorsResult addResultItem(SubmodelDescriptor resultItem) {
    if (this.result == null) {
      this.result = new ArrayList<>();
    }
    this.result.add(resultItem);
    return this;
  }

  /**
   * Get result
   * @return result
  */
  @Valid 
  @Schema(name = "result", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("result")
  public List<@Valid SubmodelDescriptor> getResult() {
    return result;
  }

  public void setResult(List<@Valid SubmodelDescriptor> result) {
    this.result = result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetSubmodelDescriptorsResult getSubmodelDescriptorsResult = (GetSubmodelDescriptorsResult) o;
    return Objects.equals(this.pagingMetadata, getSubmodelDescriptorsResult.pagingMetadata) &&
        Objects.equals(this.result, getSubmodelDescriptorsResult.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pagingMetadata, result);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetSubmodelDescriptorsResult {\n");
    sb.append("    pagingMetadata: ").append(toIndentedString(pagingMetadata)).append("\n");
    sb.append("    result: ").append(toIndentedString(result)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

