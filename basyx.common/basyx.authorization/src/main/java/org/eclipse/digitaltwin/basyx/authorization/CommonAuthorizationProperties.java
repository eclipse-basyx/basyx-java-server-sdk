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

package org.eclipse.digitaltwin.basyx.authorization;

/**
 * An utility class for defining all the properties of authorization
 * 
 * @author danish
 */
public class CommonAuthorizationProperties {

	private CommonAuthorizationProperties() {
		throw new IllegalStateException("Utility class");
	}

	public static final String PROPERTIES_PREFIX = "basyx.feature.authorization";
	public static final String ENABLED_PROPERTY_KEY = PROPERTIES_PREFIX + ".enabled";
	public static final String TYPE_PROPERTY_KEY = PROPERTIES_PREFIX + ".type";
	public static final String RBAC_FILE_PROPERTY_KEY = PROPERTIES_PREFIX + ".rbac.file";
	public static final String JWT_BEARER_TOKEN_PROVIDER_PROPERTY_KEY = PROPERTIES_PREFIX + ".jwtBearerTokenProvider";
	public static final String RULES_BACKEND_TYPE = PROPERTIES_PREFIX + ".rules.backend";
	public static final String RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION = RULES_BACKEND_TYPE + ".submodel.authorization";
	public static final String RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION_SUBMODEL_ENDPOINT = RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION + ".endpoint";
	public static final String RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION_TOKEN_ENDPOINT = RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION + ".token-endpoint";
	public static final String RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION_GRANT_TYPE = RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION + ".grant-type";
	public static final String RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION_CLIENT_ID = RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION + ".client-id";
	public static final String RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION_CLIENT_SECRET = RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION + ".client-secret";
	public static final String RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION_USERNAME = RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION + ".username";
	public static final String RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION_PASSWORD = RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION + ".password";
	public static final String RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION_SCOPES = RULES_BACKEND_TYPE_SUBMODEL_AUTHORIZATION + ".scopes";
}
