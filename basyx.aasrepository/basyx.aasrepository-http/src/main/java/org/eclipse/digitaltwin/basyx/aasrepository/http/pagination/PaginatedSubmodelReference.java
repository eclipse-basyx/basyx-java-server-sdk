/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.aasrepository.http.pagination;

import java.util.List;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CursorNotFoundException;

/**
 * Paginated wrapper for {@link AasRepository#getSubmodelReferences(String)}
 * 
 * @author danish, patrice
 *
 */
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
