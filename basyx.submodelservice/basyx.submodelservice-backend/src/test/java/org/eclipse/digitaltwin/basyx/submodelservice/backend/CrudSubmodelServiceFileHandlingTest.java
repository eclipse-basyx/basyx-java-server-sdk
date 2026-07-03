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

package org.eclipse.digitaltwin.basyx.submodelservice.backend;

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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultFile;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileMetadata;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.junit.Test;

public class CrudSubmodelServiceFileHandlingTest {

	private static final String SUBMODEL_ID = "submodel-1";
	private static final String FILE_ID_SHORT = "document";

	@Test
	public void getFileByPathWithAbsoluteStoredPathWritesOnlyTempFile() throws IOException {
		Path victimDirectory = Files.createTempDirectory("basyx-victim");
		Path victimFile = victimDirectory.resolve("pwned.txt");
		String maliciousRepositoryKey = victimFile.toAbsolutePath().toString();
		byte[] attackerPayload = "attacker bytes".getBytes(UTF_8);
		GridFsLikeFileRepository fileRepository = new GridFsLikeFileRepository();
		fileRepository.put(maliciousRepositoryKey, attackerPayload);
		TestSubmodelBackend backend = new TestSubmodelBackend(fileElement(maliciousRepositoryKey));
		CrudSubmodelService service = new CrudSubmodelService(backend.proxy(), fileRepository, SUBMODEL_ID);

		try {
			java.io.File localFile = service.getFileByPath(FILE_ID_SHORT);

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

	@Test
	public void getFileByPathAsStreamWithAbsoluteStoredPathDoesNotWriteToDisk() throws IOException {
		Path victimDirectory = Files.createTempDirectory("basyx-victim");
		Path victimFile = victimDirectory.resolve("pwned.txt");
		String maliciousRepositoryKey = victimFile.toAbsolutePath().toString();
		byte[] attackerPayload = "attacker bytes".getBytes(UTF_8);
		GridFsLikeFileRepository fileRepository = new GridFsLikeFileRepository();
		fileRepository.put(maliciousRepositoryKey, attackerPayload);
		TestSubmodelBackend backend = new TestSubmodelBackend(fileElement(maliciousRepositoryKey));
		CrudSubmodelService service = new CrudSubmodelService(backend.proxy(), fileRepository, SUBMODEL_ID);

		try (InputStream inputStream = service.getFileByPathAsStream(FILE_ID_SHORT)) {
			assertArrayEquals(attackerPayload, inputStream.readAllBytes());
			assertFalse(Files.exists(victimFile));
		} finally {
			Files.deleteIfExists(victimFile);
			Files.deleteIfExists(victimDirectory);
		}
	}

	@Test
	public void updateFileElementWithUnchangedPathDoesNotMaterializeExistingFile() throws IOException {
		Path victimDirectory = Files.createTempDirectory("basyx-victim");
		Path victimFile = victimDirectory.resolve("pwned.txt");
		String maliciousRepositoryKey = victimFile.toAbsolutePath().toString();
		GridFsLikeFileRepository fileRepository = new GridFsLikeFileRepository();
		fileRepository.put(maliciousRepositoryKey, "attacker bytes".getBytes(UTF_8));
		TestSubmodelBackend backend = new TestSubmodelBackend(fileElement(maliciousRepositoryKey));
		CrudSubmodelService service = new CrudSubmodelService(backend.proxy(), fileRepository, SUBMODEL_ID);

		try {
			service.updateSubmodelElement(FILE_ID_SHORT, fileElement(maliciousRepositoryKey));

			assertEquals(0, fileRepository.findCalls);
			assertEquals(1, backend.updateCalls);
			assertFalse(Files.exists(victimFile));
		} finally {
			Files.deleteIfExists(victimFile);
			Files.deleteIfExists(victimDirectory);
		}
	}

	@Test
	public void setFileValueCleansNewFileAndKeepsOldFileWhenValueUpdateFails() {
		String oldFilePath = "old-file";
		GridFsLikeFileRepository fileRepository = new GridFsLikeFileRepository();
		fileRepository.put(oldFilePath, "old".getBytes(UTF_8));
		TestSubmodelBackend backend = new TestSubmodelBackend(fileElement(oldFilePath));
		backend.failSetSubmodelElementValue = true;
		CrudSubmodelService service = new CrudSubmodelService(backend.proxy(), fileRepository, SUBMODEL_ID);

		try {
			service.setFileValue(FILE_ID_SHORT, "new.txt", "text/plain", new ByteArrayInputStream("new".getBytes(UTF_8)));
		} catch (RuntimeException e) {
			assertTrue(fileRepository.exists(oldFilePath));
			assertEquals(1, fileRepository.size());
			return;
		}

		throw new AssertionError("Expected setFileValue to fail.");
	}

	@Test
	public void deleteFileValueKeepsFileWhenValueUpdateFails() {
		String oldFilePath = "old-file";
		GridFsLikeFileRepository fileRepository = new GridFsLikeFileRepository();
		fileRepository.put(oldFilePath, "old".getBytes(UTF_8));
		TestSubmodelBackend backend = new TestSubmodelBackend(fileElement(oldFilePath));
		backend.failSetSubmodelElementValue = true;
		CrudSubmodelService service = new CrudSubmodelService(backend.proxy(), fileRepository, SUBMODEL_ID);

		assertThrows(RuntimeException.class, () -> service.deleteFileValue(FILE_ID_SHORT));

		assertTrue(fileRepository.exists(oldFilePath));
		assertEquals(1, fileRepository.size());
	}

	private static org.eclipse.digitaltwin.aas4j.v3.model.File fileElement(String value) {
		return new DefaultFile.Builder().idShort(FILE_ID_SHORT).contentType("text/plain").value(value).build();
	}

	private static class TestSubmodelBackend implements InvocationHandler {
		private final AtomicReference<SubmodelElement> submodelElement;
		private int updateCalls;
		private boolean failSetSubmodelElementValue;

		TestSubmodelBackend(SubmodelElement submodelElement) {
			this.submodelElement = new AtomicReference<>(submodelElement);
		}

		SubmodelBackend proxy() {
			return (SubmodelBackend) Proxy.newProxyInstance(SubmodelBackend.class.getClassLoader(), new Class<?>[] { SubmodelBackend.class }, this);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) {
			if (method.getDeclaringClass() == Object.class)
				return invokeObjectMethod(proxy, method, args);

			switch (method.getName()) {
				case "getSubmodelElement":
					return submodelElement.get();
				case "updateSubmodelElement":
					submodelElement.set((SubmodelElement) args[2]);
					updateCalls++;
					return null;
				case "setSubmodelElementValue":
					if (failSetSubmodelElementValue)
						throw new RuntimeException("Could not update submodel element value.");

					return null;
				default:
					throw new UnsupportedOperationException(method.getName());
			}
		}

		private Object invokeObjectMethod(Object proxy, Method method, Object[] args) {
			switch (method.getName()) {
				case "equals":
					return proxy == args[0];
				case "hashCode":
					return System.identityHashCode(proxy);
				case "toString":
					return "TestSubmodelBackend";
				default:
					throw new UnsupportedOperationException(method.getName());
			}
		}
	}

	private static class GridFsLikeFileRepository implements FileRepository {
		private final Map<String, byte[]> store = new HashMap<>();
		private int findCalls;

		@Override
		public String save(FileMetadata metadata) throws FileHandlingException {
			if (exists(metadata.getFileName()))
				throw new FileHandlingException("File '%s' already exists.".formatted(metadata.getFileName()));

			try {
				store.put(metadata.getFileName(), metadata.getFileContent().readAllBytes());
			} catch (IOException e) {
				throw new FileHandlingException(metadata.getFileName(), e);
			}

			return metadata.getFileName();
		}

		@Override
		public InputStream find(String fileId) throws FileDoesNotExistException {
			findCalls++;

			if (!exists(fileId))
				throw new FileDoesNotExistException();

			return new ByteArrayInputStream(store.get(fileId));
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

		void put(String fileId, byte[] content) {
			store.put(fileId, content);
		}

		int size() {
			return store.size();
		}
	}
}
