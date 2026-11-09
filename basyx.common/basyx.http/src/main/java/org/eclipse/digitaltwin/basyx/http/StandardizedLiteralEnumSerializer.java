package org.eclipse.digitaltwin.basyx.http;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
import org.eclipse.digitaltwin.basyx.core.StandardizedLiteralEnum;

public class StandardizedLiteralEnumSerializer<T extends StandardizedLiteralEnum> extends ValueSerializer<T> {

  @Override
  public void serialize(T value, JsonGenerator jgen, SerializationContext provider)
      {
    jgen.writeString(value.getValue());
  }
}
