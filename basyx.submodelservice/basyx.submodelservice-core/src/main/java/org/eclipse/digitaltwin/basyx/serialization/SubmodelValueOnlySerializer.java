package org.eclipse.digitaltwin.basyx.serialization;

import java.io.IOException;
import java.util.Map;

import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializes a {@link SubmodelValueOnly}
 * 
 * @author damm
 *
 */
public class SubmodelValueOnlySerializer extends JsonSerializer<SubmodelValueOnly> {
	@Override
	public void serialize(SubmodelValueOnly values, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartObject();
		Map<String, SubmodelElementValue> vMap = values.getValuesOnlyMap();

		for (String id : vMap.keySet()) {
			gen.writePOJOField(id, vMap.get(id));
		}
		gen.writeEndObject();
	}
}
