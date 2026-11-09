package org.eclipse.digitaltwin.basyx.http;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import org.eclipse.digitaltwin.basyx.core.StandardizedLiteralEnum;

public class StandardizedLiteralEnumDeserializer<T extends StandardizedLiteralEnum> extends ValueDeserializer<T> {

  private Class<T> clazz;

  public StandardizedLiteralEnumDeserializer(Class<T> t) {
    clazz = t;
  }

  @Override
  public T deserialize(JsonParser p, DeserializationContext ctxt) {
    String valueAsString = p.getValueAsString();
    return StandardizedLiteralEnumHelper.fromLiteral(clazz, valueAsString);
  }
}
