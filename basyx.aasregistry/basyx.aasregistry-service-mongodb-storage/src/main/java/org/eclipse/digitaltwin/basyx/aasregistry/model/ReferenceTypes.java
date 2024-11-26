package org.eclipse.digitaltwin.basyx.aasregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets ReferenceTypes
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:44.252500800+01:00[Europe/Berlin]")
public enum ReferenceTypes {
  
  EXTERNALREFERENCE("ExternalReference"),
  
  MODELREFERENCE("ModelReference");

  private String value;

  ReferenceTypes(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static ReferenceTypes fromValue(String value) {
    for (ReferenceTypes b : ReferenceTypes.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

