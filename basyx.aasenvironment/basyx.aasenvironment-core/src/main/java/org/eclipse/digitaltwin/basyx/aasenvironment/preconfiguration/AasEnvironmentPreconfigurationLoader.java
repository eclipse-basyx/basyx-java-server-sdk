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

package org.eclipse.digitaltwin.basyx.aasenvironment.preconfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.basyx.aasenvironment.AasEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.CompleteEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.CompleteEnvironment.EnvironmentType;
import org.eclipse.digitaltwin.basyx.authorization.CommonAuthorizationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.integration.file.RecursiveDirectoryScanner;
import org.springframework.stereotype.Component;

/**
 * Loader for AAS environment pre-configuration
 *
 * @author fried, mateusmolina, despen, witt, jungjan, danish
 *
 */
public class AasEnvironmentPreconfigurationLoader {

	private Logger logger = LoggerFactory.getLogger(AasEnvironmentPreconfigurationLoader.class);

	@Value("${basyx.environment:#{null}}")
	private List<String> pathsToLoad;

	private ResourceLoader resourceLoader;

	public AasEnvironmentPreconfigurationLoader(ResourceLoader resourceLoader, List<String> pathsToLoad) {
		this.resourceLoader = resourceLoader;
		this.pathsToLoad = pathsToLoad;
	}

	public boolean shouldLoadPreconfiguredEnvironment() {
		return pathsToLoad != null;
	}

	public void loadPreconfiguredEnvironments(AasEnvironment aasEnvironment)
			throws IOException, DeserializationException, InvalidFormatException {
		List<File> files = scanForEnvironments(pathsToLoad);

		if (files.isEmpty())
			return;

		int filesCount = files.size();
		int currenFileIndex = 0;

		for (File file : files) {
			logLoadingProcess(currenFileIndex++, filesCount, file.getName());
			aasEnvironment.loadEnvironment(CompleteEnvironment.fromFile(file));
		}
	}

	private List<File> scanForEnvironments(List<String> pathsToLoad) throws IOException {
		logger.info("Scanning for preconfigured AAS Environments");

		List<File> files = resolveFiles(pathsToLoad);

		logger.info("Found " + files.size() + " preconfigured AAS environments");

		return files;
	}

	private List<File> resolveFiles(List<String> paths) throws IOException {
		ArrayList<File> files = new ArrayList<>();

		for (String path : paths) {
			resolvePathAndAddFilesToList(files, path);
		}
		return files;
	}

	private void resolvePathAndAddFilesToList(ArrayList<File> files, String path) throws IOException {
		if (!getFile(path).isFile()) {
			List<File> filesFromDir = extractFilesToLoadFromEnvironmentDirectory(path);
			files.addAll(filesFromDir);
		} else {
			files.add(getFile(path));
		}
	}

	private File getFile(String filePath) throws IOException {
		return resourceLoader.getResource(filePath)
				.getFile();
	}


	private List<File> extractFilesToLoadFromEnvironmentDirectory(String directoryToLoad) throws IllegalArgumentException, IOException {
		File rootDirectory = getFile(directoryToLoad);
		RecursiveDirectoryScanner directoryScanner = new RecursiveDirectoryScanner();

		List<File> potentialEnvironments = directoryScanner.listFiles(rootDirectory);
		return potentialEnvironments.stream()
				.filter(file -> EnvironmentType.getFromFilePath(file.getPath()) != null)
				.collect(Collectors.toList());
	}

	private void logLoadingProcess(int current, int overall, String filename) {
		logger.info("Loading AAS Environment ({}/{}) from file '{}'", current, overall, filename);
	}


}