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
package org.eclipse.digitaltwin.basyx.aasregistry.service.tests;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.service.events.RegistryEvent;
import org.junit.rules.TestName;
import org.junit.runner.Description;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class TestResourcesLoader extends TestName {

	private static final String JSON_FILE_ENDING = ".json";

	private String packageName;
	
	private ObjectMapper mapper;

	public TestResourcesLoader(String packageName, ObjectMapper mapper) {
		this.packageName = packageName;
		this.mapper = mapper;
	}

	public TestResourcesLoader() {
		this(null, new ObjectMapper());
	}

	@Override
	protected void starting(Description d) {
		super.starting(d);
		if (packageName == null) {
			packageName = d.getTestClass().getPackageName();
		}
	}

	public <T> List<T> loadRepositoryDefinition(Class<T> cls) throws IOException {
		String path = getTestRepositoryPath();
		return loadValue(path, mapper.readerForListOf(cls));
	}

	public <T> List<T> loadList(Class<T> cls) throws IOException {
		return loadList(cls, null);
	}

	public <T> List<T> loadList(Class<T> cls, String suffix) throws IOException {
		
		return load(mapper.readerForListOf(cls), suffix != null ? "_" + suffix : "");
	}
	
	public <T> T load(Class<T> cls) throws IOException {
		return load(cls, null);
	}

	public <T> T load(Class<T> cls, String suffix) throws IOException {
		return load(mapper.readerFor(cls), suffix != null ? "_" + suffix : "");
	}
	
	public List<RegistryEvent> loadEvents() throws IOException {
		return load(mapper.readerForListOf(RegistryEvent.class), "_events");
	}

	private String getTestRepositoryPath() {
		String repoPath = getMethodName() + "_repo" + JSON_FILE_ENDING;
		String basedOnTestClass = basedOnPackageName(repoPath);
		if (getClass().getResource(basedOnTestClass) != null) {
			return repoPath;
		}
		return "default_repository.json";
	}

	private String basedOnPackageName(String relativePath) {
		return "/" + packageName.replace('.', '/') + "/" + relativePath;
	}

	private <T> T load(ObjectReader reader, String suffix) throws IOException {
		String path = getMethodName() + suffix + JSON_FILE_ENDING;
		return loadValue(path, reader);
	}

	private <T> T loadValue(String path, ObjectReader reader) throws IOException {
		path = basedOnPackageName(path);
		try (InputStream in = getClass().getResourceAsStream(path); BufferedInputStream bIn = new BufferedInputStream(in)) {
			return reader.readValue(bIn);
		}
	}
	
	public String loadJsonAsString() throws IOException {
		String suffixPath = getMethodName() + JSON_FILE_ENDING;
		String path = basedOnPackageName(suffixPath);		
		try (InputStream in = getClass().getResourceAsStream(path); BufferedInputStream bIn = new BufferedInputStream(in)) {
			byte[] allBytes = bIn.readAllBytes();
			return new String(allBytes, StandardCharsets.UTF_8);
		}
	}
}
