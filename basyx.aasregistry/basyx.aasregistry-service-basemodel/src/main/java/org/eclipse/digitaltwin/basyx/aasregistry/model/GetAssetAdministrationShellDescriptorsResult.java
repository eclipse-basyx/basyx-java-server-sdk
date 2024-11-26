package org.eclipse.digitaltwin.basyx.aasregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.PagedResultPagingMetadata;
import org.openapitools.jackson.nullable.JsonNullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * GetAssetAdministrationShellDescriptorsResult
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:20.873153600+01:00[Europe/Berlin]")
public class GetAssetAdministrationShellDescriptorsResult implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty("paging_metadata")
  private PagedResultPagingMetadata pagingMetadata;

  @Valid
  private List<@Valid AssetAdministrationShellDescriptor> result;

  public GetAssetAdministrationShellDescriptorsResult pagingMetadata(PagedResultPagingMetadata pagingMetadata) {
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

  public GetAssetAdministrationShellDescriptorsResult result(List<@Valid AssetAdministrationShellDescriptor> result) {
    this.result = result;
    return this;
  }

  public GetAssetAdministrationShellDescriptorsResult addResultItem(AssetAdministrationShellDescriptor resultItem) {
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
  public List<@Valid AssetAdministrationShellDescriptor> getResult() {
    return result;
  }

  public void setResult(List<@Valid AssetAdministrationShellDescriptor> result) {
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
    GetAssetAdministrationShellDescriptorsResult getAssetAdministrationShellDescriptorsResult = (GetAssetAdministrationShellDescriptorsResult) o;
    return Objects.equals(this.pagingMetadata, getAssetAdministrationShellDescriptorsResult.pagingMetadata) &&
        Objects.equals(this.result, getAssetAdministrationShellDescriptorsResult.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pagingMetadata, result);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetAssetAdministrationShellDescriptorsResult {\n");
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

