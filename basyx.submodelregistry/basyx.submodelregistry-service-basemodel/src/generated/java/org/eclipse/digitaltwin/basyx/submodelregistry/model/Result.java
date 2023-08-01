package org.eclipse.digitaltwin.basyx.submodelregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.Message;
import org.openapitools.jackson.nullable.JsonNullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Result
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T16:44:41.955927700+02:00[Europe/Berlin]")
public class Result implements Serializable {

  private static final long serialVersionUID = 1L;

  @Valid
  private List<@Valid Message> messages;

  public Result messages(List<@Valid Message> messages) {
    this.messages = messages;
    return this;
  }

  public Result addMessagesItem(Message messagesItem) {
    if (this.messages == null) {
      this.messages = new ArrayList<>();
    }
    this.messages.add(messagesItem);
    return this;
  }

  /**
   * Get messages
   * @return messages
  */
  @Valid 
  @Schema(name = "messages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("messages")
  public List<@Valid Message> getMessages() {
    return messages;
  }

  public void setMessages(List<@Valid Message> messages) {
    this.messages = messages;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Result result = (Result) o;
    return Objects.equals(this.messages, result.messages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messages);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Result {\n");
    sb.append("    messages: ").append(toIndentedString(messages)).append("\n");
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

