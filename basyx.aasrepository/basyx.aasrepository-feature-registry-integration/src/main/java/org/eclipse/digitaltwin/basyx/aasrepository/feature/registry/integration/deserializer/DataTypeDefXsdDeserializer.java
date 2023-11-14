package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.deserializer;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.KeyTypes;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.ReferenceTypes;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class DataTypeDefXsdDeserializer extends JsonDeserializer<DataTypeDefXsd> {

	@Override
	public DataTypeDefXsd deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		try {
			JsonNode node = p.getCodec().readTree(p);

			String value = node.asText();

			String stringWithoutUnderscore = StringUtils.remove(value, '_');

			return DataTypeDefXsd.valueOf(DataTypeDefXsd.class, stringWithoutUnderscore);

			// return ReferenceTypes.fromValue(value);
			// return ReferenceTypes.EXTERNALREFERENCE;

		} catch (Exception e) {
			throw new RuntimeException("Unable to deserialize the ReferenceTypes Enum");
		}
	}

}
