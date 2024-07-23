package org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration;

import static org.junit.Assert.assertEquals;

import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.junit.Test;

public class SubmodelRepositoryUrlTestSuite extends SubmodelRepositoryRegistryLinkTestSuite  {

	private static final String SUBMODEL_REPO_URL = "http://localhost:8081";
	private static final String SUBMODEL_REGISTRY_BASE_URL = "http://localhost:8060";
	private static SubmodelRepositoryRegistryLink submodelRepositoryRegistryLink;
	

	private static final String SUBMODEL_REPOSITORY_PATH_WITH_SLASH = "/submodels";
	private static final String SUBMODEL_REPOSITORY_PATH_WITHOUT_SLASH = "submodels";

	@Test
    public void testUrlWithTrailingSlashAndPathWithoutLeadingSlash() {
        String baseURLWithSlash = getSubmodelRepoBaseUrl() + "/";

        assertEquals(baseURLWithSlash + SUBMODEL_REPOSITORY_PATH_WITHOUT_SLASH, DummySubmodelDescriptorFactory.createSubmodelRepositoryUrl(baseURLWithSlash));
    }
	
	@Test
    public void testUrlWithTrailingSlashAndPathWithLeadingSlash() {
        String baseURLWithSlash = getSubmodelRepoBaseUrl() + "/";

        assertEquals(baseURLWithSlash + SUBMODEL_REPOSITORY_PATH_WITHOUT_SLASH, DummySubmodelDescriptorFactory.createSubmodelRepositoryUrl(baseURLWithSlash));
    }

    @Test
    public void testUrlWithoutTrailingSlashAndPathWithoutLeadingSlash() {
        String baseURLWithoutSlash = getSubmodelRepoBaseUrl();

        assertEquals(baseURLWithoutSlash + SUBMODEL_REPOSITORY_PATH_WITH_SLASH , DummySubmodelDescriptorFactory.createSubmodelRepositoryUrl(baseURLWithoutSlash));
    }
    
    @Test
    public void testUrlWithoutTrailingSlashAndPathWithLeadingSlash() {
        String baseURLWithoutSlash = getSubmodelRepoBaseUrl();

        assertEquals(baseURLWithoutSlash + SUBMODEL_REPOSITORY_PATH_WITH_SLASH , DummySubmodelDescriptorFactory.createSubmodelRepositoryUrl(baseURLWithoutSlash));
    }
	
	@Override
	protected String getSubmodelRepoBaseUrl() {
		return SUBMODEL_REPO_URL;
	}
	@Override
	protected String getSubmodelRegistryUrl() {
		return SUBMODEL_REGISTRY_BASE_URL;
	}
	@Override
	protected SubmodelRegistryApi getSubmodelRegistryApi() {
		
		return submodelRepositoryRegistryLink.getRegistryApi();
	}
}
