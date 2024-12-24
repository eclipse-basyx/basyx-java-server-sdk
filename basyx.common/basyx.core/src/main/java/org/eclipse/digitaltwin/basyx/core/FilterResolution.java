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

/**
 * The {@code FilterResolution} interface defines a contract for applying a {@link Filter} 
 * to produce a specific output type, {@code O}. This is intended to enable filtering 
 * operations across different storage or query mechanisms within the Eclipse BaSyx framework.
 * 
 * <p>The output type {@code O} represents the result of applying the filter, 
 * which may vary depending on the underlying implementation. For instance, 
 * it could be a {@code Predicate} for InMemory filtering, or an {@code AggregationOperations} 
 * object for MongoDB filtering.
 * 
 * @param <O> the type of output expected after applying the {@link Filter}, determined by the context 
 *            in which filtering is applied (e.g., InMemory, MongoDB, S3 etc.)
 *            
 * @author danish
 */
public interface FilterResolution<O> {
    
    /**
     * Applies the specified {@link Filter} to produce an output of type {@code O}.
     * 
     * <p>This method enables custom filtering logic depending on the storage mechanism
     * or query type. Implementing classes are expected to convert the filter criteria
     * into the desired format, whether for InMemory filtering, database filtering, or another purpose.
     *
     * @param filter the {@link Filter} containing criteria to be applied to the data
     * @return an instance of {@code O} representing the filtered output, formatted 
     *         according to the underlying filter resolution implementation
     * 
     */
    O applyFilter(Filter filter);

}

