


package org.eclipse.digitaltwin.basyx.submodelservice;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;

/**
 * 
 * @author zhangzai
 *
 */
public class TestMongoDBSubmodelService extends SubmodelServiceSuite {

	@Override
	protected SubmodelService getSubmodelService(Submodel submodel) {
		return new InMemorySubmodelServiceFactory(new InMemoryFileRepository()).create(submodel);
	}

	@Override
	protected boolean fileExistsInStorage(String fileValue) {
		java.io.File file = new java.io.File(fileValue);

		return file.exists();
	}
}
