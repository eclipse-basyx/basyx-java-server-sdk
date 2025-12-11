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

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.commons.lang3.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.basyx.core.MessageType;
import org.eclipse.digitaltwin.basyx.core.ResultMessage;
import org.eclipse.digitaltwin.basyx.core.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * Configures overall Exception to HTTP status code mapping
 * 
 * @author schnicke, fried
 *
 */
@ControllerAdvice
public class BaSyxExceptionHandler {

	private ResponseEntity<Object> buildResponse(String message, HttpStatus status, Object object) {
		var body = new ResultMessage(message, status.value(), object.getClass().getSimpleName() + "-" + status.value(), MessageType.Error).build();
		return new ResponseEntity<>(List.of(body), status);
	}

	@ExceptionHandler(ElementDoesNotExistException.class)
	public ResponseEntity<Object> handleElementNotFoundException(ElementDoesNotExistException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.NOT_FOUND, exception);
	}
	
	@ExceptionHandler(AssetLinkDoesNotExistException.class)
	public ResponseEntity<Object> handleElementNotFoundException(AssetLinkDoesNotExistException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.NOT_FOUND, exception);
	}
	
	@ExceptionHandler(FileDoesNotExistException.class)
	public ResponseEntity<Object> handleElementNotFoundException(FileDoesNotExistException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.NOT_FOUND, exception);
	}

	@ExceptionHandler(CollidingIdentifierException.class)
	public ResponseEntity<Object> handleCollidingIdentifierException(CollidingIdentifierException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.CONFLICT, exception);
	}
	
	@ExceptionHandler(MissingIdentifierException.class)
	public ResponseEntity<Object> handleMissingIdentifierException(MissingIdentifierException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, exception);
	}
	
	@ExceptionHandler(CollidingAssetLinkException.class)
	public ResponseEntity<Object> handleCollidingIdentifierException(CollidingAssetLinkException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.CONFLICT, exception);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, exception);
	}
	
	@ExceptionHandler(IdentificationMismatchException.class)
	public ResponseEntity<Object> handleIdMismatchException(IdentificationMismatchException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, exception);
	}

	@ExceptionHandler(FeatureNotSupportedException.class)
	public ResponseEntity<Object> handleFeatureNotSupportedException(FeatureNotSupportedException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.NOT_IMPLEMENTED, exception);
	}

	@ExceptionHandler(NotInvokableException.class)
	public ResponseEntity<Object> handleNotInvokableException(NotInvokableException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.METHOD_NOT_ALLOWED, exception);
	}
	
	@ExceptionHandler(ElementNotAFileException.class)
	public ResponseEntity<Object> handleElementNotAFileException(ElementNotAFileException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.PRECONDITION_FAILED, exception);
	}
	
	@ExceptionHandler(InsufficientPermissionException.class)
	public ResponseEntity<Object> handleInsufficientPermissionException(InsufficientPermissionException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.FORBIDDEN, exception);
	}
	
	@ExceptionHandler(NullSubjectException.class)
	public ResponseEntity<Object> handleNullSubjectException(NullSubjectException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED, exception);
	}
	
	@ExceptionHandler(OperationDelegationException.class)
	public ResponseEntity<Object> handleNullSubjectException(OperationDelegationException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.FAILED_DEPENDENCY, exception);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, exception);
	}

	@ExceptionHandler(RepositoryRegistryLinkException.class)
	public ResponseEntity<Object> handleRepositoryRegistryLinkException(RepositoryRegistryLinkException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, exception);
	}

	@ExceptionHandler(RepositoryRegistryUnlinkException.class)
	public ResponseEntity<Object> handleRepositoryRegistryUnlinkException(RepositoryRegistryUnlinkException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, exception);
	}

	@ExceptionHandler(MissingKeyTypeException.class)
	public ResponseEntity<Object> handleMissingKeyTypeException(MissingKeyTypeException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, exception);
	}

	@ExceptionHandler(MissingAuthorizationConfigurationException.class)
	public ResponseEntity<Object> handleMissingAuthorizationConfigurationException(MissingAuthorizationConfigurationException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, exception);
	}

	@ExceptionHandler(InvalidTargetInformationException.class)
	public ResponseEntity<Object> handleInvalidTargetInformationException(InvalidTargetInformationException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, exception);
	}

	@ExceptionHandler(SerializationException.class)
	public ResponseEntity<Object> handleSerializationException(SerializationException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, exception);
	}

	@ExceptionHandler(DeserializationException.class)
	public ResponseEntity<Object> handleDeserializationException(DeserializationException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, exception);
	}

	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException exception) {
		return buildResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, exception);
	}

	@ExceptionHandler(ZipBombException.class)
	public ResponseEntity<Object> handleZipBombException(ZipBombException exception) {
		return buildResponse("Zip bomb detected! The file would exceed the max. ratio of compressed file size to the size of the expanded data.\\nThis may indicate that the file is used to inflate memory usage and thus could pose a security risk.\\nYou can adjust this limit via basyx.aasenvironment.minInflateRatio in the AAS Environment configuration if you need to work with files which exceed this limit.", HttpStatus.BAD_REQUEST, exception);
	}

}
