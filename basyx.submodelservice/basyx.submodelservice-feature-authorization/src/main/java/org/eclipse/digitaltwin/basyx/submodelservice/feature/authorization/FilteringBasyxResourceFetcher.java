/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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
package org.eclipse.digitaltwin.basyx.submodelservice.feature.authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.digitaltwin.aas4j.v3.model.Referable;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * Generic approach for authorized filtering
 * @author Gerhard Sonnenberg (DFKI GmbH)
 */
public class FilteringBasyxResourceFetcher<R extends Referable> implements BasyxResourceFetcher<R> {

	private final BasyxResourceFetcher<R> decorated;
	private final Function<CursorResult<List<R>>, CursorResult<List<R>>> filter;

	public FilteringBasyxResourceFetcher(BasyxResourceFetcher<R> decorated, Function<CursorResult<List<R>>, CursorResult<List<R>>> filter) {
		this.decorated = decorated;
		this.filter = filter;
	}

	@Override
	public CursorResult<List<R>> fetch(PaginationInfo info) {
		Integer limit = info.getLimit();
		if (limit == null) {
			// where is no limit -> just fetch all and filter
			// -> cursor will be empty in the returned result
			CursorResult<List<R>> result = decorated.fetch(info);
			return filter.apply(result);
		}
		// we will refetch until the amount matches the limit
		// or there is no data left to fetch

		// start the refetch cyle with an empty result
		CursorResult<List<R>> lastCursorResult = emptyCursorResult(info.getCursor());
		do {
			int remaining = limit - lastCursorResult.getResult().size();
			if (remaining == 0) {
				break;
			}
			CursorResult<List<R>> result = decorated.fetch(new PaginationInfo(remaining, lastCursorResult.getCursor()));
			result = filter.apply(result);
			lastCursorResult = combine(lastCursorResult, result);
		} while (lastCursorResult.getCursor() != null);
		return lastCursorResult;
	}

	private CursorResult<List<R>> combine(CursorResult<List<R>> lastResult, CursorResult<List<R>> currentResult) {
		List<R> combined = new ArrayList<>();
		combined.addAll(lastResult.getResult());
		combined.addAll(currentResult.getResult());
		return new CursorResult<>(currentResult.getCursor(), combined);
	}

	private CursorResult<List<R>> emptyCursorResult(String cursor) {
		return new CursorResult<>(cursor, List.of());
	}
}