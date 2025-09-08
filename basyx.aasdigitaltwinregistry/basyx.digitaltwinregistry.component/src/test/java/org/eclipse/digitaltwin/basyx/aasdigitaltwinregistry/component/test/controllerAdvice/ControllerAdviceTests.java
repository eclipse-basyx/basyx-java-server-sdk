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

package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.test.controllerAdvice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.controllerAdvice.GlobalExceptionHandler;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Message;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Result;
import org.eclipse.digitaltwin.basyx.core.exceptions.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.ObjectError;

@Slf4j
public class ControllerAdviceTests {

    private GlobalExceptionHandler advice;

    @Before
    public void setUp() {
        advice = new GlobalExceptionHandler();
    }

    @Test
    public void testHandleValidationException() {
        log.info("Started unit test - testHandleValidationException");
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null,
                new org.springframework.validation.BeanPropertyBindingResult(new Object(), "object"));
        ex.getBindingResult().addError(new ObjectError("field", "Invalid value"));

        ResponseEntity<Result> response = advice.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getMessages().size());
        Message msg = response.getBody().getMessages().get(0);
        assertEquals("EXCEPTION", msg.getMessageType().name());
        assertNotNull(msg.getTimestamp());
        log.info("Successfully conducted unit test");
    }

    @Test
    public void testHandleResponseStatusException() {
        log.info("Started unit test - testHandleResponseStatusException");
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found");
        ResponseEntity<Result> response = advice.handleExceptions(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("404", response.getBody().getMessages().get(0).getCode());
        log.info("Successfully conducted unit test");
    }

    @Test
    public void testHandleGenericException() {
        log.info("Started unit test - testHandleGenericException");
        Exception ex = new Exception("Internal error");
        ResponseEntity<Result> response = advice.handleExceptions(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("500", response.getBody().getMessages().get(0).getCode());
        assertEquals("Internal error", response.getBody().getMessages().get(0).getText());
        log.info("Successfully conducted unit test");
    }

    @Test
    public void testHandleElementDoesNotExistException() {
        log.info("Started unit test - testHandleElementDoesNotExistException");
        ResponseEntity<?> response = advice.handleElementNotFoundException(new ElementDoesNotExistException(""), null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        log.info("Successfully conducted unit test");
    }

    @Test
    public void testHandleAssetLinkDoesNotExistException() {
        log.info("Started unit test - testHandleAssetLinkDoesNotExistException");
        ResponseEntity<?> response = advice.handleElementNotFoundException(new AssetLinkDoesNotExistException(""), null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        log.info("Successfully conducted unit test");
    }

    @Test
    public void testHandleFileDoesNotExistException() {
        log.info("Started unit test - testHandleFileDoesNotExistException");
        ResponseEntity<?> response = advice.handleElementNotFoundException(new FileDoesNotExistException(""), null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        log.info("Successfully conducted unit test");
    }

    @Test
    public void testHandleCollidingIdentifierException() {
        log.info("Started unit test - testHandleFileDoesNotExistException");
        ResponseEntity<?> response = advice.handleCollidingIdentifierException(new CollidingIdentifierException(""), null);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        log.info("Successfully conducted unit test");
    }

    @Test
    public void testHandleMissingIdentifierException() {
        log.info("Started unit test - testHandleMissingIdentifierException");
        ResponseEntity<?> response = advice.handleMissingIdentifierException(new MissingIdentifierException(""), null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        log.info("Successfully conducted unit test");
    }

    @Test
    public void testHandleCollidingAssetLinkException() {
        log.info("Started unit test - testHandleCollidingAssetLinkException");
        ResponseEntity<?> response = advice.handleCollidingIdentifierException(new CollidingAssetLinkException(""), null);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        log.info("Successfully conducted unit test");
    }

    @Test
    public void testHandleIllegalArgumentException() {
        log.info("Started unit test - testHandleIllegalArgumentException");
        ResponseEntity<?> response = advice.handleIllegalArgumentException(new IllegalArgumentException());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        log.info("Successfully conducted unit test");
    }

    @Test
    public void testHandleIdentificationMismatchException() {
        log.info("Started unit test - testHandleIdentificationMismatchException");
        ResponseEntity<?> response = advice.handleIdMismatchException(new IdentificationMismatchException(""));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        log.info("Successfully conducted unit test");
    }

    @Test
    public void testHandleFeatureNotSupportedException() {
        log.info("Started unit test - testHandleFeatureNotSupportedException");
        ResponseEntity<?> response = advice.handleFeatureNotSupportedException(new FeatureNotSupportedException(""));
        assertEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
        log.info("Successfully conducted unit test");
    }

    @Test
    public void testHandleNotInvokableException() {
        log.info("Started unit test - testHandleNotInvokableException");
        ResponseEntity<?> response = advice.handleNotInvokableException(new NotInvokableException(""));
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        log.info("Successfully conducted unit test");
    }

    @Test
    public void testHandleElementNotAFileException() {
        log.info("Started unit test - testHandleElementNotAFileException");
        ResponseEntity<?> response = advice.handleElementNotAFileException(new ElementNotAFileException(""));
        assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
        log.info("Successfully conducted unit test");
    }

    @Test
    public void testHandleInsufficientPermissionException() {
        log.info("Started unit test - testHandleInsufficientPermissionException");
        ResponseEntity<?> response = advice.handleInsufficientPermissionException(new InsufficientPermissionException(""), null);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        log.info("Successfully conducted unit test");
    }

    @Test
    public void testHandleNullSubjectException() {
        log.info("Started unit test - testHandleNullSubjectException");
        ResponseEntity<?> response = advice.handleNullSubjectException(new NullSubjectException(""));
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        log.info("Successfully conducted unit test");
    }

    @Test
    public void testHandleOperationDelegationException() {
        log.info("Started unit test - testHandleOperationDelegationException");
        ResponseEntity<?> response = advice.handleNullSubjectException(new OperationDelegationException(""));
        assertEquals(HttpStatus.FAILED_DEPENDENCY, response.getStatusCode());
        log.info("Successfully conducted unit test");
    }
}