package org.eclipse.digitaltwin.basyx.aasregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.eclipse.digitaltwin.basyx.aasregistry.model.PagedResultPagingMetadata;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PagedResult
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:44.252500800+01:00[Europe/Berlin]")
public class PagedResult implements Serializable {

  private static final long serialVersionUID = 1L;

  @org.springframework.data.mongodb.core.mapping.Field(name="paging_metadata")@JsonProperty("paging_metadata")
  private PagedResultPagingMetadata pagingMetadata;

  public PagedResult pagingMetadata(PagedResultPagingMetadata pagingMetadata) {
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

  @Override
  public boolean equals(Object o) {
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
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

