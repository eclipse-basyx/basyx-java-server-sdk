package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestAasDescriptorFactory {

	private static final String AAS_REPO_URL = "http://localhost:8081";

	private static final String AAS_REPOSITORY_PATH_WITH_SLASH = "/shells";
	private static final String AAS_REPOSITORY_PATH_WITHOUT_SLASH = "shells";

	@Test
    public void testUrlWithTrailingSlashAndPathWithoutLeadingSlash() {
        String baseURLWithSlash = AAS_REPO_URL + "/";

        assertEquals(baseURLWithSlash + AAS_REPOSITORY_PATH_WITHOUT_SLASH, AasDescriptorFactory.createAasRepositoryUrl(baseURLWithSlash));
    }
	
	@Test
    public void testUrlWithTrailingSlashAndPathWithLeadingSlash() {
        String baseURLWithSlash = AAS_REPO_URL + "/";

        assertEquals(baseURLWithSlash + AAS_REPOSITORY_PATH_WITHOUT_SLASH, AasDescriptorFactory.createAasRepositoryUrl(baseURLWithSlash));
    }

    @Test
    public void testUrlWithoutTrailingSlashAndPathWithoutLeadingSlash() {
        String baseURLWithoutSlash = AAS_REPO_URL;

        assertEquals(baseURLWithoutSlash + AAS_REPOSITORY_PATH_WITH_SLASH , AasDescriptorFactory.createAasRepositoryUrl(baseURLWithoutSlash));
    }
    
    @Test
    public void testUrlWithoutTrailingSlashAndPathWithLeadingSlash() {
        String baseURLWithoutSlash = AAS_REPO_URL;

        assertEquals(baseURLWithoutSlash + AAS_REPOSITORY_PATH_WITH_SLASH , AasDescriptorFactory.createAasRepositoryUrl(baseURLWithoutSlash));
    }
}
