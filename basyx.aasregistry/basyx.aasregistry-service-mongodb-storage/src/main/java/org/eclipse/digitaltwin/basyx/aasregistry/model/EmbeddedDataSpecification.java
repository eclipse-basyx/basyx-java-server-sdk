package org.eclipse.digitaltwin.basyx.aasregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.eclipse.digitaltwin.basyx.aasregistry.model.DataSpecificationContent;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Reference;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * EmbeddedDataSpecification
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:44.252500800+01:00[Europe/Berlin]")
public class EmbeddedDataSpecification implements Serializable {

  private static final long serialVersionUID = 1L;

  private Reference dataSpecification;

  private DataSpecificationContent dataSpecificationContent;

  /**
   * Default constructor
   * @deprecated Use {@link EmbeddedDataSpecification#EmbeddedDataSpecification(Reference, DataSpecificationContent)}
   */
  @Deprecated
  public EmbeddedDataSpecification() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EmbeddedDataSpecification(Reference dataSpecification, DataSpecificationContent dataSpecificationContent) {
    this.dataSpecification = dataSpecification;
    this.dataSpecificationContent = dataSpecificationContent;
  }

  public EmbeddedDataSpecification dataSpecification(Reference dataSpecification) {
    this.dataSpecification = dataSpecification;
    return this;
  }

  /**
   * Get dataSpecification
   * @return dataSpecification
  */
  @NotNull @Valid 
  @Schema(name = "dataSpecification", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("dataSpecification")
  public Reference getDataSpecification() {
    return dataSpecification;
  }

  public void setDataSpecification(Reference dataSpecification) {
    this.dataSpecification = dataSpecification;
  }

  public EmbeddedDataSpecification dataSpecificationContent(DataSpecificationContent dataSpecificationContent) {
    this.dataSpecificationContent = dataSpecificationContent;
    return this;
  }

  /**
   * Get dataSpecificationContent
   * @return dataSpecificationContent
  */
  @NotNull @Valid 
  @Schema(name = "dataSpecificationContent", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("dataSpecificationContent")
  public DataSpecificationContent getDataSpecificationContent() {
    return dataSpecificationContent;
  }

  public void setDataSpecificationContent(DataSpecificationContent dataSpecificationContent) {
    this.dataSpecificationContent = dataSpecificationContent;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmbeddedDataSpecification embeddedDataSpecification = (EmbeddedDataSpecification) o;
    return Objects.equals(this.dataSpecification, embeddedDataSpecification.dataSpecification) &&
        Objects.equals(this.dataSpecificationContent, embeddedDataSpecification.dataSpecificationContent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataSpecification, dataSpecificationContent);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EmbeddedDataSpecification {\n");
    sb.append("    dataSpecification: ").append(toIndentedString(dataSpecification)).append("\n");
    sb.append("    dataSpecificationContent: ").append(toIndentedString(dataSpecificationContent)).append("\n");
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

