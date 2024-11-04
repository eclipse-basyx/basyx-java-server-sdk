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

package org.eclipse.digitaltwin.basyx.core;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public final class RepositoryUrlHelper {

    private RepositoryUrlHelper() {
    }

	public static String createRepositoryUrl(String baseUrl, String additionalPath) {
		try {
            URI baseUri = new URI(baseUrl);

            String[] basePathSegments = baseUri.getPath().replaceAll("^/|/$", "").split("/");
            String[] additionalPathSegments = additionalPath != null ? additionalPath.replaceAll("^/|/$", "").split("/") : new String[0];

            StringBuilder fullPath = new StringBuilder();
            for (String segment : basePathSegments) {
                if (!segment.isEmpty()) {
                    fullPath.append("/").append(segment);
                }
            }
            for (String segment : additionalPathSegments) {
                if (!segment.isEmpty()) {
                    fullPath.append("/").append(segment);
                }
            }

            URI finalUri = new URI(baseUri.getScheme(), null, baseUri.getHost(), baseUri.getPort(), fullPath.toString(), null, null);

            return finalUri.toURL().toString();
		} catch (URISyntaxException | MalformedURLException e) {
			throw new RuntimeException("The Base URL or additional path is malformed.\n" + e.getMessage(), e); 
		}
	}

}
