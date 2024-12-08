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

import org.eclipse.digitaltwin.aas4j.v3.model.OperationRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationResult;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.operationdispatching.java.TestOperationValues;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */
public abstract class AbstractSubmodelServiceIntegrationTest {

    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void testSquareOperation() {
        String url = "http://localhost:" + port + "/submodel/submodel-elements/SquareOperation/invoke";
        OperationRequest request = TestOperationValues.requestForInt(5);        
        
        ResponseEntity<OperationResult> response = restTemplate.postForEntity(url, request, OperationResult.class);
        
        OperationResult expected = TestOperationValues.resultForInt(25);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(expected, response.getBody());
    }
    
    @Test
    public void testAddOperation() {
        String url = "http://localhost:" + port + "/submodel/submodel-elements/BasicOperations.AddOperation/invoke";
        OperationRequest request = TestOperationValues.requestForInt(5,7);        
        
        ResponseEntity<OperationResult> response = restTemplate.postForEntity(url, request, OperationResult.class);
        
        OperationResult expected = TestOperationValues.resultForInt(12);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(expected, response.getBody());
    }
    
    @Test
    public void testDefaultOperation() {
        String url = "http://localhost:" + port + "/submodel/submodel-elements/BasicOperations.UnassignedOperation/invoke";
        OperationRequest request = TestOperationValues.requestForInt(5);        
        
        ResponseEntity<OperationResult> response = restTemplate.postForEntity(url, request, OperationResult.class);
        // no operation handling assigned
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    public void testGetPropertyValue() {
        String url = "http://localhost:" + port + "/submodel/submodel-elements/test/$value";                
        ResponseEntity<SubmodelElementValue> response = restTemplate.getForEntity(url, SubmodelElementValue.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}