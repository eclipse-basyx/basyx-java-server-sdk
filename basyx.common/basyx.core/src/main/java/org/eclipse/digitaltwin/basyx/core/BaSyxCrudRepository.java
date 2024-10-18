/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.core;

import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

/**
 * The {@code BaSyxCrudRepository} interface extends the {@link CrudRepository}
 * to provide basic Create, Read, Update, and Delete (CRUD) operations with
 * additional support for pagination and filtering.
 * 
 * <p>
 * This interface is designed to facilitate the retrieval of paginated and
 * filtered data.
 *
 * @param <T>
 *            the type of entity that this repository manages
 * 
 * @see CrudRepository
 * 
 * @author danish
 */
public interface BaSyxCrudRepository<T> extends CrudRepository<T, String> {

	/**
	 * Retrieves all entities of type {@code T} that match the specified
	 * {@link Filter} criteria, applying the provided {@link PaginationInfo} to
	 * control the subset of results returned.
	 * 
	 * @param paginationInfo
	 * @param filter
	 *            a {@link Filter} object specifying the criteria to apply when
	 *            querying the entities
	 * @return an {@code Iterable} of entities of type {@code T} that meet the
	 *         filtering and pagination criteria
	 * @throws IllegalArgumentException
	 *             if {@code paginationInfo} or {@code filter} is null
	 */
	@NonNull
	Iterable<T> findAll(PaginationInfo paginationInfo, Filter filter);

}
