/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.submodelregistry.client.model;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;
import java.util.Objects;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets KeyTypes
 */
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

  /**
   * Convert the instance into URL query string.
   *
   * @param prefix prefix of the query string
   * @return URL query string
   */
  public String toUrlQueryString(String prefix) {
    if (prefix == null) {
      prefix = "";
    }

    return String.format("%s=%s", prefix, this.toString());
  }

}

