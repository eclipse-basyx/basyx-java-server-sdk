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
 * Gets or Sets ModelType
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:44.252500800+01:00[Europe/Berlin]")
public enum ModelType {
  
  ANNOTATEDRELATIONSHIPELEMENT("AnnotatedRelationshipElement"),
  
  ASSETADMINISTRATIONSHELL("AssetAdministrationShell"),
  
  BASICEVENTELEMENT("BasicEventElement"),
  
  BLOB("Blob"),
  
  CAPABILITY("Capability"),
  
  CONCEPTDESCRIPTION("ConceptDescription"),
  
  DATASPECIFICATIONIEC61360("DataSpecificationIec61360"),
  
  ENTITY("Entity"),
  
  FILE("File"),
  
  MULTILANGUAGEPROPERTY("MultiLanguageProperty"),
  
  OPERATION("Operation"),
  
  PROPERTY("Property"),
  
  RANGE("Range"),
  
  REFERENCEELEMENT("ReferenceElement"),
  
  RELATIONSHIPELEMENT("RelationshipElement"),
  
  SUBMODEL("Submodel"),
  
  SUBMODELELEMENTCOLLECTION("SubmodelElementCollection"),
  
  SUBMODELELEMENTLIST("SubmodelElementList");

  private String value;

  ModelType(String value) {
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
  public static ModelType fromValue(String value) {
    for (ModelType b : ModelType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

