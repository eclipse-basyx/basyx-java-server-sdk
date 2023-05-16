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

package org.eclipse.digitaltwin.basyx.submodelrepository.http.pagination;

import java.util.List;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.CursorNotFoundException;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;

/**
 * Paginated wrapper for {@link SubmodelRepository#getSubmodelElements(String)}
 * 
 * @author danish, patrice
 *
 */
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
