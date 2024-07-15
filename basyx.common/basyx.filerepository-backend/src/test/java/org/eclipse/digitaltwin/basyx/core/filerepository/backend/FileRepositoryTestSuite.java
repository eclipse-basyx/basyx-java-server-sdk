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
package org.eclipse.digitaltwin.basyx.core.filerepository.backend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
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
	
	private FileMetadata createDummyFile() {
		String name = "test";
		String contentType ="txt";
		InputStream input = new ByteArrayInputStream("this is test data".getBytes());
		
		FileMetadata dummyFile = new FileMetadata(name, contentType, input);
	
		return dummyFile;
	}

	public String getSavedTestFile(FileRepository fileRepo) {
		FileMetadata testFile = createDummyFile();
		String filePath = fileRepo.save(testFile);
		
		return filePath;
	}
	
	@Test
	public void saveValidFile() {
		FileRepository fileRepo = getFileRepository();
		FileMetadata testFile = createDummyFile();
		String filePath = fileRepo.save(testFile);
		
		assertEquals(true, fileRepo.exists(filePath));
	}

	@Test
	public void findExisting() throws IOException {
		FileRepository fileRepo = getFileRepository();
		String filePath = getSavedTestFile(fileRepo);
		
		InputStream actualStream = fileRepo.find(filePath);
		
		InputStream streamToCompare = new ByteArrayInputStream("this is test data".getBytes());
		
		IOUtils.contentEquals(streamToCompare, actualStream);
	}
	
	@Test(expected = FileDoesNotExistException.class)
	public void findNonExisting() {
		FileRepository fileRepo = getFileRepository();
		String filePath = "does not exist";
		
		InputStream testStream = fileRepo.find(filePath);
	}
	
	@Test
	public void deleteFile() {
		FileRepository fileRepo = getFileRepository();
		String filePath = getSavedTestFile(fileRepo);
		boolean fileExistsAfterSave = fileRepo.exists(filePath);
		
		fileRepo.delete(filePath);
		boolean fileDeleted = fileRepo.exists(filePath);
			
		assertNotEquals(fileExistsAfterSave, fileDeleted);
	}
	
	@Test(expected = FileDoesNotExistException.class)
	public void deleteNonExistingFile() {
		FileRepository fileRepo = getFileRepository();
		String filePath = "does not exist";
		
		fileRepo.delete(filePath);
	}
	
	@Test
	public void doesExists() {
		FileRepository fileRepo = getFileRepository();
		String filePath = getSavedTestFile(fileRepo);
		
		boolean fileExists = fileRepo.exists(filePath);
		
		assertEquals(true, fileExists);
	}
	
	@Test
	public void doesNotExists() {
		FileRepository fileRepo = getFileRepository();
		
		String filePath = "does not exist";
		
		boolean fileNotExists = fileRepo.exists(filePath);
		
		assertEquals(false, fileNotExists);
	}

}
