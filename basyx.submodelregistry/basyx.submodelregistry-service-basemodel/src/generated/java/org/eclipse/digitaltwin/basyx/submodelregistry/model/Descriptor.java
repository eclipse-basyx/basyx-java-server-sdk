package org.eclipse.digitaltwin.basyx.submodelregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.Extension;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.LangStringNameType;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.LangStringTextType;
import org.openapitools.jackson.nullable.JsonNullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Descriptor
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T16:44:41.955927700+02:00[Europe/Berlin]")
public class Descriptor implements Serializable {

  private static final long serialVersionUID = 1L;

  @Valid
  private List<@Valid LangStringTextType> description;

  @Valid
  private List<@Valid LangStringNameType> displayName;

  @Valid
  private List<@Valid Extension> extensions;

  public Descriptor description(List<@Valid LangStringTextType> description) {
    this.description = description;
    return this;
  }

  public Descriptor addDescriptionItem(LangStringTextType descriptionItem) {
    if (this.description == null) {
      this.description = new ArrayList<>();
    }
    this.description.add(descriptionItem);
    return this;
  }

  /**
   * Get description
   * @return description
  */
  @Valid 
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public List<@Valid LangStringTextType> getDescription() {
    return description;
  }

  public void setDescription(List<@Valid LangStringTextType> description) {
    this.description = description;
  }

  public Descriptor displayName(List<@Valid LangStringNameType> displayName) {
    this.displayName = displayName;
    return this;
  }

  public Descriptor addDisplayNameItem(LangStringNameType displayNameItem) {
    if (this.displayName == null) {
      this.displayName = new ArrayList<>();
    }
    this.displayName.add(displayNameItem);
    return this;
  }

  /**
   * Get displayName
   * @return displayName
  */
  @Valid 
  @Schema(name = "displayName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("displayName")
  public List<@Valid LangStringNameType> getDisplayName() {
    return displayName;
  }

  public void setDisplayName(List<@Valid LangStringNameType> displayName) {
    this.displayName = displayName;
  }

  public Descriptor extensions(List<@Valid Extension> extensions) {
    this.extensions = extensions;
    return this;
  }

  public Descriptor addExtensionsItem(Extension extensionsItem) {
    if (this.extensions == null) {
      this.extensions = new ArrayList<>();
    }
    this.extensions.add(extensionsItem);
    return this;
  }

  /**
   * Get extensions
   * @return extensions
  */
  @Valid @Size(min = 1) 
  @Schema(name = "extensions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("extensions")
  public List<@Valid Extension> getExtensions() {
    return extensions;
  }

  public void setExtensions(List<@Valid Extension> extensions) {
    this.extensions = extensions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Descriptor descriptor = (Descriptor) o;
    return Objects.equals(this.description, descriptor.description) &&
        Objects.equals(this.displayName, descriptor.displayName) &&
        Objects.equals(this.extensions, descriptor.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, displayName, extensions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Descriptor {\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    extensions: ").append(toIndentedString(extensions)).append("\n");
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

