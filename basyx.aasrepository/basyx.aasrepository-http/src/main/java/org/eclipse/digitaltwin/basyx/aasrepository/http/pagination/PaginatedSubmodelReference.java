package org.eclipse.digitaltwin.basyx.aasrepository.http.pagination;

import java.util.List;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.core.exceptions.CursorNotFoundException;

public class PaginatedSubmodelReference {

	private List<Reference> references;
	private Integer limit;
	private String cursor;

	public PaginatedSubmodelReference(List<Reference> references, Integer limit,
			String cursor) {
		super();
		this.references = references;
		this.limit = limit;
		this.cursor = cursor;
	}

	public List<Reference> getReferences() {
		return references;
	}

	public Integer getLimit() {
		return limit;
	}

	public String getCursor() {
		return cursor;
	}

	public List<Reference> getPaginatedSubmodelReferences() {
		if (references.isEmpty())
			return this.references;
		
		int start = 0;

		if (cursor != null && !cursor.isBlank())
			start = getNextIndexFromCursor();

		int end = Math.min(start + limit, references.size());

		throwIfCursorNotExists(start, end);

		return references.subList(start, end);
	}

	private void throwIfCursorNotExists(int start, int end) {
		if (start <= end)
			return;
		
		throw new CursorNotFoundException(cursor);
	}

	private int getNextIndexFromCursor() {
		int index = 0;

		for (Reference reference : references) {
			if (isReferenceHasMatchingCursor(reference))
				return ++index;

			index++;
		}

		return ++index;
	}

	private boolean isReferenceHasMatchingCursor(Reference reference) {
		return reference.getKeys().stream().anyMatch(key -> key.getValue().equals(cursor));
	}

}
