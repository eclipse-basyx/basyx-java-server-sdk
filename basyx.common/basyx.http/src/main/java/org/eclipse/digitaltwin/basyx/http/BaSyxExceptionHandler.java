/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FeatureNotSupportedException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotInvokableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Configures overall Exception to HTTP status code mapping
 * 
 * @author schnicke
 *
 */
@ControllerAdvice
public class BaSyxExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ElementDoesNotExistException.class)
	public <T> ResponseEntity<T> handleElementNotFoundException(ElementDoesNotExistException exception, WebRequest request) {
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(CollidingIdentifierException.class)
	public <T> ResponseEntity<T> handleCollidingIdentifierException(CollidingIdentifierException exception, WebRequest request) {
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
}
