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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.digitaltwin.basyx.submodelservice.component.java.DynamicJavaClassLoader;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */
public class DynamicClassLoadingTest {

	private static final String SQUARE_OPERATION_CLS_NAME = "SquareOperation";
	private static final String HELLO_OPERATION_CLS_NAME = "HelloOperation";

	@Test
	public void testCompilationInvokableFactory() throws Exception {
		DynamicJavaClassLoader loader = new DynamicJavaClassLoader();
		Path sources = Path.of("example", "sources");
		Path classes = Path.of("target", "dynamic-loading", "classes");
		List<Path> pathJars = List.of(Path.of("example/jars/HelloWorld.jar"));
		
		loader.compileClasses(sources, classes, pathJars);

		Assert.assertTrue(Files.exists(classes));
		try (URLClassLoader urlLoader = loader.loadClasses(classes, pathJars)) {
			Class<?> cls = urlLoader.loadClass(SQUARE_OPERATION_CLS_NAME);
			Assert.assertEquals(SQUARE_OPERATION_CLS_NAME, cls.getName());
			cls = urlLoader.loadClass(HELLO_OPERATION_CLS_NAME);
			Assert.assertEquals(HELLO_OPERATION_CLS_NAME, cls.getName());
		} catch (ClassNotFoundException e) {
			Assert.fail("SquareOperation class could not be loaded");
		}
	}
	
	@Test(expected = IllegalStateException.class)
	public void testOnUnknownSrcFolderIllegalStateException() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		DynamicJavaClassLoader loader = new DynamicJavaClassLoader();
		Path sources = Path.of("example", "unknown");
		Path classes = Path.of("target", "dynamic-loading", "classes");
		List<Path> pathJars = List.of(Path.of("example/jars/HelloWorld.jar"));
		loader.compileClasses(sources, classes, pathJars);
	}
}
