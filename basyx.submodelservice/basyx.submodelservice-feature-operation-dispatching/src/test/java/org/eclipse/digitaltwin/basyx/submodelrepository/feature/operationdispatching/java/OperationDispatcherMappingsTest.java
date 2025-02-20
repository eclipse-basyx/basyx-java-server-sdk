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

import java.util.Map;

import org.eclipse.digitaltwin.basyx.submodelrepository.feature.operationdispatching.java.ops.MockDefaultOperation;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.OperationDispatcherMapping;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.operationdispatching.OperationDispatchingSubmodelServiceFeature;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@EnableConfigurationProperties(OperationDispatcherMapping.class)
@TestPropertySource(properties = { 
		OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".enabled=true", 
		OperationDispatchingSubmodelServiceFeature.FEATURENAME
		+ ".defaultMapping=org.eclipse.digitaltwin.basyx.submodelrepository.feature.operationdispatching.java.ops.MockDefaultOperation" 
		, OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".mappings[AddOperation]=BasicOp.Add"
		, OperationDispatchingSubmodelServiceFeature.FEATURENAME + ".mappings[SquareOperation]=BasicOp.Square"})
/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */
public class OperationDispatcherMappingsTest {

	@Autowired
	private OperationDispatcherMapping mappingInfo;

	@Test
	public void testDefaultMappingIsSetCorrectly() {
		String defaultMapping = mappingInfo.getDefaultMapping();
		Assert.assertEquals(MockDefaultOperation.class.getName(), defaultMapping);
	}
	
	@Test
	public void testMappingIsSetCorrectly() {
		Map<String, String> mappings = mappingInfo.getMappings();
		Assert.assertEquals("BasicOp.Add", mappings.get("AddOperation"));
		Assert.assertEquals("BasicOp.Square", mappings.get("SquareOperation"));
	}
}
