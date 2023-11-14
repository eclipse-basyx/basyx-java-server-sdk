package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.deserializer;

import java.io.IOException;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.KeyTypes;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class KeyTypeDeserializer extends JsonDeserializer<KeyTypes> {

	@Override
	public KeyTypes deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		try {
			JsonNode node = p.getCodec().readTree(p);
			
			String value = node.asText();
			
			return KeyTypes.valueOf(KeyTypes.class, value);
			
		} catch (Exception e) {
			throw new RuntimeException("Unable to deserialize the KeyTypes Enum");
		}
	}

}
