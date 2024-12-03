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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.PagedResultPagingMetadata;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * GetSubmodelDescriptorsResult
 */
@JsonPropertyOrder({
  GetSubmodelDescriptorsResult.JSON_PROPERTY_PAGING_METADATA,
  GetSubmodelDescriptorsResult.JSON_PROPERTY_RESULT
})
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-11-11T08:29:25.882305+01:00[Europe/Berlin]")
public class GetSubmodelDescriptorsResult {
  public static final String JSON_PROPERTY_PAGING_METADATA = "paging_metadata";
  private PagedResultPagingMetadata pagingMetadata;

  public static final String JSON_PROPERTY_RESULT = "result";
  private List<SubmodelDescriptor> result;

  public GetSubmodelDescriptorsResult() { 
  }

  public GetSubmodelDescriptorsResult pagingMetadata(PagedResultPagingMetadata pagingMetadata) {
    this.pagingMetadata = pagingMetadata;
    return this;
  }

   /**
   * Get pagingMetadata
   * @return pagingMetadata
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_PAGING_METADATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public PagedResultPagingMetadata getPagingMetadata() {
    return pagingMetadata;
  }


  @JsonProperty(JSON_PROPERTY_PAGING_METADATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPagingMetadata(PagedResultPagingMetadata pagingMetadata) {
    this.pagingMetadata = pagingMetadata;
  }


  public GetSubmodelDescriptorsResult result(List<SubmodelDescriptor> result) {
    this.result = result;
    return this;
  }

  public GetSubmodelDescriptorsResult addResultItem(SubmodelDescriptor resultItem) {
    if (this.result == null) {
      this.result = new ArrayList<>();
    }
    this.result.add(resultItem);
    return this;
  }

   /**
   * Get result
   * @return result
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_RESULT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<SubmodelDescriptor> getResult() {
    return result;
  }


  @JsonProperty(JSON_PROPERTY_RESULT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setResult(List<SubmodelDescriptor> result) {
    this.result = result;
  }


  /**
   * Return true if this GetSubmodelDescriptorsResult object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetSubmodelDescriptorsResult getSubmodelDescriptorsResult = (GetSubmodelDescriptorsResult) o;
    return Objects.equals(this.pagingMetadata, getSubmodelDescriptorsResult.pagingMetadata) &&
        Objects.equals(this.result, getSubmodelDescriptorsResult.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pagingMetadata, result);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetSubmodelDescriptorsResult {\n");
    sb.append("    pagingMetadata: ").append(toIndentedString(pagingMetadata)).append("\n");
    sb.append("    result: ").append(toIndentedString(result)).append("\n");
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

    // add `paging_metadata` to the URL query string
    if (getPagingMetadata() != null) {
      joiner.add(getPagingMetadata().toUrlQueryString(prefix + "paging_metadata" + suffix));
    }

    // add `result` to the URL query string
    if (getResult() != null) {
      for (int i = 0; i < getResult().size(); i++) {
        if (getResult().get(i) != null) {
          joiner.add(getResult().get(i).toUrlQueryString(String.format("%sresult%s%s", prefix, suffix,
          "".equals(suffix) ? "" : String.format("%s%d%s", containerPrefix, i, containerSuffix))));
        }
      }
    }

    return joiner.toString();
  }
}

