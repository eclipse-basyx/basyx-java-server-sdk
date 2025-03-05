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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.inmemory;

import com.querydsl.collections.CollQueryFactory;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AasDiscoveryDocument;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AasDiscoveryDocumentBackend;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.QAasDiscoveryDocument;
import org.eclipse.digitaltwin.basyx.common.backend.inmemory.core.InMemoryCrudRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * InMemory backend for the AasDiscoveryDocument based on InMemoryCrudRepository
 * 
 * @author mateusmolina
 */
@ConditionalOnExpression("'${basyx.backend}'.equals('InMemory')")
@Component
public class InMemoryAasDiscoveryDocumentBackend extends InMemoryCrudRepository<AasDiscoveryDocument> implements AasDiscoveryDocumentBackend, QuerydslPredicateExecutor<AasDiscoveryDocument> {

	public InMemoryAasDiscoveryDocumentBackend() {
		super(AasDiscoveryDocument::getShellIdentifier);
	}

	@Override
	public Optional<AasDiscoveryDocument> findOne(Predicate predicate) {
		AasDiscoveryDocument result = CollQueryFactory.from(QAasDiscoveryDocument.aasDiscoveryDocument, getInMemoryStore().values())
				.where(predicate)
				.fetchFirst();

		return Optional.ofNullable(result);
	}

	@Override
	public Iterable<AasDiscoveryDocument> findAll(Predicate predicate) {
		List<AasDiscoveryDocument> result = CollQueryFactory.from(QAasDiscoveryDocument.aasDiscoveryDocument, getInMemoryStore().values())
				.where(predicate)
				.fetch();

		return result;
	}

	@Override
	public Iterable<AasDiscoveryDocument> findAll(Predicate predicate, Sort sort) {
		throw new NotImplementedException("findAll(Predicate predicate, Sort sort) is not implemented");
	}

	@Override
	public Iterable<AasDiscoveryDocument> findAll(Predicate predicate, OrderSpecifier<?>... orders) {
		throw new NotImplementedException("findAll(Predicate predicate, OrderSpecifier<?>... orders) is not implemented");
	}

	@Override
	public Iterable<AasDiscoveryDocument> findAll(OrderSpecifier<?>... orders) {
		throw new NotImplementedException("findAll(OrderSpecifier<?>... orders) is not implemented");
	}

	@Override
	public Page<AasDiscoveryDocument> findAll(Predicate predicate, Pageable pageable) {
		throw new NotImplementedException("findAll(Predicate predicate, Pageable pageable) is not implemented");
	}

	@Override
	public long count(Predicate predicate) {
		return CollQueryFactory.from(QAasDiscoveryDocument.aasDiscoveryDocument, getInMemoryStore().values())
				.where(predicate)
				.fetchCount();
	}

	@Override
	public boolean exists(Predicate predicate) {
		return CollQueryFactory.from(QAasDiscoveryDocument.aasDiscoveryDocument, getInMemoryStore().values())
				.where(predicate)
				.fetchCount() > 0;
	}

	@Override
	public <S extends AasDiscoveryDocument, R> R findBy(Predicate predicate, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
		throw new NotImplementedException("findBy(Predicate predicate, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) is not implemented");
	}
}