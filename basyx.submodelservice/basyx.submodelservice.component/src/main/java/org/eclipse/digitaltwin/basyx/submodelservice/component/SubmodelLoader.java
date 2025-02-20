/*******************************************************************************
 * Copyright (C) 2024 DFKI GmbH (https://www.dfki.de/en/web)
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
 * 
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.submodelservice.component;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;

/**
 * @author gerhard sonnenberg (DFKI GmbH)
 */
class SubmodelLoader {

	private final String filePath;

	public SubmodelLoader(String filePath) {
		this.filePath = filePath;
	}

	public Submodel loadSubmodel() {
		try (InputStream in = loadForPath(); BufferedInputStream bIn = new BufferedInputStream(in)) {
			JsonDeserializer deserializer = new JsonDeserializer();
			return deserializer.read(bIn, DefaultSubmodel.class);
		} catch (IOException | DeserializationException e) {
			throw new IllegalStateException("Could not load submodel: " + filePath, e);
		}
	}

	private InputStream loadForPath() throws IOException {
		if (filePath.startsWith("classpath:")) {
			return loadFromClassPath();
		} 
		return loadFromUrl();
	}

	private InputStream loadFromUrl() throws IOException {
		try {
			URL url = new URL(filePath);
			return url.openStream();
		} catch (MalformedURLException e) {
			// not a valid url -> file fallback
			File file = new File(filePath);
			return new FileInputStream(file);
		}
	}

	private InputStream loadFromClassPath() throws IOException {
		String resourcePath = filePath.substring("classpath:".length());
		URL resourceUrl = getClass().getResource(resourcePath);
		if (resourceUrl == null) {
			throw new FileNotFoundException("Resource not found on classpath: " + resourcePath);
		}
		return resourceUrl.openStream();
	}
}
