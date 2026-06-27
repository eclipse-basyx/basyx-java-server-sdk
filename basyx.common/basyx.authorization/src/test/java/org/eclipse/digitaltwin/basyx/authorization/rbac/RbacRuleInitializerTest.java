/*******************************************************************************
 * Copyright (C) 2026 the Eclipse BaSyx Authors
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.io.DefaultResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RbacRuleInitializerTest {
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void deserializesArrayRulesWithGeneratedKeys() throws Exception {
		File rulesFile = temporaryFolder.newFile("rbac_rules.json");
		Files.writeString(rulesFile.toPath(), """
				[
				  {
				    "role": "reader",
				    "action": "READ",
				    "targetInformation": {
				      "@type": "test-target",
				      "ids": "specific-id"
				    }
				  },
				  {
				    "role": "admin",
				    "action": ["CREATE", "UPDATE"],
				    "targetInformation": {
				      "@type": "test-target",
				      "ids": "*"
				    }
				  }
				]
				""", StandardCharsets.UTF_8);

		HashMap<String, RbacRule> rules = new RbacRuleInitializer(new ObjectMapper(), rulesFile.toURI().toString(), new DefaultResourceLoader()).deserialize();

		String readerKey = RbacRuleKeyGenerator.generateKey("reader", Action.READ.toString(), TestTargetInformation.class.getName());
		String adminCreateKey = RbacRuleKeyGenerator.generateKey("admin", Action.CREATE.toString(), TestTargetInformation.class.getName());
		String adminUpdateKey = RbacRuleKeyGenerator.generateKey("admin", Action.UPDATE.toString(), TestTargetInformation.class.getName());

		assertTrue(rules.containsKey(readerKey));
		assertTrue(rules.containsKey(adminCreateKey));
		assertTrue(rules.containsKey(adminUpdateKey));
		assertEquals(List.of(Action.READ), rules.get(readerKey).getAction());
		assertTrue(rules.get(readerKey).getTargetInformation() instanceof TestTargetInformation);
		assertEquals(List.of("specific-id"), ((TestTargetInformation) rules.get(readerKey).getTargetInformation()).getIds());
	}

	@TargetInformationSubtype(getValue = "test-target")
	public static class TestTargetInformation implements TargetInformation {
		private List<String> ids;

		public List<String> getIds() {
			return ids;
		}

		public void setIds(List<String> ids) {
			this.ids = ids;
		}

		@Override
		public Map<String, Object> toMap() {
			return Map.of("ids", ids);
		}
	}
}
