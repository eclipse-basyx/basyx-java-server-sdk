package org.eclipse.digitaltwin.basyx.http.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OperationResult
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-08-10T13:45:22.063686165Z[GMT]")


public class OperationResult extends BaseOperationResult  {
  @JsonProperty("inoutputArguments")
  @Valid
  private List<OperationVariable> inoutputArguments = null;

  @JsonProperty("outputArguments")
  @Valid
  private List<OperationVariable> outputArguments = null;

  public OperationResult inoutputArguments(List<OperationVariable> inoutputArguments) {
    this.inoutputArguments = inoutputArguments;
    return this;
  }

  public OperationResult addInoutputArgumentsItem(OperationVariable inoutputArgumentsItem) {
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

  public OperationResult outputArguments(List<OperationVariable> outputArguments) {
    this.outputArguments = outputArguments;
    return this;
  }

  public OperationResult addOutputArgumentsItem(OperationVariable outputArgumentsItem) {
    if (this.outputArguments == null) {
      this.outputArguments = new ArrayList<OperationVariable>();
    }
    this.outputArguments.add(outputArgumentsItem);
    return this;
  }

  /**
   * Get outputArguments
   * @return outputArguments
   **/
  @Schema(description = "")
      @Valid
    public List<OperationVariable> getOutputArguments() {
    return outputArguments;
  }

  public void setOutputArguments(List<OperationVariable> outputArguments) {
    this.outputArguments = outputArguments;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OperationResult operationResult = (OperationResult) o;
    return Objects.equals(this.inoutputArguments, operationResult.inoutputArguments) &&
        Objects.equals(this.outputArguments, operationResult.outputArguments) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(inoutputArguments, outputArguments, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OperationResult {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    inoutputArguments: ").append(toIndentedString(inoutputArguments)).append("\n");
    sb.append("    outputArguments: ").append(toIndentedString(outputArguments)).append("\n");
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
