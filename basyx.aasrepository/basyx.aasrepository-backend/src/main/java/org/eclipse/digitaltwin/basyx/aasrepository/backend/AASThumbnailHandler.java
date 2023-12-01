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

package org.eclipse.digitaltwin.basyx.aasrepository.backend;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Resource;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultResource;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AASThumbnailHandler {

	private static Logger logger = LoggerFactory.getLogger(AASThumbnailHandler.class);

	public static void updateThumbnail(AasRepository aasRepo, String aasId, String contentType, String filePath) {
		AssetInformation assetInfor = aasRepo.getAssetInformation(aasId);
		assetInfor.getDefaultThumbnail().setContentType(contentType);
		assetInfor.getDefaultThumbnail().setPath(filePath);
		aasRepo.setAssetInformation(aasId, assetInfor);
	}

	public static void setNewThumbnail(AasRepository aasRepo, String aasId, String contentType, String filePath) {
		Resource resource = new DefaultResource();
		resource.setContentType(contentType);
		resource.setPath(filePath);
		AssetInformation assetInfor = aasRepo.getAssetInformation(aasId);
		assetInfor.setDefaultThumbnail(resource);
		aasRepo.setAssetInformation(aasId, assetInfor);
	}

	public static void throwIfFileDoesNotExist(String aasId, Resource resource) {
		if (resource == null)
			throw new FileDoesNotExistException(aasId);

		String filePath = resource.getPath();
		throwIfFilePathIsNotValid(aasId, filePath);
	}

	public static String createFilePath(String tmpDirectory, String aasId, String fileName) {
		return tmpDirectory + "/" + aasId + "-" + "Thumbnail" + "-" + fileName;
	}

	public static void createFileAtSpecifiedPath(String fileName, InputStream inputStream, String filePath) {
		java.io.File targetFile = new java.io.File(filePath);

		try (FileOutputStream outStream = new FileOutputStream(targetFile)) {
			IOUtils.copy(inputStream, outStream);
		} catch (IOException e) {
			throw new FileHandlingException(fileName);
		}
	}

	public static void deleteExistingFile(String path) {
		if (path == null || path.isEmpty())
			return;

		try {
			Files.deleteIfExists(Paths.get(path, ""));
		} catch (IOException e) {
			logger.error("Unable to delete the file having path '{}'", path);
		}
	}

	public static String getTemporaryDirectoryPath() {
		String tempDirectoryPath = "";
		try {
			tempDirectoryPath = Files.createTempDirectory("basyx-temp-thumbnail").toAbsolutePath().toString();
		} catch (IOException e) {
			logger.error("Unable to create file in the temporary path.");
		}
		return tempDirectoryPath;
	}

	private static void throwIfFilePathIsNotValid(String aasId, String filePath) {
		if (filePath.isEmpty())
			throw new FileDoesNotExistException(aasId);
		try {
			Paths.get(filePath);
		} catch (InvalidPathException | NullPointerException ex) {
			throw new FileDoesNotExistException(aasId);
		}
	}
}
