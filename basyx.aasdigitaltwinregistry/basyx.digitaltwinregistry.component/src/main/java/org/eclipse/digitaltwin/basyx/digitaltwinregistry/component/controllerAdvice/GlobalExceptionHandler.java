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

package org.eclipse.digitaltwin.basyx.digitaltwinregistry.component.controllerAdvice;

import java.time.OffsetDateTime;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Result> handleValidationException(MethodArgumentNotValidException ex) {
		Result result = new Result();
		OffsetDateTime timestamp = OffsetDateTime.now();
		String reason = HttpStatus.BAD_REQUEST.getReasonPhrase();
		for (ObjectError error : ex.getAllErrors()) {
			result.addMessagesItem(new Message().code(reason).messageType(MessageTypeEnum.EXCEPTION).text(error.toString()).timestamp(timestamp));
		}
		return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Result> handleExceptions(ResponseStatusException ex) {
		return newResultEntity(ex, HttpStatus.valueOf(ex.getStatusCode().value()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Result> handleExceptions(Exception ex) {
		return newResultEntity(ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ElementDoesNotExistException.class)
	public <T> ResponseEntity<T> handleElementNotFoundException(ElementDoesNotExistException exception, WebRequest request) {
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(AssetLinkDoesNotExistException.class)
	public <T> ResponseEntity<T> handleElementNotFoundException(AssetLinkDoesNotExistException exception, WebRequest request) {
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(FileDoesNotExistException.class)
	public <T> ResponseEntity<T> handleElementNotFoundException(FileDoesNotExistException exception, WebRequest request) {
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(CollidingIdentifierException.class)
	public <T> ResponseEntity<T> handleCollidingIdentifierException(CollidingIdentifierException exception, WebRequest request) {
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@ExceptionHandler(MissingIdentifierException.class)
	public <T> ResponseEntity<T> handleMissingIdentifierException(MissingIdentifierException exception, WebRequest request) {
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(CollidingAssetLinkException.class)
	public <T> ResponseEntity<T> handleCollidingIdentifierException(CollidingAssetLinkException exception, WebRequest request) {
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public <T> ResponseEntity<T> handleIllegalArgumentException(IllegalArgumentException exception) {
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IdentificationMismatchException.class)
	public <T> ResponseEntity<T> handleIdMismatchException(IdentificationMismatchException exception) {
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(FeatureNotSupportedException.class)
	public <T> ResponseEntity<T> handleFeatureNotSupportedException(FeatureNotSupportedException exception) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}

	@ExceptionHandler(NotInvokableException.class)
	public <T> ResponseEntity<T> handleNotInvokableException(NotInvokableException exception) {
		return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(ElementNotAFileException.class)
	public <T> ResponseEntity<T> handleElementNotAFileException(ElementNotAFileException exception) {
		return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
	}

	@ExceptionHandler(InsufficientPermissionException.class)
	public <T> ResponseEntity<T> handleInsufficientPermissionException(InsufficientPermissionException exception, WebRequest request) {
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(NullSubjectException.class)
	public <T> ResponseEntity<T> handleNullSubjectException(NullSubjectException exception) {
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(OperationDelegationException.class)
	public <T> ResponseEntity<T> handleNullSubjectException(OperationDelegationException exception) {
		return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
	}

	private ResponseEntity<Result> newResultEntity(Exception ex, HttpStatus status) {
		log.info("Application went into exception {}", ex.getLocalizedMessage());
		Result result = new Result();
		Message message = newExceptionMessage(ex.getMessage(), status);
		result.addMessagesItem(message);

		return ResponseEntity
				.status(status)
				.contentType(MediaType.APPLICATION_JSON)
				.body(result);
	}


	private Message newExceptionMessage(String msg, HttpStatus status) {
		Message message = new Message();
		message.setCode("" + status.value());
		message.setMessageType(MessageTypeEnum.EXCEPTION);
		message.setTimestamp(OffsetDateTime.now());
		message.setText(msg);
		return message;
	}
}