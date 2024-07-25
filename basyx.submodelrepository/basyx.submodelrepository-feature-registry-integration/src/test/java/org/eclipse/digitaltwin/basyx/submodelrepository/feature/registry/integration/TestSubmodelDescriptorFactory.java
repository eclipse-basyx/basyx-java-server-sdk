package org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestSubmodelDescriptorFactory {

	private static final String SUBMODEL_REPO_URL = "http://localhost:8081";
	
	private static final String SUBMODEL_REPOSITORY_PATH = "submodels";

	@Test
    public void testUrlWithTrailingSlash() {
        assertEquals(SUBMODEL_REPO_URL + "/" + SUBMODEL_REPOSITORY_PATH, SubmodelDescriptorFactory.createSubmodelRepositoryUrl(SUBMODEL_REPO_URL + "/"));
    }

    @Test
    public void testUrlWithoutTrailingSlash() {
        assertEquals(SUBMODEL_REPO_URL + "/" + SUBMODEL_REPOSITORY_PATH , SubmodelDescriptorFactory.createSubmodelRepositoryUrl(SUBMODEL_REPO_URL));
    }
}
