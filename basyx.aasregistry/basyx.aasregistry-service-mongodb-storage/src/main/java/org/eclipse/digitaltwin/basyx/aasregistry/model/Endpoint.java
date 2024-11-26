package org.eclipse.digitaltwin.basyx.aasregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ProtocolInformation;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Endpoint
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:44.252500800+01:00[Europe/Berlin]")
public class Endpoint implements Serializable {

  private static final long serialVersionUID = 1L;

  @org.springframework.data.mongodb.core.mapping.Field(name="interface")@JsonProperty("interface")
  private String _interface;

  private ProtocolInformation protocolInformation;

  /**
   * Default constructor
   * @deprecated Use {@link Endpoint#Endpoint(String, ProtocolInformation)}
   */
  @Deprecated
  public Endpoint() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Endpoint(String _interface, ProtocolInformation protocolInformation) {
    this._interface = _interface;
    this.protocolInformation = protocolInformation;
  }

  public Endpoint _interface(String _interface) {
    this._interface = _interface;
    return this;
  }

  /**
   * Get _interface
   * @return _interface
  */
  @NotNull @Size(max = 128) 
  @Schema(name = "interface", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("interface")
  public String getInterface() {
    return _interface;
  }

  public void setInterface(String _interface) {
    this._interface = _interface;
  }

  public Endpoint protocolInformation(ProtocolInformation protocolInformation) {
    this.protocolInformation = protocolInformation;
    return this;
  }

  /**
   * Get protocolInformation
   * @return protocolInformation
  */
  @NotNull @Valid 
  @Schema(name = "protocolInformation", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("protocolInformation")
  public ProtocolInformation getProtocolInformation() {
    return protocolInformation;
  }

  public void setProtocolInformation(ProtocolInformation protocolInformation) {
    this.protocolInformation = protocolInformation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Endpoint endpoint = (Endpoint) o;
    return Objects.equals(this._interface, endpoint._interface) &&
        Objects.equals(this.protocolInformation, endpoint.protocolInformation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_interface, protocolInformation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Endpoint {\n");
    sb.append("    _interface: ").append(toIndentedString(_interface)).append("\n");
    sb.append("    protocolInformation: ").append(toIndentedString(protocolInformation)).append("\n");
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

