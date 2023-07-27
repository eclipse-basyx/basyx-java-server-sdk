package org.eclipse.digitaltwin.basyx.core.pagination;

public class PaginationInfo {
	private Integer limit;
	private String cursor;

	public PaginationInfo(int limit, String cursor) {
		this.limit = limit;
		this.cursor = cursor;
	}

	public Integer getLimit() {
		return limit;
	}

	public String getCursor() {
		return cursor;
	}

	public boolean hasLimit() {
		return limit > 0;
	}

	public boolean hasCursor() {
		return cursor != null && !cursor.isEmpty();
	}

	public boolean isPaged() {
		return hasLimit() || hasCursor();
	}
}
