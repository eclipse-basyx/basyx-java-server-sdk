package org.eclipse.digitaltwin.basyx.submodelregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.Reference;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AdministrativeInformationAllOf
 */

@JsonTypeName("AdministrativeInformation_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T16:46:10.102240900+02:00[Europe/Berlin]")
public class AdministrativeInformationAllOf implements Serializable {

  private static final long serialVersionUID = 1L;

  private String version;

  private String revision;

  private Reference creator;

  private String templateId;

  public AdministrativeInformationAllOf version(String version) {
    this.version = version;
    return this;
  }

  /**
   * Get version
   * @return version
  */
  @Pattern(regexp = "^(0|[1-9][0-9]{1,3})$") 
  @Schema(name = "version", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("version")
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public AdministrativeInformationAllOf revision(String revision) {
    this.revision = revision;
    return this;
  }

  /**
   * Get revision
   * @return revision
  */
  @Pattern(regexp = "^(0|[1-9][0-9]{1,3})$") 
  @Schema(name = "revision", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("revision")
  public String getRevision() {
    return revision;
  }

  public void setRevision(String revision) {
    this.revision = revision;
  }

  public AdministrativeInformationAllOf creator(Reference creator) {
    this.creator = creator;
    return this;
  }

  /**
   * Get creator
   * @return creator
  */
  @Valid 
  @Schema(name = "creator", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("creator")
  public Reference getCreator() {
    return creator;
  }

  public void setCreator(Reference creator) {
    this.creator = creator;
  }

  public AdministrativeInformationAllOf templateId(String templateId) {
    this.templateId = templateId;
    return this;
  }

  /**
   * Get templateId
   * @return templateId
  */
  @Pattern(regexp = "^([\\t\\n\\r \\ud7ff\\ue000-\\ufffd]|\\ud800[\\udc00-\\udfff]|[\\ud801-\\udbfe][\\udc00-\\udfff]|\\udbff[\\udc00-\\udfff])*$") @Size(min = 1, max = 2000) 
  @Schema(name = "templateId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("templateId")
  public String getTemplateId() {
    return templateId;
  }

  public void setTemplateId(String templateId) {
    this.templateId = templateId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AdministrativeInformationAllOf administrativeInformationAllOf = (AdministrativeInformationAllOf) o;
    return Objects.equals(this.version, administrativeInformationAllOf.version) &&
        Objects.equals(this.revision, administrativeInformationAllOf.revision) &&
        Objects.equals(this.creator, administrativeInformationAllOf.creator) &&
        Objects.equals(this.templateId, administrativeInformationAllOf.templateId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(version, revision, creator, templateId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AdministrativeInformationAllOf {\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    revision: ").append(toIndentedString(revision)).append("\n");
    sb.append("    creator: ").append(toIndentedString(creator)).append("\n");
    sb.append("    templateId: ").append(toIndentedString(templateId)).append("\n");
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

