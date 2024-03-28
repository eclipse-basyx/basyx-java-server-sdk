/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.aasregistry.service.errors;

import java.time.OffsetDateTime;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Message;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Message.MessageTypeEnum;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Result;
import org.eclipse.digitaltwin.basyx.core.exceptions.InsufficientPermissionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class BasyxControllerAdvice {

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

	@ExceptionHandler(InsufficientPermissionException.class)
	public ResponseEntity<Result> handleInsufficientPermissionException(InsufficientPermissionException exception) {
		return newResultEntity(exception, HttpStatus.FORBIDDEN);
	}

	private ResponseEntity<Result> newResultEntity(Exception ex, HttpStatus status) {
		Result result = new Result();
		Message message = newExceptionMessage(ex.getMessage(), status);
		result.addMessagesItem(message);
		return new ResponseEntity<>(result, status);
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
