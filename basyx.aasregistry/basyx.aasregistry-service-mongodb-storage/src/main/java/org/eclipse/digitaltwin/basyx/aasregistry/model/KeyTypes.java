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
 * Gets or Sets KeyTypes
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:44.252500800+01:00[Europe/Berlin]")
public enum KeyTypes {
  
  ANNOTATEDRELATIONSHIPELEMENT("AnnotatedRelationshipElement"),
  
  ASSETADMINISTRATIONSHELL("AssetAdministrationShell"),
  
  BASICEVENTELEMENT("BasicEventElement"),
  
  BLOB("Blob"),
  
  CAPABILITY("Capability"),
  
  CONCEPTDESCRIPTION("ConceptDescription"),
  
  DATAELEMENT("DataElement"),
  
  ENTITY("Entity"),
  
  EVENTELEMENT("EventElement"),
  
  FILE("File"),
  
  FRAGMENTREFERENCE("FragmentReference"),
  
  GLOBALREFERENCE("GlobalReference"),
  
  IDENTIFIABLE("Identifiable"),
  
  MULTILANGUAGEPROPERTY("MultiLanguageProperty"),
  
  OPERATION("Operation"),
  
  PROPERTY("Property"),
  
  RANGE("Range"),
  
  REFERABLE("Referable"),
  
  REFERENCEELEMENT("ReferenceElement"),
  
  RELATIONSHIPELEMENT("RelationshipElement"),
  
  SUBMODEL("Submodel"),
  
  SUBMODELELEMENT("SubmodelElement"),
  
  SUBMODELELEMENTCOLLECTION("SubmodelElementCollection"),
  
  SUBMODELELEMENTLIST("SubmodelElementList");

  private String value;

  KeyTypes(String value) {
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
  public static KeyTypes fromValue(String value) {
    for (KeyTypes b : KeyTypes.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

