package org.eclipse.digitaltwin.basyx.aasregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Page;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Sorting;
import org.openapitools.jackson.nullable.JsonNullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ShellDescriptorSearchRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:20.873153600+01:00[Europe/Berlin]")
public class ShellDescriptorSearchRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  private Page page;

  private Sorting sortBy;

  private ShellDescriptorQuery query;

  public ShellDescriptorSearchRequest page(Page page) {
    this.page = page;
    return this;
  }

  /**
   * Get page
   * @return page
  */
  @Valid 
  @Schema(name = "page", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("page")
  public Page getPage() {
    return page;
  }

  public void setPage(Page page) {
    this.page = page;
  }

  public ShellDescriptorSearchRequest sortBy(Sorting sortBy) {
    this.sortBy = sortBy;
    return this;
  }

  /**
   * Get sortBy
   * @return sortBy
  */
  @Valid 
  @Schema(name = "sortBy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sortBy")
  public Sorting getSortBy() {
    return sortBy;
  }

  public void setSortBy(Sorting sortBy) {
    this.sortBy = sortBy;
  }

  public ShellDescriptorSearchRequest query(ShellDescriptorQuery query) {
    this.query = query;
    return this;
  }

  /**
   * Get query
   * @return query
  */
  @Valid 
  @Schema(name = "query", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("query")
  public ShellDescriptorQuery getQuery() {
    return query;
  }

  public void setQuery(ShellDescriptorQuery query) {
    this.query = query;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ShellDescriptorSearchRequest shellDescriptorSearchRequest = (ShellDescriptorSearchRequest) o;
    return Objects.equals(this.page, shellDescriptorSearchRequest.page) &&
        Objects.equals(this.sortBy, shellDescriptorSearchRequest.sortBy) &&
        Objects.equals(this.query, shellDescriptorSearchRequest.query);
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, sortBy, query);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ShellDescriptorSearchRequest {\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    sortBy: ").append(toIndentedString(sortBy)).append("\n");
    sb.append("    query: ").append(toIndentedString(query)).append("\n");
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

