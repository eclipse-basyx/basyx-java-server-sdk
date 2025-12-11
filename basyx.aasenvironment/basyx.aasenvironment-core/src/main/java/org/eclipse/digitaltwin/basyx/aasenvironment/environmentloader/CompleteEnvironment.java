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

package org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.AASXDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.InMemoryFile;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.basyx.core.exceptions.ZipBombException;

/**
 * Represents an environment and its relatedFiles
 *
 * @author mateusmolina
 *
 */
public class CompleteEnvironment {
	private final Environment environment;
	private final List<InMemoryFile> relatedFiles;

	public enum EnvironmentType {
		AASX, JSON, XML;

		public static EnvironmentType getFromMimeType(String mimeType) {
			switch (mimeType) {
			case "application/asset-administration-shell-package":
				return AASX;
			case "application/json":
				return JSON;
			case "application/xml":
				return XML;
			case "text/xml":
				return XML;
			default:
				return null;
			}
		}

		public static EnvironmentType getFromFilePath(String filePath) {
			if (filePath.endsWith(".json"))
				return JSON;
			if (filePath.endsWith(".aasx"))
				return AASX;
			if (filePath.endsWith(".xml"))
				return XML;
			return null;
		}

	}

	public CompleteEnvironment(Environment environment, List<InMemoryFile> relatedFiles) {
		this.environment = environment;
		this.relatedFiles = relatedFiles;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public List<InMemoryFile> getRelatedFiles() {
		return relatedFiles;
	}

	public static CompleteEnvironment fromFile(File file) throws DeserializationException, InvalidFormatException, IOException, ZipBombException {
		return fromInputStream(new FileInputStream(file), EnvironmentType.getFromFilePath(file.getPath()));
	}

	public static CompleteEnvironment fromInputStream(InputStream inputStream, EnvironmentType envType) throws DeserializationException, InvalidFormatException, IOException, ZipBombException {
		Environment environment = null;
		List<InMemoryFile> relatedFiles = null;

		if(envType == EnvironmentType.JSON) {
			JsonDeserializer deserializer = new JsonDeserializer();
			environment = deserializer.read(inputStream, Environment.class);
		}
		if(envType == EnvironmentType.XML) {
			XmlDeserializer deserializer = new XmlDeserializer();
			environment = deserializer.read(inputStream);
		}
		if(envType == EnvironmentType.AASX) {
			try {
				AASXDeserializer deserializer = new AASXDeserializer(inputStream);
				relatedFiles = deserializer.getRelatedFiles();
				environment = deserializer.read();
			} catch (Exception e) {
				if (e.getMessage().startsWith("Zip bomb")) {
					throw new ZipBombException(e);
				}
				throw e;
			}
		}

		return new CompleteEnvironment(environment, relatedFiles);
	}
}
