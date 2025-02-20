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
package org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.java;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */

public class DynamicJavaClassLoader {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicJavaClassLoader.class);

	public void compileClasses(Path sourcesFolder, Path classFolder, List<Path> additionalClassPath)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		
		assertSourcePathExists(sourcesFolder);
		assertClassesPathExists(classFolder);

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		fileManager.setLocationFromPaths(StandardLocation.CLASS_OUTPUT, List.of(classFolder));
		
        List<Path> paths = new LinkedList<Path>(additionalClassPath);
		fileManager.getLocationAsPaths(StandardLocation.CLASS_PATH).forEach(paths::add);
        LOGGER.info("Compiling classes with class path: " + paths);
        
        
		fileManager.setLocationFromPaths(StandardLocation.CLASS_PATH, paths);
		
		List<Path> javaFiles = getJavaFiles(sourcesFolder);
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromPaths(javaFiles);
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, compilationUnits);
		boolean success = task.call();
		if (!success) {
			throw new IllegalStateException("Failed to compile java operation classes.");
		}
		LOGGER.info("Classes compiled successfully.");
	}

	private List<Path> getJavaFiles(Path sourcesFolder) throws IOException {
		return Files.walk(sourcesFolder, Integer.MAX_VALUE).filter(this::isJavaFile).collect(Collectors.toList());
	}

	private boolean isJavaFile(Path path) {
		return path.toString().endsWith(".java");
	}

	private void assertSourcePathExists(Path srcPath) {
		if (!Files.exists(srcPath)) {
			throw new IllegalStateException("Source folder '" + srcPath.toAbsolutePath() + "' does not exists.");
		}
	}

	private void assertClassesPathExists(Path clsPath) {
		if (!Files.exists(clsPath)) {
			try {
				Files.createDirectories(clsPath);
			} catch (IOException e) {
				throw new IllegalStateException(
						"Failed to create output directory '" + clsPath.toAbsolutePath() + "'.");
			}
		}
	}

	public URLClassLoader loadClasses(Path classFolder, List<Path> additionalClassPath) throws IOException {
		List<URL> urls = new ArrayList<>();
		if (classFolder != null) {
			urls.add(safeToUrl(classFolder.toUri()));
		}
		if (additionalClassPath != null) {
			for (Path eachAddtionalClassPath : additionalClassPath) {
				urls.add(safeToUrl(eachAddtionalClassPath.toUri()));
			}
		}
		return URLClassLoader.newInstance(urls.toArray(new URL[0]), DynamicJavaClassLoader.class.getClassLoader());
	}

	public URL safeToUrl(URI uri) {
		try {
			return uri.toURL();
		} catch (MalformedURLException e) {
			throw new IllegalStateException("Failed to convert uri to url", e);
		}
	}
}