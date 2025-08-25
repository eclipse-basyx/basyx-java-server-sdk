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

package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.controllerAdvice;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.controllerAdvice.CommonControllerAdvice;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Result;
import org.eclipse.digitaltwin.basyx.core.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CommonControllerAdviceTest {

    private CommonControllerAdvice advice;

    @BeforeEach
    void setup() {
        advice = new CommonControllerAdvice();
    }

    @Test
    void testHandleValidationException() {
        log.info("Started unit test - testHandleValidationException()");
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getAllErrors()).thenReturn(Collections.emptyList());
        ResponseEntity<Result> response = advice.handleValidationException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        log.info("Successfully conducted unit test");
    }

    @Test
    void testHandleResponseStatusException() {
        log.info("Started unit test - testHandleResponseStatusException()");
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
        ResponseEntity<Result> response = advice.handleExceptions(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("404", response.getBody().getMessages().get(0).getCode());
        log.info("Successfully conducted unit test");
    }

    @Test
    void testHandleGenericException() {
        log.info("Started unit test - testHandleGenericException()");
        Exception ex = new Exception("generic error");
        ResponseEntity<Result> response = advice.handleExceptions(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        log.info("Successfully conducted unit test");
    }

    @Test
    void testElementDoesNotExist() {
        log.info("Started unit test - testElementDoesNotExist()");
        ResponseEntity<?> response = advice.handleElementNotFoundException(new ElementDoesNotExistException("id"), null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        log.info("Successfully conducted unit test");
    }

    @Test
    void testAssetLinkDoesNotExist() {
        log.info("Started unit test - testAssetLinkDoesNotExist()");
        ResponseEntity<?> response = advice.handleElementNotFoundException(new AssetLinkDoesNotExistException("id"), null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        log.info("Successfully conducted unit test");
    }

    @Test
    void testFileDoesNotExist() {
        log.info("Started unit test - testFileDoesNotExist()");
        ResponseEntity<?> response = advice.handleElementNotFoundException(new FileDoesNotExistException("file"), null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        log.info("Successfully conducted unit test");
    }

    @Test
    void testCollidingIdentifier() {
        log.info("Started unit test - testCollidingIdentifier()");
        ResponseEntity<?> response = advice.handleCollidingIdentifierException(new CollidingIdentifierException("id"), null);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        log.info("Successfully conducted unit test");
    }
}