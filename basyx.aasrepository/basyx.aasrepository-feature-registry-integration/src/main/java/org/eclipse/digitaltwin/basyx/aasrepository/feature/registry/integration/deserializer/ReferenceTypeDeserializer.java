package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.deserializer;

import java.io.IOException;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.KeyTypes;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ReferenceTypes;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class ReferenceTypeDeserializer extends JsonDeserializer<ReferenceTypes> {

	@Override
	public ReferenceTypes deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		try {
			JsonNode node = p.getCodec().readTree(p);
			
			String value = node.asText();
			
//			return ReferenceTypes.valueOf(ReferenceTypes.class, value);
			return ReferenceTypes.EXTERNALREFERENCE;
			
		} catch (Exception e) {
			throw new RuntimeException("Unable to deserialize the ReferenceTypes Enum");
		}
	}

}
