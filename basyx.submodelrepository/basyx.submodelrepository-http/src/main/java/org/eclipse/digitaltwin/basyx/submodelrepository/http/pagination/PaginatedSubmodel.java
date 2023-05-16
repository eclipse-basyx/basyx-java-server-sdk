package org.eclipse.digitaltwin.basyx.submodelrepository.http.pagination;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.core.exceptions.CursorNotFoundException;

public class PaginatedSubmodel {

	private List<Submodel> submodels;
	private Integer limit;
	private String cursor;

	public PaginatedSubmodel(List<Submodel> submodels, Integer limit,
			String cursor) {
		super();
		this.submodels = submodels;
		this.limit = limit;
		this.cursor = cursor;
	}

	public List<Submodel> getSubmodels() {
		return submodels;
	}

	public Integer getLimit() {
		return limit;
	}

	public String getCursor() {
		return cursor;
	}

	public List<Submodel> getPaginatedSubmodels() {
		if (submodels.isEmpty())
			return this.submodels;
		
		int start = 0;

		if (cursor != null && !cursor.isBlank())
			start = getNextIndexFromCursor();

		int end = Math.min(start + limit, submodels.size());

		throwIfCursorNotExists(start, end);

		return submodels.subList(start, end);
	}

	private void throwIfCursorNotExists(int start, int end) {
		if (start <= end)
			return;
		
		throw new CursorNotFoundException(cursor);
	}

	private int getNextIndexFromCursor() {
		int index = 0;

		for (Submodel submodel : submodels) {
			if (submodel.getId().equals(cursor))
				return ++index;

			index++;
		}

		return ++index;
	}

}
