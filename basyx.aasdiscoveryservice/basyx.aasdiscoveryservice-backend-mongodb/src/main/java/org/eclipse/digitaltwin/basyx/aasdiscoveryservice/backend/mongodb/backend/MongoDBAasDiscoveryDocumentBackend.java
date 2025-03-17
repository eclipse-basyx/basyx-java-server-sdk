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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.mongodb.backend;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AasDiscoveryDocument;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AasDiscoveryDocumentBackend;
import org.eclipse.digitaltwin.basyx.core.exceptions.FeatureNotImplementedException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementation of {@link AasDiscoveryDocumentBackend} for MongoDB
 * @author fried
 */
@Service
@ConditionalOnProperty(name = "basyx.backend", havingValue = "MongoDB")
public class MongoDBAasDiscoveryDocumentBackend  implements AasDiscoveryDocumentBackend {
    private final MongoDBAasDiscoveryDocumentRepository repository;

    public MongoDBAasDiscoveryDocumentBackend(MongoDBAasDiscoveryDocumentRepository repository) {
        this.repository = repository;
    }

    @Override
    public <S extends AasDiscoveryDocument> S save(S doc) {
        AasDiscoveryDocumentEntity entity = toEntity(doc);
        repository.save(entity);
        return doc;
    }

    @Override
    public <S extends AasDiscoveryDocument> Iterable<S> saveAll(Iterable<S> docs) {
        for (S doc : docs) {
            save(doc);
        }
        return docs;
    }

    @Override
    public Optional<AasDiscoveryDocument> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    @Override
    public Iterable<AasDiscoveryDocument> findAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Iterable<AasDiscoveryDocument> findAllById(Iterable<String> ids) {
        return repository.findAllById(ids).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public void delete(AasDiscoveryDocument doc) {
        repository.delete(toEntity(doc));
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        for (String id : strings) {
            deleteById(id);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends AasDiscoveryDocument> docs) {
        for (AasDiscoveryDocument doc : docs) {
            delete(doc);
        }
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    private AasDiscoveryDocumentEntity toEntity(AasDiscoveryDocument doc) {
        return new AasDiscoveryDocumentEntity(doc.getShellIdentifier(),doc.getAssetLinks(),doc.getSpecificAssetIds());
    }

    private AasDiscoveryDocument toDomain(AasDiscoveryDocumentEntity entity) {
        return new AasDiscoveryDocument(entity.getShellIdentifier(),entity.getAssetLinks(),entity.getSpecificAssetIds());
    }

    @Override
    public Optional<AasDiscoveryDocument> findOne(Predicate predicate) {
        return repository.findOne(predicate).map(this::toDomain);
    }

    public List<AasDiscoveryDocumentEntity> IterableToList(Iterable<AasDiscoveryDocumentEntity> iterable){
        List<AasDiscoveryDocumentEntity> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    @Override
    public Iterable<AasDiscoveryDocument> findAll(Predicate predicate) {
        return IterableToList(repository.findAll(predicate)).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Iterable<AasDiscoveryDocument> findAll(Predicate predicate, Sort sort) {
        return IterableToList(repository.findAll(predicate, sort)).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Iterable<AasDiscoveryDocument> findAll(Predicate predicate, OrderSpecifier<?>... orders) {
        return IterableToList(repository.findAll(predicate, orders)).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Iterable<AasDiscoveryDocument> findAll(OrderSpecifier<?>... orders) {
        return IterableToList(repository.findAll(orders)).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<AasDiscoveryDocument> findAll(Predicate predicate, Pageable pageable) {
        return repository.findAll(predicate, pageable).map(this::toDomain);
    }

    @Override
    public long count(Predicate predicate) {
        return repository.count(predicate);
    }

    @Override
    public boolean exists(Predicate predicate) {
        return repository.exists(predicate);
    }

    @Override
    public <S extends AasDiscoveryDocument, R> R findBy(Predicate predicate, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new FeatureNotImplementedException("findBy not implemented for MongoDB backend");
    }
}
