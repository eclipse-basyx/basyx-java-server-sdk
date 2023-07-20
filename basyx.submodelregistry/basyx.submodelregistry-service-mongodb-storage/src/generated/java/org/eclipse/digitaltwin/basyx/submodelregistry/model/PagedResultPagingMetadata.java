package org.eclipse.digitaltwin.basyx.submodelregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PagedResultPagingMetadata
 */

@JsonTypeName("PagedResult_paging_metadata")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-18T15:02:35.777401700+02:00[Europe/Berlin]")
public class PagedResultPagingMetadata implements Serializable {

  private static final long serialVersionUID = 1L;

  private String cursor;

  public PagedResultPagingMetadata cursor(String cursor) {
    this.cursor = cursor;
    return this;
  }

  /**
   * Get cursor
   * @return cursor
  */
  
  @Schema(name = "cursor", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cursor")
  public String getCursor() {
    return cursor;
  }

  public void setCursor(String cursor) {
    this.cursor = cursor;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PagedResultPagingMetadata pagedResultPagingMetadata = (PagedResultPagingMetadata) o;
    return Objects.equals(this.cursor, pagedResultPagingMetadata.cursor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cursor);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PagedResultPagingMetadata {\n");
    sb.append("    cursor: ").append(toIndentedString(cursor)).append("\n");
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

