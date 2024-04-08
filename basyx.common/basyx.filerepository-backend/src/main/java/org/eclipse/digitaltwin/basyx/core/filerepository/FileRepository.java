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

package org.eclipse.digitaltwin.basyx.core.filerepository;

import java.io.InputStream;

import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;

/**
 * Interface representing a file repository. 
 * Provides methods to save, find, delete, and check the existence of files.
 * 
 * @author danish
 */
public interface FileRepository {
	
	/**
	 * Saves the file with provided {@link FileMetadata}.
	 * 
	 * @param metadata
	 * @return the implementation specific value such as file path or id
	 * @throws FileHandlingException
	 */
	public String save(FileMetadata metadata) throws FileHandlingException;
	
	/**
	 * Finds and returns the InputStream of the file specified by its id.
	 * 
	 * @param fileId
	 * @return the InputStream of the file
	 * @throws FileDoesNotExistException
	 */
	public InputStream find(String fileId) throws FileDoesNotExistException;
	
	/**
	 * Deletes the file specified by its id.
	 * 
	 * @param fileId
	 * @throws FileDoesNotExistException
	 */
	public void delete(String fileId) throws FileDoesNotExistException;
	
	/**
	 * Checks if a file exists with the specified id.
	 * 
	 * @param fileId
	 * @return
	 */
	public boolean exists(String fileId);

}
