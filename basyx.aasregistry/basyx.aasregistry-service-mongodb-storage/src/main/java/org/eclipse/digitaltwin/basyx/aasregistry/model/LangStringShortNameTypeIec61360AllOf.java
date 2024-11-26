package org.eclipse.digitaltwin.basyx.aasregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * LangStringShortNameTypeIec61360AllOf
 */

@JsonTypeName("LangStringShortNameTypeIec61360_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:44.252500800+01:00[Europe/Berlin]")
public class LangStringShortNameTypeIec61360AllOf implements Serializable {

  private static final long serialVersionUID = 1L;

  private String text;

  /**
   * Default constructor
   * @deprecated Use {@link LangStringShortNameTypeIec61360AllOf#LangStringShortNameTypeIec61360AllOf(String)}
   */
  @Deprecated
  public LangStringShortNameTypeIec61360AllOf() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public LangStringShortNameTypeIec61360AllOf(String text) {
    this.text = text;
  }

  public LangStringShortNameTypeIec61360AllOf text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Get text
   * @return text
  */
  @NotNull @Pattern(regexp = "^([\\t\\n\\r -\\ud7ff\\ue000-\\ufffd]|\\ud800[\\udc00-\\udfff]|[\\ud801-\\udbfe][\\udc00-\\udfff]|\\udbff[\\udc00-\\udfff])*$") @Size(min = 1, max = 18) 
  @Schema(name = "text", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("text")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LangStringShortNameTypeIec61360AllOf langStringShortNameTypeIec61360AllOf = (LangStringShortNameTypeIec61360AllOf) o;
    return Objects.equals(this.text, langStringShortNameTypeIec61360AllOf.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(text);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LangStringShortNameTypeIec61360AllOf {\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
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

