package org.eclipse.digitaltwin.basyx.core.pagination;

public class CursorResult<T> {
	private final String cursor;
	private final T result;

	public CursorResult(String cursor, T result) {
		this.cursor = cursor;
		this.result = result;
	}

	public String getCursor() {
		return cursor;
	}

	public T getResult() {
		return result;
	}
}