package org.eclipse.digitaltwin.basyx.aasregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SortDirection;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SortingPath;
import org.openapitools.jackson.nullable.JsonNullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Sorting
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:20.873153600+01:00[Europe/Berlin]")
public class Sorting implements Serializable {

  private static final long serialVersionUID = 1L;

  private SortDirection direction;

  @Valid
  private List<SortingPath> path = new ArrayList<>();

  /**
   * Default constructor
   * @deprecated Use {@link Sorting#Sorting(List<SortingPath>)}
   */
  @Deprecated
  public Sorting() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Sorting(List<SortingPath> path) {
    this.path = path;
  }

  public Sorting direction(SortDirection direction) {
    this.direction = direction;
    return this;
  }

  /**
   * Get direction
   * @return direction
  */
  @Valid 
  @Schema(name = "direction", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("direction")
  public SortDirection getDirection() {
    return direction;
  }

  public void setDirection(SortDirection direction) {
    this.direction = direction;
  }

  public Sorting path(List<SortingPath> path) {
    this.path = path;
    return this;
  }

  public Sorting addPathItem(SortingPath pathItem) {
    if (this.path == null) {
      this.path = new ArrayList<>();
    }
    this.path.add(pathItem);
    return this;
  }

  /**
   * Get path
   * @return path
  */
  @NotNull @Valid 
  @Schema(name = "path", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("path")
  public List<SortingPath> getPath() {
    return path;
  }

  public void setPath(List<SortingPath> path) {
    this.path = path;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Sorting sorting = (Sorting) o;
    return Objects.equals(this.direction, sorting.direction) &&
        Objects.equals(this.path, sorting.path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(direction, path);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Sorting {\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
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

