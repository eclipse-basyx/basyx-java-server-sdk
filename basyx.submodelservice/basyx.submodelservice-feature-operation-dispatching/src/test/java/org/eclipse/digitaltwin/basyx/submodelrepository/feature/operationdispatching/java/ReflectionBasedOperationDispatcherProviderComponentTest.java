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

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.OperationDispatcherMapping;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.OperationDispatchingSubmodelServiceFeature;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.execution.OperationExecutor;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.java.JavaInvokableDefinition;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.java.ReflectionBasedOperationDispatcherProviderComponent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { ReflectionBasedOperationDispatcherProviderComponent.class })
@EnableConfigurationProperties(value = { JavaInvokableDefinition.class, OperationDispatcherMapping.class })
@TestPropertySource(properties = { OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".enabled=true",
		OperationDispatchingSubmodelServiceFeature.FEATURENAME
				+ ".defaultMapping=org.eclipse.digitaltwin.basyx.submodelrepository.feature.operationdispatching.java.ops.MockDefaultOperation",
		OperationDispatchingSubmodelServiceFeature.FEATURENAME
				+ ".mappings[BaseOperations.SquareOperation]=SquareOperation",
		OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".mappings[HelloOperation]=HelloOperation",
		OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".java.sources=test/sources",
		OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".java.classes=target/dynamic-loading/classes",
		OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".java.additionalClasspath=../basyx.submodelservice.component/example/jars/HelloWorld.jar" })
/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */
public class ReflectionBasedOperationDispatcherProviderComponentTest {

	@Autowired
	private ReflectionBasedOperationDispatcherProviderComponent component;

	@Test
	public void testDefaultMapping() {
		OperationExecutor executor = component.getOperationExecutor("Unknown");
		OperationVariable[] vars = executor.invoke(null, null, null);
		Assert.assertEquals(1, vars.length);
		Property prop = (Property) vars[0].getValue();
		Assert.assertEquals("default", prop.getValue());
	}

	@Test
	public void testSquareOperation() {
		OperationExecutor executor = component.getOperationExecutor("BaseOperations.SquareOperation");
		OperationVariable in = TestOperationValues.toOperationVariable(TestOperationValues.toIntProperty(3));
		OperationVariable[] vars = executor.invoke("BaseOperations.SquareOperation", null, new OperationVariable[] { in });
		Assert.assertEquals(1, vars.length);
		Property prop = (Property) vars[0].getValue();
		Assert.assertEquals("9", prop.getValue());
	}
}