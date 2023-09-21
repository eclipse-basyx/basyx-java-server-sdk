/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.submodelregistry.service.configuration;

import org.eclipse.digitaltwin.basyx.authorization.rbac.IRbacStorage;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleSet;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleSetDeserializer;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.CursorEncodingRegistryStorage;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.memory.InMemorySubmodelRegistryStorage;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.memory.ThreadSafeSubmodelRegistryStorageDecorator;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization.rbac.InMemoryAuthorizationRbacStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Predicate;

@Configuration
public class InMemorySubmodelStorageConfiguration {

	@Bean
	@ConditionalOnProperty(prefix = "registry", name = "type", havingValue = "inMemory")
	public SubmodelRegistryStorage<?> storage() {
		return new ThreadSafeSubmodelRegistryStorageDecorator<>(new CursorEncodingRegistryStorage<>(new InMemorySubmodelRegistryStorage()));
	}

	@Bean
	@ConditionalOnProperty(prefix = "registry", name = "type", havingValue = "inMemory")
	public IRbacStorage<Predicate<RbacRule>> rbacStorage() {
		return new InMemoryAuthorizationRbacStorage(getRbacRuleSet());
	}

	final static String RBAC_RULES_FILE_PATH = "/rbac_rules.json";

	private RbacRuleSet getRbacRuleSet() {
		try {
			return new RbacRuleSetDeserializer().fromFile(RBAC_RULES_FILE_PATH);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
