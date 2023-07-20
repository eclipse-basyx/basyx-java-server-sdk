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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.DataSpecificationContent;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Reference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * EmbeddedDataSpecification
 */
@JsonPropertyOrder({
  EmbeddedDataSpecification.JSON_PROPERTY_DATA_SPECIFICATION,
  EmbeddedDataSpecification.JSON_PROPERTY_DATA_SPECIFICATION_CONTENT
})
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2023-07-18T15:02:01.566475800+02:00[Europe/Berlin]")
public class EmbeddedDataSpecification {
  public static final String JSON_PROPERTY_DATA_SPECIFICATION = "dataSpecification";
  private Reference dataSpecification;

  public static final String JSON_PROPERTY_DATA_SPECIFICATION_CONTENT = "dataSpecificationContent";
  private DataSpecificationContent dataSpecificationContent;

  public EmbeddedDataSpecification() { 
  }

  public EmbeddedDataSpecification dataSpecification(Reference dataSpecification) {
    this.dataSpecification = dataSpecification;
    return this;
  }

   /**
   * Get dataSpecification
   * @return dataSpecification
  **/
  @javax.annotation.Nonnull
  @JsonProperty(JSON_PROPERTY_DATA_SPECIFICATION)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Reference getDataSpecification() {
    return dataSpecification;
  }


  @JsonProperty(JSON_PROPERTY_DATA_SPECIFICATION)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setDataSpecification(Reference dataSpecification) {
    this.dataSpecification = dataSpecification;
  }


  public EmbeddedDataSpecification dataSpecificationContent(DataSpecificationContent dataSpecificationContent) {
    this.dataSpecificationContent = dataSpecificationContent;
    return this;
  }

   /**
   * Get dataSpecificationContent
   * @return dataSpecificationContent
  **/
  @javax.annotation.Nonnull
  @JsonProperty(JSON_PROPERTY_DATA_SPECIFICATION_CONTENT)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public DataSpecificationContent getDataSpecificationContent() {
    return dataSpecificationContent;
  }


  @JsonProperty(JSON_PROPERTY_DATA_SPECIFICATION_CONTENT)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setDataSpecificationContent(DataSpecificationContent dataSpecificationContent) {
    this.dataSpecificationContent = dataSpecificationContent;
  }


  /**
   * Return true if this EmbeddedDataSpecification object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmbeddedDataSpecification embeddedDataSpecification = (EmbeddedDataSpecification) o;
    return Objects.equals(this.dataSpecification, embeddedDataSpecification.dataSpecification) &&
        Objects.equals(this.dataSpecificationContent, embeddedDataSpecification.dataSpecificationContent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataSpecification, dataSpecificationContent);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EmbeddedDataSpecification {\n");
    sb.append("    dataSpecification: ").append(toIndentedString(dataSpecification)).append("\n");
    sb.append("    dataSpecificationContent: ").append(toIndentedString(dataSpecificationContent)).append("\n");
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

  /**
   * Convert the instance into URL query string.
   *
   * @return URL query string
   */
  public String toUrlQueryString() {
    return toUrlQueryString(null);
  }

  /**
   * Convert the instance into URL query string.
   *
   * @param prefix prefix of the query string
   * @return URL query string
   */
  public String toUrlQueryString(String prefix) {
    String suffix = "";
    String containerSuffix = "";
    String containerPrefix = "";
    if (prefix == null) {
      // style=form, explode=true, e.g. /pet?name=cat&type=manx
      prefix = "";
    } else {
      // deepObject style e.g. /pet?id[name]=cat&id[type]=manx
      prefix = prefix + "[";
      suffix = "]";
      containerSuffix = "]";
      containerPrefix = "[";
    }

    StringJoiner joiner = new StringJoiner("&");

    // add `dataSpecification` to the URL query string
    if (getDataSpecification() != null) {
      joiner.add(getDataSpecification().toUrlQueryString(prefix + "dataSpecification" + suffix));
    }

    // add `dataSpecificationContent` to the URL query string
    if (getDataSpecificationContent() != null) {
      joiner.add(getDataSpecificationContent().toUrlQueryString(prefix + "dataSpecificationContent" + suffix));
    }

    return joiner.toString();
  }
}

