package org.eclipse.digitaltwin.basyx.submodelregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.AdministrativeInformation;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.Endpoint;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.Extension;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.LangStringNameType;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.LangStringTextType;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.Reference;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * SubmodelDescriptor
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T16:46:10.102240900+02:00[Europe/Berlin]")
@org.springframework.data.mongodb.core.mapping.Document(collection = "submodeldescriptors")
public class SubmodelDescriptor implements Serializable {

  private static final long serialVersionUID = 1L;

  @Valid
  private List<@Valid LangStringTextType> description;

  @Valid
  private List<@Valid LangStringNameType> displayName;

  @Valid
  private List<@Valid Extension> extensions;

  private AdministrativeInformation administration;

  private String idShort;

  @org.springframework.data.annotation.Id
  private String id;

  private Reference semanticId;

  @Valid
  private List<@Valid Reference> supplementalSemanticId;

  @Valid
  private List<@Valid Endpoint> endpoints = new ArrayList<>();

  /**
   * Default constructor
   * @deprecated Use {@link SubmodelDescriptor#SubmodelDescriptor(String, List<@Valid Endpoint>)}
   */
  @Deprecated
  public SubmodelDescriptor() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SubmodelDescriptor(String id, List<@Valid Endpoint> endpoints) {
    this.id = id;
    this.endpoints = endpoints;
  }

  public SubmodelDescriptor description(List<@Valid LangStringTextType> description) {
    this.description = description;
    return this;
  }

  public SubmodelDescriptor addDescriptionItem(LangStringTextType descriptionItem) {
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

  public SubmodelDescriptor displayName(List<@Valid LangStringNameType> displayName) {
    this.displayName = displayName;
    return this;
  }

  public SubmodelDescriptor addDisplayNameItem(LangStringNameType displayNameItem) {
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

  public SubmodelDescriptor extensions(List<@Valid Extension> extensions) {
    this.extensions = extensions;
    return this;
  }

  public SubmodelDescriptor addExtensionsItem(Extension extensionsItem) {
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

  public SubmodelDescriptor administration(AdministrativeInformation administration) {
    this.administration = administration;
    return this;
  }

  /**
   * Get administration
   * @return administration
  */
  @Valid 
  @Schema(name = "administration", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("administration")
  public AdministrativeInformation getAdministration() {
    return administration;
  }

  public void setAdministration(AdministrativeInformation administration) {
    this.administration = administration;
  }

  public SubmodelDescriptor idShort(String idShort) {
    this.idShort = idShort;
    return this;
  }

  /**
   * Get idShort
   * @return idShort
  */
  @Size(max = 128) 
  @Schema(name = "idShort", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("idShort")
  public String getIdShort() {
    return idShort;
  }

  public void setIdShort(String idShort) {
    this.idShort = idShort;
  }

  public SubmodelDescriptor id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @NotNull @Pattern(regexp = "^[\\x09\\x0A\\x0D\\x20-\\uD7FF\\uE000-\\uFFFD\\u10000-\\u10FFFF]*$") @Size(min = 1, max = 2000) 
  @Schema(name = "id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public SubmodelDescriptor semanticId(Reference semanticId) {
    this.semanticId = semanticId;
    return this;
  }

  /**
   * Get semanticId
   * @return semanticId
  */
  @Valid 
  @Schema(name = "semanticId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("semanticId")
  public Reference getSemanticId() {
    return semanticId;
  }

  public void setSemanticId(Reference semanticId) {
    this.semanticId = semanticId;
  }

  public SubmodelDescriptor supplementalSemanticId(List<@Valid Reference> supplementalSemanticId) {
    this.supplementalSemanticId = supplementalSemanticId;
    return this;
  }

  public SubmodelDescriptor addSupplementalSemanticIdItem(Reference supplementalSemanticIdItem) {
    if (this.supplementalSemanticId == null) {
      this.supplementalSemanticId = new ArrayList<>();
    }
    this.supplementalSemanticId.add(supplementalSemanticIdItem);
    return this;
  }

  /**
   * Get supplementalSemanticId
   * @return supplementalSemanticId
  */
  @Valid @Size(min = 1) 
  @Schema(name = "supplementalSemanticId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("supplementalSemanticId")
  public List<@Valid Reference> getSupplementalSemanticId() {
    return supplementalSemanticId;
  }

  public void setSupplementalSemanticId(List<@Valid Reference> supplementalSemanticId) {
    this.supplementalSemanticId = supplementalSemanticId;
  }

  public SubmodelDescriptor endpoints(List<@Valid Endpoint> endpoints) {
    this.endpoints = endpoints;
    return this;
  }

  public SubmodelDescriptor addEndpointsItem(Endpoint endpointsItem) {
    if (this.endpoints == null) {
      this.endpoints = new ArrayList<>();
    }
    this.endpoints.add(endpointsItem);
    return this;
  }

  /**
   * Get endpoints
   * @return endpoints
  */
  @NotNull @Valid @Size(min = 1) 
  @Schema(name = "endpoints", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("endpoints")
  public List<@Valid Endpoint> getEndpoints() {
    return endpoints;
  }

  public void setEndpoints(List<@Valid Endpoint> endpoints) {
    this.endpoints = endpoints;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SubmodelDescriptor submodelDescriptor = (SubmodelDescriptor) o;
    return Objects.equals(this.description, submodelDescriptor.description) &&
        Objects.equals(this.displayName, submodelDescriptor.displayName) &&
        Objects.equals(this.extensions, submodelDescriptor.extensions) &&
        Objects.equals(this.administration, submodelDescriptor.administration) &&
        Objects.equals(this.idShort, submodelDescriptor.idShort) &&
        Objects.equals(this.id, submodelDescriptor.id) &&
        Objects.equals(this.semanticId, submodelDescriptor.semanticId) &&
        Objects.equals(this.supplementalSemanticId, submodelDescriptor.supplementalSemanticId) &&
        Objects.equals(this.endpoints, submodelDescriptor.endpoints);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, displayName, extensions, administration, idShort, id, semanticId, supplementalSemanticId, endpoints);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SubmodelDescriptor {\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    extensions: ").append(toIndentedString(extensions)).append("\n");
    sb.append("    administration: ").append(toIndentedString(administration)).append("\n");
    sb.append("    idShort: ").append(toIndentedString(idShort)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    semanticId: ").append(toIndentedString(semanticId)).append("\n");
    sb.append("    supplementalSemanticId: ").append(toIndentedString(supplementalSemanticId)).append("\n");
    sb.append("    endpoints: ").append(toIndentedString(endpoints)).append("\n");
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

