package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.http.pagination;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.basyx.core.exceptions.CursorNotFoundException;

public class PaginatedConceptDescription {

	private List<ConceptDescription> conceptDescriptions;
	private Integer limit;
	private String cursor;

	public PaginatedConceptDescription(List<ConceptDescription> conceptDescriptions, Integer limit,
			String cursor) {
		super();
		this.conceptDescriptions = conceptDescriptions;
		this.limit = limit;
		this.cursor = cursor;
	}

	public List<ConceptDescription> getConceptDescriptions() {
		return conceptDescriptions;
	}

	public Integer getLimit() {
		return limit;
	}

	public String getCursor() {
		return cursor;
	}

	public List<ConceptDescription> getPaginatedConceptDescriptions() {
		if (conceptDescriptions.isEmpty())
			return this.conceptDescriptions;
		
		int start = 0;

		if (cursor != null && !cursor.isBlank())
			start = getNextIndexFromCursor();

		int end = Math.min(start + limit, conceptDescriptions.size());

		throwIfCursorNotExists(start, end);

		return conceptDescriptions.subList(start, end);
	}

	private void throwIfCursorNotExists(int start, int end) {
		if (start <= end)
			return;
		
		throw new CursorNotFoundException(cursor);
	}

	private int getNextIndexFromCursor() {
		int index = 0;

		for (ConceptDescription conceptDescription : conceptDescriptions) {
			if (conceptDescription.getId().equals(cursor))
				return ++index;

			index++;
		}

		return ++index;
	}

}
