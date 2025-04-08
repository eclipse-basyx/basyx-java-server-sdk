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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.h2.backend;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AasDiscoveryDocument;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AasDiscoveryDocumentBackend;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.h2.dto.SpecificAssetIdEntity;
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
 * Implementation of {@link AasDiscoveryDocumentBackend} for H2
 * @author fried
 */
@Service
@ConditionalOnProperty(name = "basyx.backend", havingValue = "InMemory")
public class H2AasDiscoveryDocumentBackend implements AasDiscoveryDocumentBackend {
    private final JpaAasDiscoveryDocumentRepository jpaRepo;

    public H2AasDiscoveryDocumentBackend(JpaAasDiscoveryDocumentRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public <S extends AasDiscoveryDocument> S save(S doc) {
        AasDiscoveryDocumentEntity entity = toEntity(doc);
        jpaRepo.save(entity);
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
        return jpaRepo.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsById(String id) {
        return jpaRepo.existsById(id);
    }

    @Override
    public Iterable<AasDiscoveryDocument> findAll() {
        return jpaRepo.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Iterable<AasDiscoveryDocument> findAllById(Iterable<String> ids) {
        return jpaRepo.findAllById(ids).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public long count() {
        return jpaRepo.count();
    }

    @Override
    public void deleteById(String id) {
        jpaRepo.deleteById(id);
    }

    @Override
    public void delete(AasDiscoveryDocument doc) {
        jpaRepo.delete(toEntity(doc));
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
        jpaRepo.deleteAll();
    }

    private AasDiscoveryDocumentEntity toEntity(AasDiscoveryDocument doc) {
        List<SpecificAssetIdEntity> specificAssetIdEntities = doc.getSpecificAssetIds().stream()
                .map(id -> new SpecificAssetIdEntity((DefaultSpecificAssetId) id))
                .toList();

        return new AasDiscoveryDocumentEntity(
                doc.getShellIdentifier(),
                doc.getAssetLinks(),
                specificAssetIdEntities
        );
    }

    private AasDiscoveryDocument toDomain(AasDiscoveryDocumentEntity entity) {
        List<SpecificAssetId> specificAssetIds = entity.getSpecificAssetIds().stream()
                .map(SpecificAssetIdEntity::toSpecificAssetId)
                .map(specificAssetId -> (SpecificAssetId) specificAssetId)
                .toList();

        return new AasDiscoveryDocument(
                entity.getShellIdentifier(),
                entity.getAssetLinks(),
                specificAssetIds
        );
    }

    @Override
    public Optional<AasDiscoveryDocument> findOne(Predicate predicate) {
        return jpaRepo.findOne(predicate).map(this::toDomain);
    }

    public List<AasDiscoveryDocumentEntity> IterableToList(Iterable<AasDiscoveryDocumentEntity> iterable){
        List<AasDiscoveryDocumentEntity> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    @Override
    public Iterable<AasDiscoveryDocument> findAll(Predicate predicate) {
        return IterableToList(jpaRepo.findAll(predicate)).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Iterable<AasDiscoveryDocument> findAll(Predicate predicate, Sort sort) {
        return IterableToList(jpaRepo.findAll(predicate, sort)).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Iterable<AasDiscoveryDocument> findAll(Predicate predicate, OrderSpecifier<?>... orders) {
        return IterableToList(jpaRepo.findAll(predicate, orders)).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Iterable<AasDiscoveryDocument> findAll(OrderSpecifier<?>... orders) {
        return IterableToList(jpaRepo.findAll(orders)).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<AasDiscoveryDocument> findAll(Predicate predicate, Pageable pageable) {
        return jpaRepo.findAll(predicate, pageable).map(this::toDomain);
    }

    @Override
    public long count(Predicate predicate) {
        return jpaRepo.count(predicate);
    }

    @Override
    public boolean exists(Predicate predicate) {
        return jpaRepo.exists(predicate);
    }

    @Override
    public <S extends AasDiscoveryDocument, R> R findBy(Predicate predicate, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new FeatureNotImplementedException("findBy not implemented for H2 backend");
    }
}
