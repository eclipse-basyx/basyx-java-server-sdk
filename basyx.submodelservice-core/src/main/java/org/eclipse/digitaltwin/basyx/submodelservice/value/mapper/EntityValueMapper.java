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
package org.eclipse.digitaltwin.basyx.submodelservice.value.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.Entity;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.submodelservice.value.EntityValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ReferenceValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SpecificAssetIdValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.ValueOnly;
import org.eclipse.digitaltwin.basyx.submodelservice.value.factory.SubmodelElementValueMapperFactory;

/**
 * Maps {@link Entity} value to {@link EntityValue}
 * 
 * @author danish
 *
 */
public class EntityValueMapper implements ValueMapper<EntityValue> {
	private Entity entity;

	public EntityValueMapper(Entity entity) {
		this.entity = entity;
	}

	@Override
	public EntityValue getValue() {
		return new EntityValue(createValueOnly(entity.getStatements()), entity.getEntityType(),
				createReferenceValue(entity.getGlobalAssetId()), getSpecificAssetIdValue(entity.getSpecificAssetId()));
	}

	@Override
	public void setValue(EntityValue entityValue) {
		setStatements(entity.getStatements(), entityValue.getStatements());
		entity.setEntityType(entityValue.getEntityType());
		setGlobalAssetId(entityValue.getGlobalAssetId());
		setSpecificAssetId(entityValue.getSpecificAssetIds());
	}

	private void setSpecificAssetId(List<SpecificAssetIdValue> specificAssetIds) {
		if (entity.getSpecificAssetId() == null)
			return;

		entity.getSpecificAssetId().setName(specificAssetIds.get(0).getName());
		entity.getSpecificAssetId().setValue(specificAssetIds.get(0).getValue());
	}

	private void setGlobalAssetId(ReferenceValue referenceValue) {
		if (entity.getGlobalAssetId() == null)
			return;

		entity.getGlobalAssetId().setType(referenceValue.getType());
		entity.getGlobalAssetId().setKeys(referenceValue.getKeys());
	}

	private void setStatements(List<SubmodelElement> submodelElements, List<ValueOnly> valueOnlies) {
		submodelElements.stream().forEach(annotation -> setValue(annotation, valueOnlies));
	}

	private void setValue(SubmodelElement submodelElement, List<ValueOnly> valueOnlies) {
		ValueMapper<SubmodelElementValue> valueMapper = new SubmodelElementValueMapperFactory().create(submodelElement);

		valueMapper.setValue(ValueMapperUtil.getSubmodelElementValue(submodelElement, valueOnlies));
	}

	private List<ValueOnly> createValueOnly(List<SubmodelElement> statements) {
		return statements.stream().map(ValueMapperUtil::toValueOnly).collect(Collectors.toList());
	}

	private ReferenceValue createReferenceValue(Reference globalAssetId) {
		return new ReferenceValue(globalAssetId.getType(), globalAssetId.getKeys());
	}

	private List<SpecificAssetIdValue> getSpecificAssetIdValue(SpecificAssetId specificAssetId) {
		return Arrays.asList(new SpecificAssetIdValue(specificAssetId.getName(), specificAssetId.getValue()));
	}
}
