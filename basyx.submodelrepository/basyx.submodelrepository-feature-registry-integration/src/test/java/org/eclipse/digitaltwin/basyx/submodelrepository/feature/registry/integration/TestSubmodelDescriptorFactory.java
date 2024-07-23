package org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestSubmodelDescriptorFactory {

	private static final String SUBMODEL_REPO_URL = "http://localhost:8081";
	

	private static final String SUBMODEL_REPOSITORY_PATH_WITH_SLASH = "/submodels";
	private static final String SUBMODEL_REPOSITORY_PATH_WITHOUT_SLASH = "submodels";

	@Test
    public void testUrlWithTrailingSlashAndPathWithoutLeadingSlash() {
        String baseURLWithSlash = SUBMODEL_REPO_URL + "/";

        assertEquals(baseURLWithSlash + SUBMODEL_REPOSITORY_PATH_WITHOUT_SLASH, SubmodelDescriptorFactory.createSubmodelRepositoryUrl(baseURLWithSlash));
    }
	
	@Test
    public void testUrlWithTrailingSlashAndPathWithLeadingSlash() {
        String baseURLWithSlash = SUBMODEL_REPO_URL + "/";

        assertEquals(baseURLWithSlash + SUBMODEL_REPOSITORY_PATH_WITHOUT_SLASH, SubmodelDescriptorFactory.createSubmodelRepositoryUrl(baseURLWithSlash));
    }

    @Test
    public void testUrlWithoutTrailingSlashAndPathWithoutLeadingSlash() {
        String baseURLWithoutSlash = SUBMODEL_REPO_URL;

        assertEquals(baseURLWithoutSlash + SUBMODEL_REPOSITORY_PATH_WITH_SLASH , SubmodelDescriptorFactory.createSubmodelRepositoryUrl(baseURLWithoutSlash));
    }
    
    @Test
    public void testUrlWithoutTrailingSlashAndPathWithLeadingSlash() {
        String baseURLWithoutSlash = SUBMODEL_REPO_URL;

        assertEquals(baseURLWithoutSlash + SUBMODEL_REPOSITORY_PATH_WITH_SLASH , SubmodelDescriptorFactory.createSubmodelRepositoryUrl(baseURLWithoutSlash));
    }
}
