package org.eclipse.digitaltwin.basyx.submodelregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * LangStringPreferredNameTypeIec61360AllOf
 */

@JsonTypeName("LangStringPreferredNameTypeIec61360_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T16:46:10.102240900+02:00[Europe/Berlin]")
public class LangStringPreferredNameTypeIec61360AllOf implements Serializable {

  private static final long serialVersionUID = 1L;

  private String text;

  /**
   * Default constructor
   * @deprecated Use {@link LangStringPreferredNameTypeIec61360AllOf#LangStringPreferredNameTypeIec61360AllOf(String)}
   */
  @Deprecated
  public LangStringPreferredNameTypeIec61360AllOf() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public LangStringPreferredNameTypeIec61360AllOf(String text) {
    this.text = text;
  }

  public LangStringPreferredNameTypeIec61360AllOf text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Get text
   * @return text
  */
  @NotNull @Pattern(regexp = "^([\\t\\n\\r -\\ud7ff\\ue000-\\ufffd]|\\ud800[\\udc00-\\udfff]|[\\ud801-\\udbfe][\\udc00-\\udfff]|\\udbff[\\udc00-\\udfff])*$") @Size(min = 1, max = 255) 
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
    LangStringPreferredNameTypeIec61360AllOf langStringPreferredNameTypeIec61360AllOf = (LangStringPreferredNameTypeIec61360AllOf) o;
    return Objects.equals(this.text, langStringPreferredNameTypeIec61360AllOf.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(text);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LangStringPreferredNameTypeIec61360AllOf {\n");
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

