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
public class CommonAuthorizationConfig {

	private CommonAuthorizationConfig() {
		throw new IllegalStateException("Utility class");
	}

	public static final String PROPERTIES_PREFIX = "basyx.feature.authorization";
	public static final String ENABLED_PROPERTY_KEY = PROPERTIES_PREFIX + ".enabled";
	public static final String TYPE_PROPERTY_KEY = PROPERTIES_PREFIX + ".type";
	public static final String RBAC_FILE_PROPERTY_KEY = PROPERTIES_PREFIX + ".rbac.file";
	public static final String JWT_BEARER_TOKEN_PROVIDER_PROPERTY_KEY = PROPERTIES_PREFIX + ".jwtBearerTokenProvider";
}
