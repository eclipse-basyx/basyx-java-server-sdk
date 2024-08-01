package org.eclipse.digitaltwin.basyx.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestRepositoryUrlHelper {

	private static final String BASE_URL = "http://localhost:8081";
	private static final String BASE_URL_WITH_CONTEXT = "http://localhost:8081/context";
	private static final String ADDITIONAL_PATH = "shells";
	
	private static final String EXPECTED_URL = BASE_URL + "/" + ADDITIONAL_PATH;
	private static final String EXPECTED_URL_WITH_CONTEXT = BASE_URL_WITH_CONTEXT + "/" + ADDITIONAL_PATH;

	@Test
    public void testUrlWithTrailingSlashAndPathNameWithNoLeadingSlash() {
		assertEquals(EXPECTED_URL, RepositoryUrlHelper.createRepositoryUrl(BASE_URL + "/", ADDITIONAL_PATH));
    }
	
    @Test
    public void testUrlWithoutTrailingSlashAndPathNameWithNoLeadingSlash() {
    	assertEquals(EXPECTED_URL, RepositoryUrlHelper.createRepositoryUrl(BASE_URL, ADDITIONAL_PATH));
    }
    
    @Test
    public void testUrlWithTrailingSlashAndPathNameWithLeadingSlash() {
    	assertEquals(EXPECTED_URL, RepositoryUrlHelper.createRepositoryUrl(BASE_URL + "/", "/" + ADDITIONAL_PATH));
    }
	
    @Test
    public void testUrlWithoutTrailingSlashAndPathNameWithLeadingSlash() {
    	assertEquals(EXPECTED_URL, RepositoryUrlHelper.createRepositoryUrl(BASE_URL, "/" + ADDITIONAL_PATH));
    }
    
    @Test
    public void testUrlWithContextAndTrailingSlashAndPathNameWithLeadingSlash() {
    	assertEquals(EXPECTED_URL_WITH_CONTEXT, RepositoryUrlHelper.createRepositoryUrl(BASE_URL_WITH_CONTEXT + "/", "/" + ADDITIONAL_PATH));
    }
    
    @Test
    public void testUrlWithContextAndNoTrailingSlashAndPathNameWithLeadingSlash() {
    	assertEquals(EXPECTED_URL_WITH_CONTEXT, RepositoryUrlHelper.createRepositoryUrl(BASE_URL_WITH_CONTEXT, "/" + ADDITIONAL_PATH));
    }
    
    @Test
    public void testUrlWithContextAndTrailingSlashAndPathNameWithNoLeadingSlash() {
    	assertEquals(EXPECTED_URL_WITH_CONTEXT, RepositoryUrlHelper.createRepositoryUrl(BASE_URL_WITH_CONTEXT + "/", ADDITIONAL_PATH));
    }
    
    @Test
    public void testUrlWithContextAndNoTrailingSlashAndPathNameWithNoLeadingSlash() {
    	assertEquals(EXPECTED_URL_WITH_CONTEXT, RepositoryUrlHelper.createRepositoryUrl(BASE_URL_WITH_CONTEXT, ADDITIONAL_PATH));
    }
}
