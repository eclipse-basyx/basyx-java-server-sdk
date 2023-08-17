package org.eclipse.digitaltwin.basyx.submodelservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OperationRequest
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-08-10T13:45:22.063686165Z[GMT]")


public class OperationRequest   {
  @JsonProperty("inoutputArguments")
  @Valid
  private List<OperationVariable> inoutputArguments = null;

  @JsonProperty("inputArguments")
  @Valid
  private List<OperationVariable> inputArguments = null;

  @JsonProperty("clientTimeoutDuration")
  private String clientTimeoutDuration = null;

  public OperationRequest inoutputArguments(List<OperationVariable> inoutputArguments) {
    this.inoutputArguments = inoutputArguments;
    return this;
  }

  public OperationRequest addInoutputArgumentsItem(OperationVariable inoutputArgumentsItem) {
    if (this.inoutputArguments == null) {
      this.inoutputArguments = new ArrayList<OperationVariable>();
    }
    this.inoutputArguments.add(inoutputArgumentsItem);
    return this;
  }

  /**
   * Get inoutputArguments
   * @return inoutputArguments
   **/
  @Schema(description = "")
      @Valid
    public List<OperationVariable> getInoutputArguments() {
    return inoutputArguments;
  }

  public void setInoutputArguments(List<OperationVariable> inoutputArguments) {
    this.inoutputArguments = inoutputArguments;
  }

  public OperationRequest inputArguments(List<OperationVariable> inputArguments) {
    this.inputArguments = inputArguments;
    return this;
  }

  public OperationRequest addInputArgumentsItem(OperationVariable inputArgumentsItem) {
    if (this.inputArguments == null) {
      this.inputArguments = new ArrayList<OperationVariable>();
    }
    this.inputArguments.add(inputArgumentsItem);
    return this;
  }

  /**
   * Get inputArguments
   * @return inputArguments
   **/
  @Schema(description = "")
      @Valid
    public List<OperationVariable> getInputArguments() {
    return inputArguments;
  }

  public void setInputArguments(List<OperationVariable> inputArguments) {
    this.inputArguments = inputArguments;
  }

  public OperationRequest clientTimeoutDuration(String clientTimeoutDuration) {
    this.clientTimeoutDuration = clientTimeoutDuration;
    return this;
  }

  /**
   * Get clientTimeoutDuration
   * @return clientTimeoutDuration
   **/
  @Schema(description = "")
  
  @Pattern(regexp="^(-?)P(?=.)((\\d+)Y)?((\\d+)M)?((\\d+)D)?(T(?=.)((\\d+)H)?((\\d+)M)?(\\d*(\\.\\d+)?S)?)?$")   public String getClientTimeoutDuration() {
    return clientTimeoutDuration;
  }

  public void setClientTimeoutDuration(String clientTimeoutDuration) {
    this.clientTimeoutDuration = clientTimeoutDuration;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OperationRequest operationRequest = (OperationRequest) o;
    return Objects.equals(this.inoutputArguments, operationRequest.inoutputArguments) &&
        Objects.equals(this.inputArguments, operationRequest.inputArguments) &&
        Objects.equals(this.clientTimeoutDuration, operationRequest.clientTimeoutDuration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(inoutputArguments, inputArguments, clientTimeoutDuration);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OperationRequest {\n");
    
    sb.append("    inoutputArguments: ").append(toIndentedString(inoutputArguments)).append("\n");
    sb.append("    inputArguments: ").append(toIndentedString(inputArguments)).append("\n");
    sb.append("    clientTimeoutDuration: ").append(toIndentedString(clientTimeoutDuration)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
