package org.eclipse.digitaltwin.basyx.http;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.eclipse.digitaltwin.basyx.core.StandardizedLiteralEnum;

import java.io.IOException;

public class StandardizedLiteralEnumDeserializer<T extends StandardizedLiteralEnum> extends JsonDeserializer<T> {

  private Class<T> clazz;

  public StandardizedLiteralEnumDeserializer(Class<T> t) {
    clazz = t;
  }

  @Override
  public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    String valueAsString = p.getValueAsString();
    return StandardizedLiteralEnumHelper.fromLiteral(clazz, valueAsString);
  }
}
