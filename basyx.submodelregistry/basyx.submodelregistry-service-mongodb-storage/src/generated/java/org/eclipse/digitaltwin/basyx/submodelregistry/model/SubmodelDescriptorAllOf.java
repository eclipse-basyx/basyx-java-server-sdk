package org.eclipse.digitaltwin.basyx.submodelregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.AdministrativeInformation;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.Endpoint;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.Reference;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * SubmodelDescriptorAllOf
 */

@JsonTypeName("SubmodelDescriptor_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-18T15:02:35.777401700+02:00[Europe/Berlin]")
public class SubmodelDescriptorAllOf implements Serializable {

  private static final long serialVersionUID = 1L;

  private AdministrativeInformation administration;

  private String idShort;

  @org.springframework.data.annotation.Id
  private String id;

  private Reference semanticId;

  @Valid
  private List<@Valid Reference> supplementalSemanticId;

  @Valid
  private List<@Valid Endpoint> endpoints;

  public SubmodelDescriptorAllOf administration(AdministrativeInformation administration) {
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

  public SubmodelDescriptorAllOf idShort(String idShort) {
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

  public SubmodelDescriptorAllOf id(String id) {
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

  public SubmodelDescriptorAllOf semanticId(Reference semanticId) {
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

  public SubmodelDescriptorAllOf supplementalSemanticId(List<@Valid Reference> supplementalSemanticId) {
    this.supplementalSemanticId = supplementalSemanticId;
    return this;
  }

  public SubmodelDescriptorAllOf addSupplementalSemanticIdItem(Reference supplementalSemanticIdItem) {
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

  public SubmodelDescriptorAllOf endpoints(List<@Valid Endpoint> endpoints) {
    this.endpoints = endpoints;
    return this;
  }

  public SubmodelDescriptorAllOf addEndpointsItem(Endpoint endpointsItem) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SubmodelDescriptorAllOf submodelDescriptorAllOf = (SubmodelDescriptorAllOf) o;
    return Objects.equals(this.administration, submodelDescriptorAllOf.administration) &&
        Objects.equals(this.idShort, submodelDescriptorAllOf.idShort) &&
        Objects.equals(this.id, submodelDescriptorAllOf.id) &&
        Objects.equals(this.semanticId, submodelDescriptorAllOf.semanticId) &&
        Objects.equals(this.supplementalSemanticId, submodelDescriptorAllOf.supplementalSemanticId) &&
        Objects.equals(this.endpoints, submodelDescriptorAllOf.endpoints);
  }

  @Override
  public int hashCode() {
    return Objects.hash(administration, idShort, id, semanticId, supplementalSemanticId, endpoints);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SubmodelDescriptorAllOf {\n");
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

