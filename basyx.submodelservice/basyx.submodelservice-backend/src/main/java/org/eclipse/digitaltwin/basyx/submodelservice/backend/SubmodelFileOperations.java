package org.eclipse.digitaltwin.basyx.submodelservice.backend;

import java.io.InputStream;

import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;

public interface SubmodelFileOperations {

	java.io.File getFile(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException ;

	void setFileValue(String submodelId, String idShortPath, String fileName, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException ;

	void deleteFileValue(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException ;

    InputStream getInputStream(String submodelId, String filePath);

}
