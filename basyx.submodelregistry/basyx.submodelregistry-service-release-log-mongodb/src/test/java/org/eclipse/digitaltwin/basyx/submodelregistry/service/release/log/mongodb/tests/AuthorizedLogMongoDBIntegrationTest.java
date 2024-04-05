/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.submodelregistry.service.release.log.mongodb.tests;

import org.eclipse.digitaltwin.basyx.submodelregistry.service.tests.integration.AuthorizedSubmodelRegistryTestSuite;
import org.springframework.test.context.TestPropertySource;

/**
 * Tests for Authorization with Logging events and MongoDB storage
 *
 * @author danish
 */
@TestPropertySource(properties = {"spring.profiles.active=logEvents,mongoDbStorage", "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9096/realms/BaSyx", "basyx.feature.authorization.enabled=true", "basyx.feature.authorization.type=rbac", "basyx.feature.authorization.jwtBearerTokenProvider=keycloak", "basyx.feature.authorization.rbac.file=classpath:rbac_rules.json", "spring.data.mongodb.database=submodelregistry", "spring.data.mongodb.uri=mongodb://mongoAdmin:mongoPassword@localhost:27017/"})
public class AuthorizedLogMongoDBIntegrationTest extends AuthorizedSubmodelRegistryTestSuite {

}