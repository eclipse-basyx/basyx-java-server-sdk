package org.eclipse.digitaltwin.basyx.aasregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AdministrativeInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Endpoint;
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
 * AssetAdministrationShellDescriptorAllOf
 */

@JsonTypeName("AssetAdministrationShellDescriptor_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:20.873153600+01:00[Europe/Berlin]")
public class AssetAdministrationShellDescriptorAllOf implements Serializable {

  private static final long serialVersionUID = 1L;

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

  public AssetAdministrationShellDescriptorAllOf administration(AdministrativeInformation administration) {
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

  public AssetAdministrationShellDescriptorAllOf assetKind(AssetKind assetKind) {
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

  public AssetAdministrationShellDescriptorAllOf assetType(String assetType) {
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

  public AssetAdministrationShellDescriptorAllOf endpoints(List<@Valid Endpoint> endpoints) {
    this.endpoints = endpoints;
    return this;
  }

  public AssetAdministrationShellDescriptorAllOf addEndpointsItem(Endpoint endpointsItem) {
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

  public AssetAdministrationShellDescriptorAllOf globalAssetId(String globalAssetId) {
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

  public AssetAdministrationShellDescriptorAllOf idShort(String idShort) {
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

  public AssetAdministrationShellDescriptorAllOf id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @Pattern(regexp = "^[\\x09\\x0A\\x0D\\x20-\\uD7FF\\uE000-\\uFFFD\\u10000-\\u10FFFF]*$") @Size(min = 1, max = 2000) 
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public AssetAdministrationShellDescriptorAllOf specificAssetIds(List<@Valid SpecificAssetId> specificAssetIds) {
    this.specificAssetIds = specificAssetIds;
    return this;
  }

  public AssetAdministrationShellDescriptorAllOf addSpecificAssetIdsItem(SpecificAssetId specificAssetIdsItem) {
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

  public AssetAdministrationShellDescriptorAllOf submodelDescriptors(List<@Valid SubmodelDescriptor> submodelDescriptors) {
    this.submodelDescriptors = submodelDescriptors;
    return this;
  }

  public AssetAdministrationShellDescriptorAllOf addSubmodelDescriptorsItem(SubmodelDescriptor submodelDescriptorsItem) {
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
    AssetAdministrationShellDescriptorAllOf assetAdministrationShellDescriptorAllOf = (AssetAdministrationShellDescriptorAllOf) o;
    return Objects.equals(this.administration, assetAdministrationShellDescriptorAllOf.administration) &&
        Objects.equals(this.assetKind, assetAdministrationShellDescriptorAllOf.assetKind) &&
        Objects.equals(this.assetType, assetAdministrationShellDescriptorAllOf.assetType) &&
        Objects.equals(this.endpoints, assetAdministrationShellDescriptorAllOf.endpoints) &&
        Objects.equals(this.globalAssetId, assetAdministrationShellDescriptorAllOf.globalAssetId) &&
        Objects.equals(this.idShort, assetAdministrationShellDescriptorAllOf.idShort) &&
        Objects.equals(this.id, assetAdministrationShellDescriptorAllOf.id) &&
        Objects.equals(this.specificAssetIds, assetAdministrationShellDescriptorAllOf.specificAssetIds) &&
        Objects.equals(this.submodelDescriptors, assetAdministrationShellDescriptorAllOf.submodelDescriptors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(administration, assetKind, assetType, endpoints, globalAssetId, idShort, id, specificAssetIds, submodelDescriptors);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AssetAdministrationShellDescriptorAllOf {\n");
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

