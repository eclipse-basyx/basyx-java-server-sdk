package org.eclipse.digitaltwin.basyx.aasregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AdministrativeInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Endpoint;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Extension;
import org.eclipse.digitaltwin.basyx.aasregistry.model.LangStringNameType;
import org.eclipse.digitaltwin.basyx.aasregistry.model.LangStringTextType;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.openapitools.jackson.nullable.JsonNullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AssetAdministrationShellDescriptor
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:20.873153600+01:00[Europe/Berlin]")
public class AssetAdministrationShellDescriptor implements Serializable {

  private static final long serialVersionUID = 1L;

  @Valid
  private List<@Valid LangStringTextType> description;

  @Valid
  private List<@Valid LangStringNameType> displayName;

  @Valid
  private List<@Valid Extension> extensions;

  private AdministrativeInformation administration;

  private AssetKind assetKind;

  private String assetType;

  @Valid
  private List<@Valid Endpoint> endpoints;

  private String globalAssetId;

  private String idShort;

  private String id;

  @Valid
  private List<@Valid SpecificAssetId> specificAssetIds;

  @Valid
  private List<@Valid SubmodelDescriptor> submodelDescriptors;

  /**
   * Default constructor
   * @deprecated Use {@link AssetAdministrationShellDescriptor#AssetAdministrationShellDescriptor(String)}
   */
  @Deprecated
  public AssetAdministrationShellDescriptor() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AssetAdministrationShellDescriptor(String id) {
    this.id = id;
  }

  public AssetAdministrationShellDescriptor description(List<@Valid LangStringTextType> description) {
    this.description = description;
    return this;
  }

  public AssetAdministrationShellDescriptor addDescriptionItem(LangStringTextType descriptionItem) {
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

  public AssetAdministrationShellDescriptor displayName(List<@Valid LangStringNameType> displayName) {
    this.displayName = displayName;
    return this;
  }

  public AssetAdministrationShellDescriptor addDisplayNameItem(LangStringNameType displayNameItem) {
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

  public AssetAdministrationShellDescriptor extensions(List<@Valid Extension> extensions) {
    this.extensions = extensions;
    return this;
  }

  public AssetAdministrationShellDescriptor addExtensionsItem(Extension extensionsItem) {
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

  public AssetAdministrationShellDescriptor administration(AdministrativeInformation administration) {
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

  public AssetAdministrationShellDescriptor assetKind(AssetKind assetKind) {
    this.assetKind = assetKind;
    return this;
  }

  /**
   * Get assetKind
   * @return assetKind
  */
  @Valid 
  @Schema(name = "assetKind", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("assetKind")
  public AssetKind getAssetKind() {
    return assetKind;
  }

  public void setAssetKind(AssetKind assetKind) {
    this.assetKind = assetKind;
  }

  public AssetAdministrationShellDescriptor assetType(String assetType) {
    this.assetType = assetType;
    return this;
  }

  /**
   * Get assetType
   * @return assetType
  */
  @Pattern(regexp = "^[\\x09\\x0A\\x0D\\x20-\\uD7FF\\uE000-\\uFFFD\\u10000-\\u10FFFF]*$") @Size(min = 1, max = 2000) 
  @Schema(name = "assetType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("assetType")
  public String getAssetType() {
    return assetType;
  }

  public void setAssetType(String assetType) {
    this.assetType = assetType;
  }

  public AssetAdministrationShellDescriptor endpoints(List<@Valid Endpoint> endpoints) {
    this.endpoints = endpoints;
    return this;
  }

  public AssetAdministrationShellDescriptor addEndpointsItem(Endpoint endpointsItem) {
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
  @Valid @Size(min = 1) 
  @Schema(name = "endpoints", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("endpoints")
  public List<@Valid Endpoint> getEndpoints() {
    return endpoints;
  }

  public void setEndpoints(List<@Valid Endpoint> endpoints) {
    this.endpoints = endpoints;
  }

  public AssetAdministrationShellDescriptor globalAssetId(String globalAssetId) {
    this.globalAssetId = globalAssetId;
    return this;
  }

  /**
   * Get globalAssetId
   * @return globalAssetId
  */
  @Pattern(regexp = "^[\\x09\\x0A\\x0D\\x20-\\uD7FF\\uE000-\\uFFFD\\u10000-\\u10FFFF]*$") @Size(min = 1, max = 2000) 
  @Schema(name = "globalAssetId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("globalAssetId")
  public String getGlobalAssetId() {
    return globalAssetId;
  }

  public void setGlobalAssetId(String globalAssetId) {
    this.globalAssetId = globalAssetId;
  }

  public AssetAdministrationShellDescriptor idShort(String idShort) {
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

  public AssetAdministrationShellDescriptor id(String id) {
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

  public AssetAdministrationShellDescriptor specificAssetIds(List<@Valid SpecificAssetId> specificAssetIds) {
    this.specificAssetIds = specificAssetIds;
    return this;
  }

  public AssetAdministrationShellDescriptor addSpecificAssetIdsItem(SpecificAssetId specificAssetIdsItem) {
    if (this.specificAssetIds == null) {
      this.specificAssetIds = new ArrayList<>();
    }
    this.specificAssetIds.add(specificAssetIdsItem);
    return this;
  }

  /**
   * Get specificAssetIds
   * @return specificAssetIds
  */
  @Valid 
  @Schema(name = "specificAssetIds", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("specificAssetIds")
  public List<@Valid SpecificAssetId> getSpecificAssetIds() {
    return specificAssetIds;
  }

  public void setSpecificAssetIds(List<@Valid SpecificAssetId> specificAssetIds) {
    this.specificAssetIds = specificAssetIds;
  }

  public AssetAdministrationShellDescriptor submodelDescriptors(List<@Valid SubmodelDescriptor> submodelDescriptors) {
    this.submodelDescriptors = submodelDescriptors;
    return this;
  }

  public AssetAdministrationShellDescriptor addSubmodelDescriptorsItem(SubmodelDescriptor submodelDescriptorsItem) {
    if (this.submodelDescriptors == null) {
      this.submodelDescriptors = new ArrayList<>();
    }
    this.submodelDescriptors.add(submodelDescriptorsItem);
    return this;
  }

  /**
   * Get submodelDescriptors
   * @return submodelDescriptors
  */
  @Valid 
  @Schema(name = "submodelDescriptors", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("submodelDescriptors")
  public List<@Valid SubmodelDescriptor> getSubmodelDescriptors() {
    return submodelDescriptors;
  }

  public void setSubmodelDescriptors(List<@Valid SubmodelDescriptor> submodelDescriptors) {
    this.submodelDescriptors = submodelDescriptors;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AssetAdministrationShellDescriptor assetAdministrationShellDescriptor = (AssetAdministrationShellDescriptor) o;
    return Objects.equals(this.description, assetAdministrationShellDescriptor.description) &&
        Objects.equals(this.displayName, assetAdministrationShellDescriptor.displayName) &&
        Objects.equals(this.extensions, assetAdministrationShellDescriptor.extensions) &&
        Objects.equals(this.administration, assetAdministrationShellDescriptor.administration) &&
        Objects.equals(this.assetKind, assetAdministrationShellDescriptor.assetKind) &&
        Objects.equals(this.assetType, assetAdministrationShellDescriptor.assetType) &&
        Objects.equals(this.endpoints, assetAdministrationShellDescriptor.endpoints) &&
        Objects.equals(this.globalAssetId, assetAdministrationShellDescriptor.globalAssetId) &&
        Objects.equals(this.idShort, assetAdministrationShellDescriptor.idShort) &&
        Objects.equals(this.id, assetAdministrationShellDescriptor.id) &&
        Objects.equals(this.specificAssetIds, assetAdministrationShellDescriptor.specificAssetIds) &&
        Objects.equals(this.submodelDescriptors, assetAdministrationShellDescriptor.submodelDescriptors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, displayName, extensions, administration, assetKind, assetType, endpoints, globalAssetId, idShort, id, specificAssetIds, submodelDescriptors);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AssetAdministrationShellDescriptor {\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    extensions: ").append(toIndentedString(extensions)).append("\n");
    sb.append("    administration: ").append(toIndentedString(administration)).append("\n");
    sb.append("    assetKind: ").append(toIndentedString(assetKind)).append("\n");
    sb.append("    assetType: ").append(toIndentedString(assetType)).append("\n");
    sb.append("    endpoints: ").append(toIndentedString(endpoints)).append("\n");
    sb.append("    globalAssetId: ").append(toIndentedString(globalAssetId)).append("\n");
    sb.append("    idShort: ").append(toIndentedString(idShort)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    specificAssetIds: ").append(toIndentedString(specificAssetIds)).append("\n");
    sb.append("    submodelDescriptors: ").append(toIndentedString(submodelDescriptors)).append("\n");
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

