package org.eclipse.digitaltwin.basyx.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestHelper {

private static final String BASE_URL = "http://localhost:8081";
	
	private static final String PATH = "shells";

	@Test
    public void testUrlWithTrailingSlashAndPathNameWithNoLeadingSlash() {
		assertEquals(BASE_URL + "/" + PATH, Helper.createRepositoryUrl(BASE_URL + "/", PATH));
    }
	
    @Test
    public void testUrlWithoutTrailingSlashAndPathNameWithNoLeadingSlash() {
        assertEquals(BASE_URL + "/" + PATH , Helper.createRepositoryUrl(BASE_URL, PATH));
    }
    
    @Test
    public void testUrlWithTrailingSlashAndPathNameWithLeadingSlash() {
		assertEquals(BASE_URL + "/" + PATH, Helper.createRepositoryUrl(BASE_URL + "/", "/" + PATH));
    }
	
    @Test
    public void testUrlWithoutTrailingSlashAndPathNameWithLeadingSlash() {
        assertEquals(BASE_URL + "/" + PATH , Helper.createRepositoryUrl(BASE_URL, "/" + PATH));
    }
	
}
