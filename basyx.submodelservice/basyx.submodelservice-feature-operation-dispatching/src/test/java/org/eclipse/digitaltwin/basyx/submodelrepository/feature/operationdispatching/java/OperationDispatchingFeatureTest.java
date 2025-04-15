/*******************************************************************************
 * Copyright (C) 2025 DFKI GmbH (https://www.dfki.de/en/web)
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

import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelBackend;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.CrudSubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.CrudSubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.OperationDispatcherMapping;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.OperationDispatchingSubmodelServiceFeature;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { OperationDispatchingSubmodelServiceFeature.class,
		CrudSubmodelServiceFactory.class, InMemorySubmodelBackend.class, ReflectionBasedOperationDispatcherProviderComponent.class,
		InMemoryFileRepository.class, SubmodelServiceFactoryTestConfiguration.class })
@EnableConfigurationProperties(value = { JavaInvokableDefinition.class, OperationDispatcherMapping.class })
@TestPropertySource(properties = { "basyx.backend=InMemory",
		OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".enabled=true",
		OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".mappings[SquareOp]=SquareOperation",
		OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".java.sources=test/sources",
		OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".java.classes=test/classes",
		OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".java.additionalClasspath=../basyx.submodelservice.component/example/jars/HelloWorld.jar" })
/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */
public class OperationDispatchingFeatureTest {

	@Autowired
	private SubmodelServiceFactory factory;

	private Submodel createSubmodel() {
		Operation op = new DefaultOperation.Builder().idShort("SquareOp").build();
		Property prop = new DefaultProperty.Builder().idShort("Prop").value("5").valueType(DataTypeDefXsd.INTEGER)
				.build();
		Submodel sm = new DefaultSubmodel.Builder().id("http://submodel.org/123").idShort("123")
				.submodelElements(List.of(op, prop)).build();
		return sm;
	}

	@Test
	public void testOperationInvokation() {
		SubmodelService service = factory.create(createSubmodel());
		OperationVariable in = TestOperationValues.toOperationVariable(TestOperationValues.toIntProperty(7));
		OperationVariable[] out = service.invokeOperation("SquareOp", new OperationVariable[] { in });
		Assert.assertEquals(1, out.length);
		Property prop = (Property) out[0].getValue();
		Assert.assertEquals("49", prop.getValue());
	}

	@Test(expected = ResponseStatusException.class)
	public void testOperationInvokationOnProperty() {
		SubmodelService service = factory.create(createSubmodel());
		OperationVariable in = TestOperationValues.toOperationVariable(TestOperationValues.toIntProperty(7));
		service.invokeOperation("Prop", new OperationVariable[] { in });
	}
}