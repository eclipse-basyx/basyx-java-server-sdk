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


package org.eclipse.digitaltwin.basyx.http;

import org.eclipse.digitaltwin.basyx.core.MessageType;
import org.eclipse.digitaltwin.basyx.core.ResultMessage;
import org.eclipse.digitaltwin.basyx.core.exceptions.AssetLinkDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingAssetLinkException;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FeatureNotSupportedException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.exceptions.InsufficientPermissionException;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotInvokableException;
import org.eclipse.digitaltwin.basyx.core.exceptions.NullSubjectException;
import org.eclipse.digitaltwin.basyx.core.exceptions.OperationDelegationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configures overall Exception to HTTP status code mapping
 * 
 * @author schnicke, fried
 *
 */
@ControllerAdvice
public class BaSyxExceptionHandler {

	private ResponseEntity<Object> buildResponse(String message, HttpStatus status) {
		var body = new ResultMessage(message, status.value(), "TODO", MessageType.Error).build();
		return new ResponseEntity<>(List.of(body), status);
	}

	@ExceptionHandler(ElementDoesNotExistException.class)
	public ResponseEntity<Object> handleElementNotFoundException(ElementDoesNotExistException exception, WebRequest request) {
		return buildResponse(exception.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(AssetLinkDoesNotExistException.class)
	public ResponseEntity<Object> handleElementNotFoundException(AssetLinkDoesNotExistException exception, WebRequest request) {
		return buildResponse(exception.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(FileDoesNotExistException.class)
	public ResponseEntity<Object> handleElementNotFoundException(FileDoesNotExistException exception, WebRequest request) {
		return buildResponse(exception.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(CollidingIdentifierException.class)
	public ResponseEntity<Object> handleCollidingIdentifierException(CollidingIdentifierException exception, WebRequest request) {
		return buildResponse(exception.getMessage(), HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(MissingIdentifierException.class)
	public ResponseEntity<Object> handleMissingIdentifierException(MissingIdentifierException exception, WebRequest request) {
		return buildResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(CollidingAssetLinkException.class)
	public ResponseEntity<Object> handleCollidingIdentifierException(CollidingAssetLinkException exception, WebRequest request) {
		return buildResponse(exception.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(IdentificationMismatchException.class)
	public ResponseEntity<Object> handleIdMismatchException(IdentificationMismatchException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(FeatureNotSupportedException.class)
	public ResponseEntity<Object> handleFeatureNotSupportedException(FeatureNotSupportedException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.NOT_IMPLEMENTED);
	}

	@ExceptionHandler(NotInvokableException.class)
	public ResponseEntity<Object> handleNotInvokableException(NotInvokableException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
	}
	
	@ExceptionHandler(ElementNotAFileException.class)
	public ResponseEntity<Object> handleElementNotAFileException(ElementNotAFileException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.PRECONDITION_FAILED);
	}
	
	@ExceptionHandler(InsufficientPermissionException.class)
	public ResponseEntity<Object> handleInsufficientPermissionException(InsufficientPermissionException exception, WebRequest request) {
		return buildResponse(exception.getMessage(), HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(NullSubjectException.class)
	public ResponseEntity<Object> handleNullSubjectException(NullSubjectException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(OperationDelegationException.class)
	public ResponseEntity<Object> handleNullSubjectException(OperationDelegationException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.FAILED_DEPENDENCY);
	}
}
