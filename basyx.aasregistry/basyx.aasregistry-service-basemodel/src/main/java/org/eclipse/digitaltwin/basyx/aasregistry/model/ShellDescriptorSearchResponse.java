package org.eclipse.digitaltwin.basyx.aasregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.openapitools.jackson.nullable.JsonNullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ShellDescriptorSearchResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:20.873153600+01:00[Europe/Berlin]")
public class ShellDescriptorSearchResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long total;

  @Valid
  private List<@Valid AssetAdministrationShellDescriptor> hits = new ArrayList<>();

  /**
   * Default constructor
   * @deprecated Use {@link ShellDescriptorSearchResponse#ShellDescriptorSearchResponse(Long, List<@Valid AssetAdministrationShellDescriptor>)}
   */
  @Deprecated
  public ShellDescriptorSearchResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ShellDescriptorSearchResponse(Long total, List<@Valid AssetAdministrationShellDescriptor> hits) {
    this.total = total;
    this.hits = hits;
  }

  public ShellDescriptorSearchResponse total(Long total) {
    this.total = total;
    return this;
  }

  /**
   * Get total
   * minimum: 0
   * @return total
  */
  @NotNull @Min(0L) 
  @Schema(name = "total", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("total")
  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }

  public ShellDescriptorSearchResponse hits(List<@Valid AssetAdministrationShellDescriptor> hits) {
    this.hits = hits;
    return this;
  }

  public ShellDescriptorSearchResponse addHitsItem(AssetAdministrationShellDescriptor hitsItem) {
    if (this.hits == null) {
      this.hits = new ArrayList<>();
    }
    this.hits.add(hitsItem);
    return this;
  }

  /**
   * Get hits
   * @return hits
  */
  @NotNull @Valid 
  @Schema(name = "hits", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hits")
  public List<@Valid AssetAdministrationShellDescriptor> getHits() {
    return hits;
  }

  public void setHits(List<@Valid AssetAdministrationShellDescriptor> hits) {
    this.hits = hits;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ShellDescriptorSearchResponse shellDescriptorSearchResponse = (ShellDescriptorSearchResponse) o;
    return Objects.equals(this.total, shellDescriptorSearchResponse.total) &&
        Objects.equals(this.hits, shellDescriptorSearchResponse.hits);
  }

  @Override
  public int hashCode() {
    return Objects.hash(total, hits);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ShellDescriptorSearchResponse {\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
    sb.append("    hits: ").append(toIndentedString(hits)).append("\n");
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

