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
package org.eclipse.digitaltwin.basyx.core.filerepository.backend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileMetadata;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.junit.Test;

/**
 * Testsuite for implementations of the FileRepository interface
 * 
 * @author witt
 */
public abstract class FileRepositoryTestSuite {
	protected abstract FileRepository getFileRepository();

	@Test
	public void saveValidFile() {
		FileRepository fileRepo = getFileRepository();
		
		FileMetadata fileMetadata = createDummyFile();
		String filePath = saveTestFile(fileRepo, fileMetadata);
		
		assertEquals(true, fileRepo.exists(filePath));
	}

	@Test (expected = FileHandlingException.class)
	public void saveExistingFile() {
		FileRepository fileRepo = getFileRepository();
		
		FileMetadata fileMetadata = createDummyFile();
		
		String filePath = fileRepo.save(fileMetadata);
		filePath = fileRepo.save(fileMetadata);
	}

	@Test
	public void findExistingFile() throws IOException {
		FileRepository fileRepo = getFileRepository();
		
		FileMetadata fileMetadata = createDummyFile();
		String filePath = saveTestFile(fileRepo, fileMetadata);

		InputStream streamToCompare = new ByteArrayInputStream("this is test data".getBytes());
		
		InputStream actualStream = fileRepo.find(filePath);
		
		IOUtils.contentEquals(streamToCompare, actualStream);
	}
	
	@Test(expected = FileDoesNotExistException.class)
	public void findNonExistingFile() {
		FileRepository fileRepo = getFileRepository();
		
		InputStream testInputStream = fileRepo.find("does not exist");
	}
	
	@Test
	public void deleteFile() {
		FileRepository fileRepo = getFileRepository();
		
		FileMetadata fileMetadata = createDummyFile();
		String filePath = saveTestFile(fileRepo, fileMetadata);
		
		fileRepo.delete(filePath);
			
		assertFalse(fileRepo.exists(filePath));
	}
	
	@Test(expected = FileDoesNotExistException.class)
	public void deleteNonExistingFile() {
		FileRepository fileRepo = getFileRepository();
		
		String filePath = "does not exist";
		
		fileRepo.delete(filePath); 
	}
	
	@Test
	public void fileDoesExists() {
		FileRepository fileRepo = getFileRepository();
		
		FileMetadata fileMetadata = createDummyFile();
		String filePath = saveTestFile(fileRepo, fileMetadata);
		
		boolean fileExists = fileRepo.exists(filePath);
		
		assertTrue(fileExists);
	}
	
	@Test
	public void fileDoesNotExists() {
		FileRepository fileRepo = getFileRepository();
		
		String filePath = "does not exist";
		
		boolean fileNotExists = fileRepo.exists(filePath);
		
		assertFalse(fileNotExists);
	}
	
	protected FileMetadata createDummyFile() {
		String name = "test";
		String contentType = "txt";
		String contentInput ="this is test data";
		
		InputStream content = new ByteArrayInputStream(contentInput.getBytes());
		
		return new FileMetadata(name, contentType, content);
	}

	protected String saveTestFile(FileRepository fileRepo, FileMetadata fileMetadata) {
		String filePath = fileMetadata.getFileName();
		
		if(fileRepo.exists(fileMetadata.getFileName()))
			fileRepo.delete(filePath);
		
		return fileRepo.save(fileMetadata);
	}
}
