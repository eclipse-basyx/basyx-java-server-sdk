package org.eclipse.digitaltwin.basyx.submodelregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets DataTypeIec61360
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T16:46:10.102240900+02:00[Europe/Berlin]")
public enum DataTypeIec61360 {
  
  BLOB("BLOB"),
  
  BOOLEAN("BOOLEAN"),
  
  DATE("DATE"),
  
  FILE("FILE"),
  
  HTML("HTML"),
  
  INTEGER_COUNT("INTEGER_COUNT"),
  
  INTEGER_CURRENCY("INTEGER_CURRENCY"),
  
  INTEGER_MEASURE("INTEGER_MEASURE"),
  
  IRDI("IRDI"),
  
  IRI("IRI"),
  
  RATIONAL("RATIONAL"),
  
  RATIONAL_MEASURE("RATIONAL_MEASURE"),
  
  REAL_COUNT("REAL_COUNT"),
  
  REAL_CURRENCY("REAL_CURRENCY"),
  
  REAL_MEASURE("REAL_MEASURE"),
  
  STRING("STRING"),
  
  STRING_TRANSLATABLE("STRING_TRANSLATABLE"),
  
  TIME("TIME"),
  
  TIMESTAMP("TIMESTAMP");

  private String value;

  DataTypeIec61360(String value) {
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
  public static DataTypeIec61360 fromValue(String value) {
    for (DataTypeIec61360 b : DataTypeIec61360.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

