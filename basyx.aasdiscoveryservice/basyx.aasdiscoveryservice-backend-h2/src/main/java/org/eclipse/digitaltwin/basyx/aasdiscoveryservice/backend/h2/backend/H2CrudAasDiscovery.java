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

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.Column;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AasDiscoveryDocument;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AasDiscoveryDocumentBackend;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.h2.dto.QSpecificAssetIdEntity;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.h2.dto.SpecificAssetIdEntity;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.core.exceptions.AssetLinkDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingAssetLinkException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryUtils.deriveAssetLinksFromSpecificAssetIds;

/**
 * H2 Implementation for the {@link AasDiscoveryService} based on Spring
 * {@link CrudRepository}
 * 
 * @author zielstor, fried, mateusmolina
 *
 */
public class H2CrudAasDiscovery implements AasDiscoveryService {

	private final AasDiscoveryDocumentBackend backend;
	private final String aasDiscoveryServiceName;
	private final JPAQueryFactory queryFactory;

	public H2CrudAasDiscovery(AasDiscoveryDocumentBackend backend, String aasDiscoveryServiceName, JPAQueryFactory queryFactory) {
		this.backend = backend;
		this.aasDiscoveryServiceName = aasDiscoveryServiceName;
		this.queryFactory = queryFactory;
	}

	/**
	 * Returns a list of Asset Administration Shell ids based on asset identifier
	 * key-value-pairs
	 *
	 * @param pInfo
	 *            pagination information
	 * @param assetIds
	 *            a list of asset links
	 * @return a list of Asset Administration Shell ids
	 */
	@Override
	public CursorResult<List<String>> getAllAssetAdministrationShellIdsByAssetLink(PaginationInfo pInfo, List<AssetLink> assetIds) {
		QAasDiscoveryDocumentEntity qDoc = QAasDiscoveryDocumentEntity.aasDiscoveryDocumentEntity;
		QSpecificAssetIdEntity specificAssetId = QSpecificAssetIdEntity.specificAssetIdEntity; // Alias for join

		BooleanExpression predicate = Expressions.FALSE;

		for (AssetLink asset : assetIds) {
			predicate = predicate.or(
					qDoc.assetLinks.any().name.eq(asset.getName())
							.and(qDoc.assetLinks.any().value.eq(asset.getValue()))
			);
		}

		List<AasDiscoveryDocumentEntity> aasDiscoveryDocuments = findAllWithJoin(predicate);

		Set<String> shellIds = aasDiscoveryDocuments.stream()
				.map(AasDiscoveryDocumentEntity::getShellIdentifier)
				.collect(Collectors.toSet());


		return paginateList(pInfo, new ArrayList<>(shellIds));
	}



	/**
	 * Returns a list of asset identifier key-value-pairs based on an Asset
	 * Administration Shell id
	 *
	 * @param shellIdentifier
	 *            the shell identifier
	 * @return a list of asset identifiers
	 */
	@Override
	public List<SpecificAssetId> getAllAssetLinksById(String shellIdentifier) {
		List<SpecificAssetId> assetIds = getAssetIds(shellIdentifier);
		if (assetIds.isEmpty()) {
			throw new AssetLinkDoesNotExistException(shellIdentifier);
		}
		return assetIds;
	}

	/**
	 * Creates new asset identifier key-value-pairs linked to an Asset
	 * Administration Shell for discoverable content. The existing content might
	 * have to be deleted first.
	 *
	 * @param shellIdentifier
	 *            the shell identifier
	 * @param specificAssetIds
	 *            a list of asset identifiers
	 * @return a list of asset identifiers
	 */
	@Override
	public List<SpecificAssetId> createAllAssetLinksById(String shellIdentifier,
														 List<SpecificAssetId> specificAssetIds) {
		QAasDiscoveryDocumentEntity qDoc = QAasDiscoveryDocumentEntity.aasDiscoveryDocumentEntity;
		BooleanExpression predicate = qDoc.shellIdentifier.eq(shellIdentifier);
		if (backend.exists(predicate)) {
			throw new CollidingAssetLinkException(shellIdentifier);
		}

		List<AssetLink> shellAssetLinks = deriveAssetLinksFromSpecificAssetIds(specificAssetIds);
		AasDiscoveryDocument aasDiscoveryDocument = new AasDiscoveryDocument(
				shellIdentifier,
				new HashSet<>(shellAssetLinks),
				specificAssetIds
		);

		aasDiscoveryDocument.getSpecificAssetIds().size();

		backend.save(aasDiscoveryDocument);
		return specificAssetIds;
	}


	/**
	 * Deletes all asset identifier key-value-pairs linked to an Asset
	 * Administration Shell
	 *
	 * @param shellIdentifier
	 *            the shell identifier
	 */
	@Override
	public void deleteAllAssetLinksById(String shellIdentifier) {
		QAasDiscoveryDocumentEntity qDoc = QAasDiscoveryDocumentEntity.aasDiscoveryDocumentEntity;
		BooleanExpression predicate = qDoc.shellIdentifier.eq(shellIdentifier);
		if(!backend.exists(predicate)){
			throw new AssetLinkDoesNotExistException(shellIdentifier);
		}
		backend.deleteById(shellIdentifier);
	}

	@Override
	public String getName(){
		return aasDiscoveryServiceName == null ? AasDiscoveryService.super.getName() : aasDiscoveryServiceName;
	}

	private List<SpecificAssetId> getAssetIds(String shellIdentifier) {
		QAasDiscoveryDocumentEntity qDoc = QAasDiscoveryDocumentEntity.aasDiscoveryDocumentEntity;
		BooleanExpression predicate = qDoc.shellIdentifier.eq(shellIdentifier);

		List<AasDiscoveryDocumentEntity> result = findAllWithJoin(predicate);

		return result.stream()
				.findFirst()
				.map(AasDiscoveryDocumentEntity::getSpecificAssetIds)
				.map(specificAssetIdEntities -> specificAssetIdEntities.stream()
						.map(SpecificAssetIdEntity::toSpecificAssetId)
						.map(specificAssetId -> (SpecificAssetId) specificAssetId)
						.toList()
				)
				.orElseThrow(() -> new AssetLinkDoesNotExistException(shellIdentifier));
	}

	private CursorResult<List<String>> paginateList(PaginationInfo pInfo, List<String> shellIdentifiers) {
		TreeMap<String, String> shellIdentifierMap = shellIdentifiers.stream()
				.collect(Collectors.toMap(Function.identity(), Function.identity(), (a, b) -> a, TreeMap::new));

		PaginationSupport<String> paginationSupport = new PaginationSupport<>(shellIdentifierMap, Function.identity());

		return paginationSupport.getPaged(pInfo);
	}

	private static List<AasDiscoveryDocument> convertIterableToList(Iterable<AasDiscoveryDocument> result) {
		List<AasDiscoveryDocument> aasDiscoveryDocuments = StreamSupport.stream(result.spliterator(), false)
				.collect(Collectors.toList());
		return aasDiscoveryDocuments;
	}

	private List<AasDiscoveryDocumentEntity> findAllWithJoin(Predicate predicate) {
		QSpecificAssetIdEntity specificAssetId = QSpecificAssetIdEntity.specificAssetIdEntity;
		QAasDiscoveryDocumentEntity qDoc = QAasDiscoveryDocumentEntity.aasDiscoveryDocumentEntity;

		return queryFactory
				.selectFrom(qDoc)
				.leftJoin(qDoc.specificAssetIds, specificAssetId).fetchJoin()
				.where(predicate)
				.fetch();
	}
}
