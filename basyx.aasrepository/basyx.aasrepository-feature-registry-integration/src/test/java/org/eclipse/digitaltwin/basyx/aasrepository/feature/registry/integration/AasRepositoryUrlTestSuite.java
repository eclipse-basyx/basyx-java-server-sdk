package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import static org.junit.Assert.assertEquals;

import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.junit.Test;

public class AasRepositoryUrlTestSuite extends AasRepositoryRegistryLinkTestSuite  {

	private static final String AAS_REPO_URL = "http://localhost:8081";
	private static final String AAS_REGISTRY_BASE_URL = "http://localhost:8050";
	private static AasRepositoryRegistryLink aasRepositoryRegistryLink;
	

	private static final String AAS_REPOSITORY_PATH_WITH_SLASH = "/shells";
	private static final String AAS_REPOSITORY_PATH_WITHOUT_SLASH = "shells";

	@Test
    public void testUrlWithTrailingSlashAndPathWithoutLeadingSlash() {
        String baseURLWithSlash = getAasRepoBaseUrl() + "/";

        assertEquals(baseURLWithSlash + AAS_REPOSITORY_PATH_WITHOUT_SLASH, DummyAasDescriptorFactory.createAasRepositoryUrl(baseURLWithSlash));
    }
	
	@Test
    public void testUrlWithTrailingSlashAndPathWithLeadingSlash() {
        String baseURLWithSlash = getAasRepoBaseUrl() + "/";

        assertEquals(baseURLWithSlash + AAS_REPOSITORY_PATH_WITHOUT_SLASH, DummyAasDescriptorFactory.createAasRepositoryUrl(baseURLWithSlash));
    }

    @Test
    public void testUrlWithoutTrailingSlashAndPathWithoutLeadingSlash() {
        String baseURLWithoutSlash = getAasRepoBaseUrl();

        assertEquals(baseURLWithoutSlash + AAS_REPOSITORY_PATH_WITH_SLASH , DummyAasDescriptorFactory.createAasRepositoryUrl(baseURLWithoutSlash));
    }
    
    @Test
    public void testUrlWithoutTrailingSlashAndPathWithLeadingSlash() {
        String baseURLWithoutSlash = getAasRepoBaseUrl();

        assertEquals(baseURLWithoutSlash + AAS_REPOSITORY_PATH_WITH_SLASH , DummyAasDescriptorFactory.createAasRepositoryUrl(baseURLWithoutSlash));
    }
	
    @Override
	protected String getAasRepoBaseUrl() {
		return AAS_REPO_URL;
	}

	@Override
	protected String getAasRegistryUrl() {
		return AAS_REGISTRY_BASE_URL;
	}

	@Override
	protected RegistryAndDiscoveryInterfaceApi getAasRegistryApi() {
		
		return aasRepositoryRegistryLink.getRegistryApi();
	}
}
