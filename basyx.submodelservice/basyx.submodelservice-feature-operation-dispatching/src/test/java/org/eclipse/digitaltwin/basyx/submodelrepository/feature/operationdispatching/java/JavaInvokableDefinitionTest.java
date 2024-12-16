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
package org.eclipse.digitaltwin.basyx.submodelrepository.feature.operationdispatching.java;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.OperationDispatchingSubmodelServiceFeature;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.java.JavaInvokableDefinition;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@EnableConfigurationProperties(JavaInvokableDefinition.class)
@TestPropertySource(properties = {
		OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".enabled=true",
		OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".java.sources=" + JavaInvokableDefinitionTest.SOURCES,
		OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".java.classes=" + JavaInvokableDefinitionTest.CLASSES,
		OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".java.additionalClasspath=" + JavaInvokableDefinitionTest.LIBS })
/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */
public class JavaInvokableDefinitionTest {

	public static final String LIB1 = "lib1.jar";
	public static final String LIB2 = "lib2.jar";
	public static final String SOURCES = "sources";
	public static final String CLASSES = "classes";
	public static final String LIBS = LIB1 + "," + LIB2;

	@Autowired
	private JavaInvokableDefinition invokableDefintion;

	@Test
	public void testDefinitionSetCorrectly() {
		Path sourcesPath = invokableDefintion.getSources();
		Path classesPath = invokableDefintion.getClasses();
		List<Path> additionalClassPath = invokableDefintion.getAdditionalClasspath();

		Assert.assertEquals(SOURCES, sourcesPath.toString());
		Assert.assertEquals(CLASSES, classesPath.toString());
		Assert.assertEquals(List.of(LIB1, LIB2), additionalClassPath.stream().map(Path::toString).collect(Collectors.toList()));
	}

}
