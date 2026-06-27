/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.authorization.rbac;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.digitaltwin.basyx.core.exceptions.MissingAuthorizationConfigurationException;
import org.reflections.Reflections;
import org.springframework.core.io.ResourceLoader;

/**
 * Initializes {@link RbacRule} from the resource
 * 
 * @author danish
 */
public class RbacRuleInitializer {

	private String rbacJsonFilePath;

	private final ObjectMapper objectMapper;

	private ResourceLoader resourceLoader;

	public RbacRuleInitializer(ObjectMapper objectMapper, String filePath, ResourceLoader resourceLoader) {
		this.objectMapper = objectMapper.copy()
				.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		this.rbacJsonFilePath = filePath;
		this.resourceLoader = resourceLoader;
		registerTargetInformationSubtypes();
	}

	/**
	 * Provides the Map of {@link RbacRule} from the resource
	 * 
	 * It auto-generates the key based on hash of combination of role, {@link Action}, and the concrete {@link TargetInformation}
	 * class.
	 * 
	 * @return map of rbac rules
	 * @throws IOException
	 */
	public HashMap<String, RbacRule> deserialize() throws IOException {
		JsonNode rules = objectMapper.readTree(getFile(rbacJsonFilePath));

		if (rules.isArray()) {
			return mapByGeneratedKey(objectMapper.convertValue(rules, new TypeReference<List<RbacRule>>() {
			}));
		}

		if (rules.isObject()) {
			Map<String, RbacRule> mappedRules = objectMapper.convertValue(rules, new TypeReference<Map<String, RbacRule>>() {
			});
			return new HashMap<>(mappedRules);
		}

		throw new IOException("Unsupported RBAC rules format: " + rules.getNodeType());
	}

	private HashMap<String, RbacRule> mapByGeneratedKey(List<RbacRule> rbacRules) {
		HashMap<String, RbacRule> result = new HashMap<>();

		for (RbacRule rule : rbacRules) {
			for (Action action : rule.getAction()) {
				RbacRule singleActionRule = new RbacRule(rule.getRole(), List.of(action), rule.getTargetInformation());
				result.put(createKey(singleActionRule), singleActionRule);
			}
		}

		return result;
	}

	private String createKey(RbacRule rbacRule) {
		return RbacRuleKeyGenerator.generateKey(rbacRule.getRole(), rbacRule.getAction().get(0).toString(), rbacRule.getTargetInformation().getClass().getName());
	}

	private void registerTargetInformationSubtypes() {
		Reflections reflections = new Reflections("org.eclipse.digitaltwin.basyx");
		Set<Class<?>> subtypes = reflections.getTypesAnnotatedWith(TargetInformationSubtype.class);

		subtypes.stream().map(this::createNamedType).filter(Objects::nonNull).forEach(objectMapper::registerSubtypes);
	}

	private NamedType createNamedType(Class<?> subType) {
		TargetInformationSubtype annotation = subType.getAnnotation(TargetInformationSubtype.class);

		return (annotation != null) ? new NamedType(subType, annotation.getValue()) : null;
	}

	private File getFile(String filePath) {

		try {
			return resourceLoader.getResource(filePath).getFile();
		} catch (IOException e) {
			throw new MissingAuthorizationConfigurationException(filePath);
		}

	}
}
