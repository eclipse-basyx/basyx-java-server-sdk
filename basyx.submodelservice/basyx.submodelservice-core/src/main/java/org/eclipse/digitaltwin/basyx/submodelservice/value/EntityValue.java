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
package org.eclipse.digitaltwin.basyx.submodelservice.value;

import java.util.List;
import java.util.Optional;

import org.eclipse.digitaltwin.aas4j.v3.model.Entity;
import org.eclipse.digitaltwin.aas4j.v3.model.EntityType;

/**
 * Represents the submodel element {@link Entity} value
 * 
 * @author danish
 *
 */
public class EntityValue implements SubmodelElementValue {
	private List<ValueOnly> statements;
	private EntityType entityType;
	private Optional<String> globalAssetId = Optional.empty();
	private Optional<List< SpecificAssetIdValue>> specificAssetIds = Optional.empty();
	
	@SuppressWarnings("unused")
	private EntityValue() {
		super();
	}
	
	public EntityValue(List<ValueOnly> statements, EntityType entityType, String globalAssetId,
			List< SpecificAssetIdValue> specificAssetIds) {
		this.statements = statements;
		this.entityType = entityType;
		this.globalAssetId = Optional.ofNullable(globalAssetId);
		this.specificAssetIds = Optional.ofNullable(specificAssetIds);
	}

	public List<ValueOnly> getStatements() {
		return statements;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public String getGlobalAssetId() {
		return globalAssetId.orElse(null);
	}

	public List<SpecificAssetIdValue> getSpecificAssetIds() {
		return specificAssetIds.orElse(null);
	}
	
}
