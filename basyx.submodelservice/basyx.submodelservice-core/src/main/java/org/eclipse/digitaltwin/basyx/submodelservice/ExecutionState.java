package org.eclipse.digitaltwin.basyx.submodelservice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets ExecutionState
 */
public enum ExecutionState {
  INITIATED("Initiated"),
    RUNNING("Running"),
    COMPLETED("Completed"),
    CANCELED("Canceled"),
    FAILED("Failed"),
    TIMEOUT("Timeout");

  private String value;

  ExecutionState(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static ExecutionState fromValue(String text) {
    for (ExecutionState b : ExecutionState.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
