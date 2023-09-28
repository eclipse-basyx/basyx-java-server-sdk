package org.eclipse.digitaltwin.basyx.http.pagination;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests Base64UrlEncodedCursor
 * 
 * @author mateusmolina
 * 
 */
public class TestBase64UrlEncodedCursor {
	private static final String DECODED_CURSOR = "testCursor";
	private static final String ENCODED_CURSOR = "dGVzdEN1cnNvcg";
	private static final String TO_STRING_OUTPUT = "Base64UrlEncodedCursor [cursor=" + ENCODED_CURSOR + "]";

	@Test
	public void testEncodeCursor() {
		String actualEncodedCursor = Base64UrlEncodedCursor.encodeCursor(DECODED_CURSOR);
		assertEquals(ENCODED_CURSOR, actualEncodedCursor);
	}

	@Test
	public void testDecodeCursor() {
		String actualDecodedCursor = Base64UrlEncodedCursor.decodeCursor(ENCODED_CURSOR);
		assertEquals(DECODED_CURSOR, actualDecodedCursor);
	}

	@Test
	public void testFromUnencodedCursor() {
		Base64UrlEncodedCursor cursorObj = Base64UrlEncodedCursor.fromUnencodedCursor(DECODED_CURSOR);
		assertEquals(ENCODED_CURSOR, cursorObj.getEncodedCursor());
	}

	@Test
	public void testGetDecodedCursor() {
		Base64UrlEncodedCursor cursorObj = new Base64UrlEncodedCursor(ENCODED_CURSOR);
		assertEquals(DECODED_CURSOR, cursorObj.getDecodedCursor());
	}

	@Test
	public void testGetEncodedCursor() {
		Base64UrlEncodedCursor cursorObj = new Base64UrlEncodedCursor(ENCODED_CURSOR);
		assertEquals(ENCODED_CURSOR, cursorObj.getEncodedCursor());
	}

	@Test
	public void testToString() {
		Base64UrlEncodedCursor cursorObj = new Base64UrlEncodedCursor(ENCODED_CURSOR);
		assertEquals(TO_STRING_OUTPUT, cursorObj.toString());
	}

}
