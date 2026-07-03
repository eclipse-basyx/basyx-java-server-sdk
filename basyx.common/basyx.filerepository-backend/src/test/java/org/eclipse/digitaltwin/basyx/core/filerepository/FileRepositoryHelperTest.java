/*******************************************************************************
 * Copyright (C) 2026 the Eclipse BaSyx Authors
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;
import org.junit.Test;

public class FileRepositoryHelperTest {

	@Test
	public void saveOrOverwriteFileRejectsPathTraversalFileName() throws IOException {
		Path victimFile = Files.createTempDirectory("basyx-victim").resolve("pwned.txt");
		GridFsLikeFileRepository fileRepository = new GridFsLikeFileRepository();

		try {
			assertThrows(SecurityException.class, () -> FileRepositoryHelper.saveOrOverwriteFile(fileRepository, victimFile.toAbsolutePath().toString(), "text/plain", content("payload")));
			assertFalse(Files.exists(victimFile));
		} finally {
			Files.deleteIfExists(victimFile.getParent());
		}
	}

	@Test
	public void saveOrOverwriteFileUsesGeneratedStorageIdAndPreservesOriginalName() {
		GridFsLikeFileRepository fileRepository = new GridFsLikeFileRepository();

		String storedId = FileRepositoryHelper.saveOrOverwriteFile(fileRepository, "thumbnail.png", "image/png", content("payload"));

		assertNotEquals("thumbnail.png", storedId);
		assertTrue(storedId.matches("[0-9a-fA-F\\-]{36}\\.png"));
		assertTrue(fileRepository.exists(storedId));
		assertEquals("thumbnail.png", fileRepository.getOriginalFileName(storedId));
		assertArrayEquals("payload".getBytes(UTF_8), fileRepository.content(storedId));
	}

	@Test
	public void saveOrOverwriteFileDropsUnsafeExtensionFromStorageId() {
		GridFsLikeFileRepository fileRepository = new GridFsLikeFileRepository();

		String storedId = FileRepositoryHelper.saveOrOverwriteFile(fileRepository, "thumbnail.pn:g", "image/png", content("payload"));

		assertTrue(storedId.matches("[0-9a-fA-F\\-]{36}"));
		assertTrue(fileRepository.exists(storedId));
		assertEquals("thumbnail.pn:g", fileRepository.getOriginalFileName(storedId));
	}

	@Test
	public void saveOrOverwriteFileDropsLongExtensionFromStorageId() {
		GridFsLikeFileRepository fileRepository = new GridFsLikeFileRepository();

		String storedId = FileRepositoryHelper.saveOrOverwriteFile(fileRepository, "thumbnail.abcdefghijklmnopq", "image/png", content("payload"));

		assertTrue(storedId.matches("[0-9a-fA-F\\-]{36}"));
		assertTrue(fileRepository.exists(storedId));
		assertEquals("thumbnail.abcdefghijklmnopq", fileRepository.getOriginalFileName(storedId));
	}

	@Test
	public void saveOrOverwriteFileRejectsControlCharacterFileName() {
		GridFsLikeFileRepository fileRepository = new GridFsLikeFileRepository();

		assertThrows(IllegalArgumentException.class, () -> FileRepositoryHelper.saveOrOverwriteFile(fileRepository, "thumbnail.pn\0g", "image/png", content("payload")));
	}

	@Test
	public void fetchAndStoreFileLocallyDoesNotUseRepositoryKeyAsFilesystemPath() throws IOException {
		Path victimDirectory = Files.createTempDirectory("basyx-victim");
		Path victimFile = victimDirectory.resolve("pwned.txt");
		String maliciousRepositoryKey = victimFile.toAbsolutePath().toString();
		byte[] attackerPayload = "attacker bytes".getBytes(UTF_8);
		GridFsLikeFileRepository fileRepository = new GridFsLikeFileRepository();
		fileRepository.put(maliciousRepositoryKey, attackerPayload);

		try {
			java.io.File localFile = FileRepositoryHelper.fetchAndStoreFileLocally(fileRepository, maliciousRepositoryKey);

			assertTrue(localFile.exists());
			assertNotEquals(victimFile.toAbsolutePath().toString(), localFile.toPath().toAbsolutePath().toString());
			assertFalse(Files.exists(victimFile));
			assertArrayEquals(attackerPayload, Files.readAllBytes(localFile.toPath()));
			Files.deleteIfExists(localFile.toPath());
		} finally {
			Files.deleteIfExists(victimFile);
			Files.deleteIfExists(victimDirectory);
		}
	}

	private static ByteArrayInputStream content(String content) {
		return new ByteArrayInputStream(content.getBytes(UTF_8));
	}

	private static class GridFsLikeFileRepository implements FileRepository {
		private final Map<String, StoredFile> store = new HashMap<>();

		@Override
		public String save(FileMetadata metadata) throws FileHandlingException {
			if (exists(metadata.getFileName()))
				throw new FileHandlingException("File '%s' already exists.".formatted(metadata.getFileName()));

			try {
				put(metadata.getFileName(), metadata.getOriginalFileName(), metadata.getFileContent().readAllBytes());
			} catch (IOException e) {
				throw new FileHandlingException(metadata.getFileName(), e);
			}

			return metadata.getFileName();
		}

		@Override
		public InputStream find(String fileId) throws FileDoesNotExistException {
			if (!exists(fileId))
				throw new FileDoesNotExistException();

			return new ByteArrayInputStream(content(fileId));
		}

		@Override
		public void delete(String fileId) throws FileDoesNotExistException {
			if (!exists(fileId))
				throw new FileDoesNotExistException();

			store.remove(fileId);
		}

		@Override
		public boolean exists(String fileId) {
			return fileId != null && store.containsKey(fileId);
		}

		@Override
		public String getOriginalFileName(String fileId) {
			return store.get(fileId).originalFileName;
		}

		void put(String fileId, byte[] content) {
			put(fileId, fileId, content);
		}

		void put(String fileId, String originalFileName, byte[] content) {
			store.put(fileId, new StoredFile(originalFileName, content));
		}

		byte[] content(String fileId) {
			return store.get(fileId).content;
		}
	}

	private static class StoredFile {
		private final String originalFileName;
		private final byte[] content;

		StoredFile(String originalFileName, byte[] content) {
			this.originalFileName = originalFileName;
			this.content = content;
		}
	}
}
