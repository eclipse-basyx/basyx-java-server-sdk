package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestAasDescriptorFactory {

	private static final String AAS_REPO_URL = "http://localhost:8081";
	
	private static final String AAS_REPOSITORY_PATH = "shells";

	@Test
    public void testUrlWithTrailingSlash() {
		assertEquals(AAS_REPO_URL + "/" + AAS_REPOSITORY_PATH, AasDescriptorFactory.createAasRepositoryUrl(AAS_REPO_URL + "/"));
    }
	
    @Test
    public void testUrlWithoutTrailingSlash() {
        assertEquals(AAS_REPO_URL + "/" + AAS_REPOSITORY_PATH , AasDescriptorFactory.createAasRepositoryUrl(AAS_REPO_URL));
    }
    
}