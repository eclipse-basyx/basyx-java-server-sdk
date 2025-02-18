package org.eclipse.digitaltwin.basyx.core.pagination;

import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

public class PaginationSupportTest {

	@Test
	public void testPagination() {
		PaginationSupport<String> support = getPaginationSupport();

		PaginationInfo info = new PaginationInfo(6, null);
		CursorResult<List<String>> cursorResult = support.getPaged(info);
		List<String> results = cursorResult.getResult();
		Assert.assertArrayEquals(new String[] { "0", "1", "2", "3", "4", "5" }, results.toArray(String[]::new));
		Assert.assertEquals("5", cursorResult.getCursor());
		info = new PaginationInfo(6, cursorResult.getCursor());
		cursorResult = support.getPaged(info);
		results = cursorResult.getResult();
		Assert.assertArrayEquals(new String[] { "6", "7", "8", "9" }, results.toArray(String[]::new));
		Assert.assertNull(cursorResult.getCursor());
	}

	@Test
	public void testPaginationNoLimit() {
		String cursor = "5";
		PaginationSupport<String> support = getPaginationSupport();
		PaginationInfo info = new PaginationInfo(10, cursor);
		CursorResult<List<String>> cursorResult = support.getPaged(info);
		Assert.assertNull(cursorResult.getCursor());
		Assert.assertArrayEquals(new String[] { "6", "7", "8", "9" }, cursorResult.getResult().toArray(String[]::new));
	}

	@Test
	public void testNoLimit() {
		PaginationSupport<String> support = getPaginationSupport();
		CursorResult<List<String>> cursorResult = support.getPaged(PaginationInfo.NO_LIMIT);
		Assert.assertEquals(null, cursorResult.getCursor());
		Assert.assertArrayEquals(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" }, cursorResult.getResult().toArray(String[]::new));
	}

	@Test
	public void testEmptyList() {
		PaginationSupport<String> support = 
				 new PaginationSupport<String>(new TreeMap<String, String>(), Function.identity());
		CursorResult<List<String>> cursorResult = support.getPaged(new PaginationInfo(100, null));
		Assert.assertEquals(0, cursorResult.getResult().size());
		Assert.assertNull(cursorResult.getCursor());		 
	}
	
	@Test
	public void testCursorAtEndOfList() {
		PaginationSupport<String> support = getPaginationSupport();
		CursorResult<List<String>> cursorResult = support.getPaged(new PaginationInfo(10, "9"));
		Assert.assertEquals(0, cursorResult.getResult().size());
		Assert.assertNull(cursorResult.getCursor());
	}

	private PaginationSupport<String> getPaginationSupport() {
		TreeMap<String, String> sorted = new TreeMap<>();
		IntStream.iterate(0, i -> ++i).limit(10).mapToObj(Integer::toString).forEach(i -> sorted.put(i, i));

		return new PaginationSupport<String>(sorted, Function.identity());
	}

}
