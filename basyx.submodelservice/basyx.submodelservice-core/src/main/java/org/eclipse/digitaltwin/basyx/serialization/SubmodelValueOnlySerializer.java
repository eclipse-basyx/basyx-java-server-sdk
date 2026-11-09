package org.eclipse.digitaltwin.basyx.serialization;

import java.io.IOException;
import java.util.Map;

import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;

/**
 * Serializes a {@link SubmodelValueOnly}
 * 
 * @author damm
 *
 */
public class SubmodelValueOnlySerializer extends ValueSerializer<SubmodelValueOnly> {
	@Override
	public void serialize(SubmodelValueOnly values, JsonGenerator gen, SerializationContext serializers) {
		gen.writeStartObject();
		Map<String, SubmodelElementValue> vMap = values.getValuesOnlyMap();

		for (String id : vMap.keySet()) {
			gen.writePOJOProperty(id, vMap.get(id));
		}
		gen.writeEndObject();
	}
}
