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

package org.eclipse.digitaltwin.basyx.aasservice.backend;

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
import java.util.List;
import java.util.Map;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Resource;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultResource;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileMetadata;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.Test;

public class AasThumbnailOperationsTest {

	private static final String AAS_ID = "aas-1";

	@Test
	public void setThumbnailRejectsPathTraversalFileName() throws IOException {
		Path victimFile = Files.createTempDirectory("basyx-victim").resolve("pwned.txt");
		AasThumbnailOperations thumbnailOperations = new AasThumbnailOperations(new InMemoryAasOperations(), new GridFsLikeFileRepository());

		try {
			assertThrows(SecurityException.class, () -> thumbnailOperations.setThumbnail(AAS_ID, victimFile.toAbsolutePath().toString(), "image/png", new ByteArrayInputStream("payload".getBytes(UTF_8))));
			assertFalse(Files.exists(victimFile));
		} finally {
			Files.deleteIfExists(victimFile.getParent());
		}
	}

	@Test
	public void getThumbnailWithAbsoluteResourcePathWritesOnlyTempFile() throws IOException {
		Path victimDirectory = Files.createTempDirectory("basyx-victim");
		Path victimFile = victimDirectory.resolve("pwned.txt");
		String maliciousRepositoryKey = victimFile.toAbsolutePath().toString();
		byte[] attackerPayload = "attacker bytes".getBytes(UTF_8);
		GridFsLikeFileRepository fileRepository = new GridFsLikeFileRepository();
		fileRepository.put(maliciousRepositoryKey, attackerPayload);

		InMemoryAasOperations aasOperations = new InMemoryAasOperations();
		aasOperations.setAssetInformation(AAS_ID, createAssetInformationWithThumbnail(maliciousRepositoryKey));
		AasThumbnailOperations thumbnailOperations = new AasThumbnailOperations(aasOperations, fileRepository);

		try {
			java.io.File localFile = thumbnailOperations.getThumbnail(AAS_ID);

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
	public void getThumbnailInputStreamWithAbsoluteResourcePathDoesNotWriteToDisk() throws IOException {
		Path victimDirectory = Files.createTempDirectory("basyx-victim");
		Path victimFile = victimDirectory.resolve("pwned.txt");
		String maliciousRepositoryKey = victimFile.toAbsolutePath().toString();
		byte[] attackerPayload = "attacker bytes".getBytes(UTF_8);
		GridFsLikeFileRepository fileRepository = new GridFsLikeFileRepository();
		fileRepository.put(maliciousRepositoryKey, attackerPayload);

		InMemoryAasOperations aasOperations = new InMemoryAasOperations();
		aasOperations.setAssetInformation(AAS_ID, createAssetInformationWithThumbnail(maliciousRepositoryKey));
		AasThumbnailOperations thumbnailOperations = new AasThumbnailOperations(aasOperations, fileRepository);

		try (InputStream inputStream = thumbnailOperations.getThumbnailInputStream(AAS_ID)) {
			assertArrayEquals(attackerPayload, inputStream.readAllBytes());
			assertFalse(Files.exists(victimFile));
		} finally {
			Files.deleteIfExists(victimFile);
			Files.deleteIfExists(victimDirectory);
		}
	}

	@Test
	public void setThumbnailCleansNewFileAndKeepsOldFileWhenAssetInformationUpdateFails() {
		String oldThumbnailPath = "old-thumbnail";
		GridFsLikeFileRepository fileRepository = new GridFsLikeFileRepository();
		fileRepository.put(oldThumbnailPath, "old".getBytes(UTF_8));
		FailingAasOperations aasOperations = new FailingAasOperations();
		aasOperations.assetInformation = createAssetInformationWithThumbnail(oldThumbnailPath);
		AasThumbnailOperations thumbnailOperations = new AasThumbnailOperations(aasOperations, fileRepository);

		assertThrows(RuntimeException.class, () -> thumbnailOperations.setThumbnail(AAS_ID, "new.png", "image/png", new ByteArrayInputStream("new".getBytes(UTF_8))));

		assertTrue(fileRepository.exists(oldThumbnailPath));
		assertEquals(1, fileRepository.size());
		assertEquals(oldThumbnailPath, aasOperations.getAssetInformation(AAS_ID).getDefaultThumbnail().getPath());
	}

	@Test
	public void deleteThumbnailKeepsFileWhenAssetInformationUpdateFails() {
		String oldThumbnailPath = "old-thumbnail";
		GridFsLikeFileRepository fileRepository = new GridFsLikeFileRepository();
		fileRepository.put(oldThumbnailPath, "old".getBytes(UTF_8));
		FailingAasOperations aasOperations = new FailingAasOperations();
		aasOperations.assetInformation = createAssetInformationWithThumbnail(oldThumbnailPath);
		AasThumbnailOperations thumbnailOperations = new AasThumbnailOperations(aasOperations, fileRepository);

		assertThrows(RuntimeException.class, () -> thumbnailOperations.deleteThumbnail(AAS_ID));

		assertTrue(fileRepository.exists(oldThumbnailPath));
		assertEquals(1, fileRepository.size());
		assertEquals(oldThumbnailPath, aasOperations.getAssetInformation(AAS_ID).getDefaultThumbnail().getPath());
	}

	private static AssetInformation createAssetInformationWithThumbnail(String path) {
		Resource thumbnail = new DefaultResource.Builder().path(path).contentType("image/png").build();
		AssetInformation assetInformation = new DefaultAssetInformation.Builder().build();
		assetInformation.setDefaultThumbnail(thumbnail);
		return assetInformation;
	}

	private static class InMemoryAasOperations implements AasOperations {
		protected AssetInformation assetInformation = new DefaultAssetInformation.Builder().build();

		@Override
		public void setAssetInformation(String aasId, AssetInformation assetInformation) {
			this.assetInformation = assetInformation;
		}

		@Override
		public AssetInformation getAssetInformation(String aasId) {
			return assetInformation;
		}

		@Override
		public CursorResult<List<AssetAdministrationShell>> getShells(List<SpecificAssetId> assetIds, String idShort, PaginationInfo pInfo) {
			throw new UnsupportedOperationException();
		}

		@Override
		public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addSubmodelReference(String aasId, Reference submodelReference) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void removeSubmodelReference(String aasId, String submodelId) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterable<AssetAdministrationShell> getAllAas(List<SpecificAssetId> assetIds, String idShort) {
			throw new UnsupportedOperationException();
		}
	}

	private static class FailingAasOperations extends InMemoryAasOperations {
		@Override
		public void setAssetInformation(String aasId, AssetInformation assetInformation) {
			throw new RuntimeException("Could not update asset information.");
		}
	}

	private static class GridFsLikeFileRepository implements FileRepository {
		private final Map<String, byte[]> store = new HashMap<>();

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
