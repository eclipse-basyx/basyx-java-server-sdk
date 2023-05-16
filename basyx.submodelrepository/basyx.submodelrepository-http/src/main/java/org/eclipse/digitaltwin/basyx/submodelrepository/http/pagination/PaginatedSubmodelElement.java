package org.eclipse.digitaltwin.basyx.submodelrepository.http.pagination;

import java.util.List;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.CursorNotFoundException;

public class PaginatedSubmodelElement {

	private List<SubmodelElement> submodelElements;
	private Integer limit;
	private String cursor;

	public PaginatedSubmodelElement(List<SubmodelElement> submodelElements, Integer limit,
			String cursor) {
		super();
		this.submodelElements = submodelElements;
		this.limit = limit;
		this.cursor = cursor;
	}

	public List<SubmodelElement> getSubmodelElements() {
		return submodelElements;
	}

	public Integer getLimit() {
		return limit;
	}

	public String getCursor() {
		return cursor;
	}

	public List<SubmodelElement> getPaginatedSubmodelElements() {
		if (submodelElements.isEmpty())
			return this.submodelElements;
		
		int start = 0;

		if (cursor != null && !cursor.isBlank())
			start = getNextIndexFromCursor();

		int end = Math.min(start + limit, submodelElements.size());

		throwIfCursorNotExists(start, end);

		return submodelElements.subList(start, end);
	}

	private void throwIfCursorNotExists(int start, int end) {
		if (start <= end)
			return;
		
		throw new CursorNotFoundException(cursor);
	}

	private int getNextIndexFromCursor() {
		int index = 0;

		for (SubmodelElement submodelElement : submodelElements) {
			if (submodelElement.getIdShort().equals(cursor))
				return ++index;

			index++;
		}

		return ++index;
	}

}
