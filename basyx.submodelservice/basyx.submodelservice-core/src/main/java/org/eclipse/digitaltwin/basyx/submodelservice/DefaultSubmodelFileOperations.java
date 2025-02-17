package org.eclipse.digitaltwin.basyx.submodelservice;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileHandlingException;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileMetadata;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.submodelservice.value.FileBlobValue;

public class DefaultSubmodelFileOperations implements SubmodelFileOperations {
    private final FileRepository fileRepository;
    private final SubmodelOperations submodelOperations;
    
	public DefaultSubmodelFileOperations(FileRepository fileRepository, SubmodelOperations operations) {
        this.fileRepository = fileRepository;
        this.submodelOperations = operations;
    }

    @Override
	public java.io.File getFile(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		SubmodelElement submodelElement = submodelOperations.getSubmodelElement(submodelId, idShortPath);

		throwIfSmElementIsNotAFile(submodelElement);

		File fileSmElement = (File) submodelElement;
		String filePath = getFilePath(fileSmElement);

		InputStream fileContent = getFileInputStream(filePath);

		return createFile(filePath, fileContent);
	}

	@Override
	public void setFileValue(String submodelId, String idShortPath, String fileName, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
        SubmodelElement submodelElement = submodelOperations.getSubmodelElement(submodelId, idShortPath);

        throwIfSmElementIsNotAFile(submodelElement);

        File fileSmElement = (File) submodelElement;

        if (fileRepository.exists(fileSmElement.getValue()))
            fileRepository.delete(fileSmElement.getValue());

        String uniqueFileName = createUniqueFileName(submodelId, idShortPath, fileName);

        FileMetadata fileMetadata = new FileMetadata(uniqueFileName, fileSmElement.getContentType(), inputStream);

        if (fileRepository.exists(fileMetadata.getFileName()))
            fileRepository.delete(fileMetadata.getFileName());

        String filePath = fileRepository.save(fileMetadata);

        FileBlobValue fileValue = new FileBlobValue(fileSmElement.getContentType(), filePath);

        submodelOperations.setSubmodelElementValue(submodelId, idShortPath, fileValue);
	}

	@Override
	public void deleteFileValue(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
        SubmodelElement submodelElement = submodelOperations.getSubmodelElement(submodelId, idShortPath);

        throwIfSmElementIsNotAFile(submodelElement);

        File fileSubmodelElement = (File) submodelElement;
        String filePath = fileSubmodelElement.getValue();

        fileRepository.delete(filePath);

        FileBlobValue fileValue = new FileBlobValue(" ", " ");

        submodelOperations.setSubmodelElementValue(submodelId, idShortPath, fileValue);
    }

	@Override
	public InputStream getInputStream(String submodelId, String filePath) throws FileDoesNotExistException{
		return fileRepository.find(filePath);
	}

	private boolean isFileSubmodelElement(SubmodelElement submodelElement) {
		return submodelElement instanceof File;
	}

	private InputStream getFileInputStream(String filePath) {
		InputStream fileContent;

		try {
			fileContent = fileRepository.find(filePath);
		} catch (FileDoesNotExistException e) {
			throw new FileDoesNotExistException(String.format("File at path '%s' could not be found.", filePath));
		}

		return fileContent;
	}

	private java.io.File createFile(String filePath, InputStream fileIs) {

		try {
			byte[] content = fileIs.readAllBytes();
			fileIs.close();

			createOutputStream(filePath, content);

			return new java.io.File(filePath);
		} catch (IOException e) {
			throw new FileHandlingException("Exception occurred while creating file from the InputStream." + e.getMessage());
		}

	}

	private String getFilePath(File fileSubmodelElement) {
		return fileSubmodelElement.getValue();
	}

	private String createUniqueFileName(String submodelId, String idShortPath, String fileName) {
		return Base64UrlEncodedIdentifier.encodeIdentifier(submodelId) + "-" + idShortPath.replace("/", "-") + "-" + fileName;
	}

	private void throwIfSmElementIsNotAFile(SubmodelElement submodelElement) {

		if (!isFileSubmodelElement(submodelElement))
			throw new ElementNotAFileException(submodelElement.getIdShort());
	}

    private void createOutputStream(String filePath, byte[] content) throws IOException {

		try (OutputStream outputStream = new FileOutputStream(filePath)) {
			outputStream.write(content);
		} catch (IOException e) {
			throw new FileHandlingException("Exception occurred while creating OutputStream from byte[]." + e.getMessage());
		}

	}

}
