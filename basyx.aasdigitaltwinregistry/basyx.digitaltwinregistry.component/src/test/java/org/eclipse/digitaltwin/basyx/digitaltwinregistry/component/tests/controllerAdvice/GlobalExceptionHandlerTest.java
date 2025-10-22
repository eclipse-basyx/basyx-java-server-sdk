/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.digitaltwinregistry.component.tests.controllerAdvice;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.digitaltwin.basyx.digitaltwinregistry.component.controllerAdvice.GlobalExceptionHandler;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Message;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Result;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Message.MessageTypeEnum;
import org.eclipse.digitaltwin.basyx.core.exceptions.AssetLinkDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingAssetLinkException;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FeatureNotSupportedException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.exceptions.InsufficientPermissionException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotInvokableException;
import org.eclipse.digitaltwin.basyx.core.exceptions.NullSubjectException;
import org.eclipse.digitaltwin.basyx.core.exceptions.OperationDelegationException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

@Slf4j
public class GlobalExceptionHandlerTest {

	private GlobalExceptionHandler exceptionHandler;
	private WebRequest mockWebRequest;

	@Before
	public void setUp() {
		exceptionHandler = new GlobalExceptionHandler();
		mockWebRequest = mock(WebRequest.class);
	}

	@Test
	public void testHandleValidationException() {
		log.info("Started unit test - testHandleValidationException()");
		MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
		ObjectError error1 = new ObjectError("object", "Error message 1");
		ObjectError error2 = new ObjectError("object", "Error message 2");
		List<ObjectError> errors = Arrays.asList(error1, error2);
		when(ex.getAllErrors()).thenReturn(errors);
		ResponseEntity<Result> response = exceptionHandler.handleValidationException(ex);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNotNull(response.getBody());
		assertNotNull(response.getBody().getMessages());
		assertEquals(2, response.getBody().getMessages().size());
		log.info("Unit test conducted successfully");
	}

	@Test
	public void testHandleGenericException() {
		log.info("Started unit test - testHandleGenericException()");
		Exception ex = new Exception("Generic error");
		ResponseEntity<Result> response = exceptionHandler.handleExceptions(ex);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertResultHasExceptionMessage(response.getBody(), "Generic error");
		log.info("Unit test conducted successfully");
	}

	@Test
	public void testHandleElementDoesNotExistException() {
		log.info("Started unit test - testHandleElementDoesNotExistException()");
		ElementDoesNotExistException ex = new ElementDoesNotExistException("Element not found");
		ResponseEntity<Object> response = exceptionHandler.handleElementNotFoundException(ex, mockWebRequest);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		log.info("Unit test conducted successfully");
	}

	@Test
	public void testHandleAssetLinkDoesNotExistException() {
		log.info("Started unit test - testHandleAssetLinkDoesNotExistException()");
		AssetLinkDoesNotExistException ex = new AssetLinkDoesNotExistException("Asset link not found");
		ResponseEntity<Object> response = exceptionHandler.handleElementNotFoundException(ex, mockWebRequest);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		log.info("Unit test conducted successfully");
	}

	@Test
	public void testHandleFileDoesNotExistException() {
		log.info("Started unit test - testHandleFileDoesNotExistException()");
		FileDoesNotExistException ex = new FileDoesNotExistException("File not found");
		ResponseEntity<Object> response = exceptionHandler.handleElementNotFoundException(ex, mockWebRequest);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		log.info("Unit test conducted successfully");
	}

	@Test
	public void testHandleCollidingIdentifierException() {
		log.info("Started unit test - testHandleCollidingIdentifierException()");
		CollidingIdentifierException ex = new CollidingIdentifierException("Colliding identifier");
		ResponseEntity<Object> response = exceptionHandler.handleCollidingIdentifierException(ex, mockWebRequest);
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		log.info("Unit test conducted successfully");
	}

	@Test
	public void testHandleMissingIdentifierException() {
		log.info("Started unit test - testHandleMissingIdentifierException()");
		MissingIdentifierException ex = new MissingIdentifierException("Missing identifier");
		ResponseEntity<Object> response = exceptionHandler.handleMissingIdentifierException(ex, mockWebRequest);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		log.info("Unit test conducted successfully");
	}

	@Test
	public void testHandleCollidingAssetLinkException() {
		log.info("Started unit test - testHandleCollidingAssetLinkException()");
		CollidingAssetLinkException ex = new CollidingAssetLinkException("Colliding asset link");
		ResponseEntity<Object> response = exceptionHandler.handleCollidingIdentifierException(ex, mockWebRequest);
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		log.info("Unit test conducted successfully");
	}

	@Test
	public void testHandleIllegalArgumentException() {
		log.info("Started unit test - testHandleIllegalArgumentException()");
		IllegalArgumentException ex = new IllegalArgumentException("Illegal argument");
		ResponseEntity<Object> response = exceptionHandler.handleIllegalArgumentException(ex);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		log.info("Unit test conducted successfully");
	}

	@Test
	public void testHandleIdentificationMismatchException() {
		log.info("Started unit test - testHandleIdentificationMismatchException()");
		IdentificationMismatchException ex = new IdentificationMismatchException("ID mismatch");
		ResponseEntity<Object> response = exceptionHandler.handleIdMismatchException(ex);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		log.info("Unit test conducted successfully");
	}

	@Test
	public void testHandleFeatureNotSupportedException() {
		log.info("Started unit test - testHandleFeatureNotSupportedException()");
		FeatureNotSupportedException ex = new FeatureNotSupportedException("Feature not supported");
		ResponseEntity<Object> response = exceptionHandler.handleFeatureNotSupportedException(ex);
		assertEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
		log.info("Unit test conducted successfully");
	}

	@Test
	public void testHandleNotInvokableException() {
		log.info("Started unit test - testHandleNotInvokableException()");
		NotInvokableException ex = new NotInvokableException("Not invokable");
		ResponseEntity<Object> response = exceptionHandler.handleNotInvokableException(ex);
		assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
		log.info("Unit test conducted successfully");
	}

	@Test
	public void testHandleElementNotAFileException() {
		log.info("Started unit test - testHandleElementNotAFileException()");
		ElementNotAFileException ex = new ElementNotAFileException("Element not a file");
		ResponseEntity<Object> response = exceptionHandler.handleElementNotAFileException(ex);
		assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
		log.info("Unit test conducted successfully");
	}

	@Test
	public void testHandleInsufficientPermissionException() {
		log.info("Started unit test - testHandleInsufficientPermissionException()");
		InsufficientPermissionException ex = new InsufficientPermissionException("Insufficient permission");
		ResponseEntity<Object> response = exceptionHandler.handleInsufficientPermissionException(ex, mockWebRequest);
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
		log.info("Unit test conducted successfully");
	}

	@Test
	public void testHandleNullSubjectException() {
		log.info("Started unit test - testHandleNullSubjectException()");
		NullSubjectException ex = new NullSubjectException("Null subject");
		ResponseEntity<Object> response = exceptionHandler.handleNullSubjectException(ex);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		log.info("Unit test conducted successfully");
	}

	@Test
	public void testHandleOperationDelegationException() {
		log.info("Started unit test - testHandleOperationDelegationException()");
		OperationDelegationException ex = new OperationDelegationException("Operation delegation failed");
		ResponseEntity<Object> response = exceptionHandler.handleNullSubjectException(ex);
		assertEquals(HttpStatus.FAILED_DEPENDENCY, response.getStatusCode());
		log.info("Unit test conducted successfully");
	}

	@Test
	public void testNewExceptionMessage() throws Exception {
		log.info("Started unit test - testNewExceptionMessage()");
		String errorMessage = "Test error message";
		HttpStatus status = HttpStatus.BAD_REQUEST;

		java.lang.reflect.Method method = GlobalExceptionHandler.class.getDeclaredMethod("newExceptionMessage", String.class, HttpStatus.class);
		method.setAccessible(true);

		Message message = (Message) method.invoke(exceptionHandler, errorMessage, status);
		assertNotNull(message);
		assertEquals(String.valueOf(status.value()), message.getCode());
		assertEquals(MessageTypeEnum.EXCEPTION, message.getMessageType());
		assertEquals(errorMessage, message.getText());
		assertNotNull(message.getTimestamp());
		log.info("Unit test conducted successfully");
	}

	@Test
	public void testNewResultEntity() throws Exception {
		log.info("Started unit test - testNewResultEntity()");
		Exception ex = new Exception("Test exception");
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

		java.lang.reflect.Method method = GlobalExceptionHandler.class.getDeclaredMethod("newResultEntity", Exception.class, HttpStatus.class);
		method.setAccessible(true);

		ResponseEntity<Result> response = (ResponseEntity<Result>) method.invoke(exceptionHandler, ex, status);
		assertEquals(status, response.getStatusCode());
		assertNotNull(response.getBody());
		assertResultHasExceptionMessage(response.getBody(), "Test exception");
		log.info("Unit test conducted successfully");
	}

	private void assertResultHasExceptionMessage(Result result, String expectedMessage) {
		assertNotNull(result);
		assertNotNull(result.getMessages());
		assertEquals(1, result.getMessages().size());
		Message message = result.getMessages().get(0);
		assertEquals(expectedMessage, message.getText());
		assertEquals(MessageTypeEnum.EXCEPTION, message.getMessageType());
		assertNotNull(message.getTimestamp());
	}
}