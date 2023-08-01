package org.eclipse.digitaltwin.basyx.submodelregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * LevelType
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T16:44:41.955927700+02:00[Europe/Berlin]")
public class LevelType implements Serializable {

  private static final long serialVersionUID = 1L;

  private Boolean min;

  private Boolean nom;

  private Boolean typ;

  private Boolean max;

  /**
   * Default constructor
   * @deprecated Use {@link LevelType#LevelType(Boolean, Boolean, Boolean, Boolean)}
   */
  @Deprecated
  public LevelType() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public LevelType(Boolean min, Boolean nom, Boolean typ, Boolean max) {
    this.min = min;
    this.nom = nom;
    this.typ = typ;
    this.max = max;
  }

  public LevelType min(Boolean min) {
    this.min = min;
    return this;
  }

  /**
   * Get min
   * @return min
  */
  @NotNull 
  @Schema(name = "min", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("min")
  public Boolean isMin() {
    return min;
  }

  public void setMin(Boolean min) {
    this.min = min;
  }

  public LevelType nom(Boolean nom) {
    this.nom = nom;
    return this;
  }

  /**
   * Get nom
   * @return nom
  */
  @NotNull 
  @Schema(name = "nom", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("nom")
  public Boolean isNom() {
    return nom;
  }

  public void setNom(Boolean nom) {
    this.nom = nom;
  }

  public LevelType typ(Boolean typ) {
    this.typ = typ;
    return this;
  }

  /**
   * Get typ
   * @return typ
  */
  @NotNull 
  @Schema(name = "typ", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("typ")
  public Boolean isTyp() {
    return typ;
  }

  public void setTyp(Boolean typ) {
    this.typ = typ;
  }

  public LevelType max(Boolean max) {
    this.max = max;
    return this;
  }

  /**
   * Get max
   * @return max
  */
  @NotNull 
  @Schema(name = "max", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("max")
  public Boolean isMax() {
    return max;
  }

  public void setMax(Boolean max) {
    this.max = max;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LevelType levelType = (LevelType) o;
    return Objects.equals(this.min, levelType.min) &&
        Objects.equals(this.nom, levelType.nom) &&
        Objects.equals(this.typ, levelType.typ) &&
        Objects.equals(this.max, levelType.max);
  }

  @Override
  public int hashCode() {
    return Objects.hash(min, nom, typ, max);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LevelType {\n");
    sb.append("    min: ").append(toIndentedString(min)).append("\n");
    sb.append("    nom: ").append(toIndentedString(nom)).append("\n");
    sb.append("    typ: ").append(toIndentedString(typ)).append("\n");
    sb.append("    max: ").append(toIndentedString(max)).append("\n");
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

