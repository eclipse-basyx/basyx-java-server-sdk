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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.digitaltwin.basyx.core.exceptions.MissingAuthorizationConfigurationException;
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
		this.objectMapper = objectMapper;
		this.rbacJsonFilePath = filePath;
		this.resourceLoader = resourceLoader;
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
		return objectMapper.readValue(getFile(rbacJsonFilePath), new TypeReference<HashMap<String, RbacRule>>() {
		});
	}

	private File getFile(String filePath) {

		try {
			return resourceLoader.getResource(filePath).getFile();
		} catch (IOException e) {
			throw new MissingAuthorizationConfigurationException(filePath);
		}

	}
}
