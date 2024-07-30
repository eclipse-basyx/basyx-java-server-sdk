package org.eclipse.digitaltwin.basyx.authorization.rbac;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class RbacRuleDeserializer extends JsonDeserializer<HashMap<String, RbacRule>> {

	@Override
	public HashMap<String, RbacRule> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		ObjectMapper mapper = (ObjectMapper) p.getCodec();
		List<RbacRule> rbacRules = mapper.readValue(p, new TypeReference<List<RbacRule>>() {
		});
		HashMap<String, RbacRule> result = new HashMap<>();

		for (RbacRule rule : rbacRules) {
			rule.getAction().stream().map(action -> generateKey(rule.getRole(), action.toString(), rule.getTargetInformation().getClass().getName())).forEach(key -> result.put(key, rule));
		}

		return result;
	}

	private String generateKey(String role, String action, String clazz) {
		return String.valueOf((role + action + clazz).hashCode());
	}
}

