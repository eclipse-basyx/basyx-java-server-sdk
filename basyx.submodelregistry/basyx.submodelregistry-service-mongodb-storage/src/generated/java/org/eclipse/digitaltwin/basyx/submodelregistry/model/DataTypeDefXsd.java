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
 * Gets or Sets DataTypeDefXsd
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-18T15:02:35.777401700+02:00[Europe/Berlin]")
public enum DataTypeDefXsd {
  
  ANYURI("xs:anyURI"),
  
  BASE64BINARY("xs:base64Binary"),
  
  BOOLEAN("xs:boolean"),
  
  BYTE("xs:byte"),
  
  DATE("xs:date"),
  
  DATETIME("xs:dateTime"),
  
  DECIMAL("xs:decimal"),
  
  DOUBLE("xs:double"),
  
  DURATION("xs:duration"),
  
  FLOAT("xs:float"),
  
  GDAY("xs:gDay"),
  
  GMONTH("xs:gMonth"),
  
  GMONTHDAY("xs:gMonthDay"),
  
  GYEAR("xs:gYear"),
  
  GYEARMONTH("xs:gYearMonth"),
  
  HEXBINARY("xs:hexBinary"),
  
  INT("xs:int"),
  
  INTEGER("xs:integer"),
  
  LONG("xs:long"),
  
  NEGATIVEINTEGER("xs:negativeInteger"),
  
  NONNEGATIVEINTEGER("xs:nonNegativeInteger"),
  
  NONPOSITIVEINTEGER("xs:nonPositiveInteger"),
  
  POSITIVEINTEGER("xs:positiveInteger"),
  
  SHORT("xs:short"),
  
  STRING("xs:string"),
  
  TIME("xs:time"),
  
  UNSIGNEDBYTE("xs:unsignedByte"),
  
  UNSIGNEDINT("xs:unsignedInt"),
  
  UNSIGNEDLONG("xs:unsignedLong"),
  
  UNSIGNEDSHORT("xs:unsignedShort");

  private String value;

  DataTypeDefXsd(String value) {
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
  public static DataTypeDefXsd fromValue(String value) {
    for (DataTypeDefXsd b : DataTypeDefXsd.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

