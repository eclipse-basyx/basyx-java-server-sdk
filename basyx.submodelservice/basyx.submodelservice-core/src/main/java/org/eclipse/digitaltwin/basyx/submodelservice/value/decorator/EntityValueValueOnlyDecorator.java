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

package org.eclipse.digitaltwin.basyx.submodelservice.value.decorator;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.basyx.submodelservice.value.EntityValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SpecificAssetIDValueValueOnly;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class EntityValueValueOnlyDecorator extends EntityValue {
	private EntityValue entityValue;

	public EntityValueValueOnlyDecorator(EntityValue entityValue) {
		super(entityValue.getStatements(), entityValue.getEntityType(), entityValue.getGlobalAssetId(), entityValue.getSpecificAssetIds());
		this.entityValue = entityValue;
	}

	/**
	 * returns a ValueOnly Serializable Verison of the SpecificAssetIds this is
	 * achieved through a simple conversion of each SpecificAssetIdValue to
	 * SpecificAssetIDValueValueOnly which is used by custom serializer in
	 * {@link SubmodelServiceHTTPSerializationExtension#extend(Jackson2ObjectMapperBuilder)}
	 */
	@Override
	public List<? extends SpecificAssetIDValueValueOnly> getSpecificAssetIds() {
		return entityValue.getSpecificAssetIds()
				.stream()
				.map(specificAssetIdValue -> new SpecificAssetIDValueValueOnly(specificAssetIdValue.toSpecificAssetID()))
				.collect(Collectors.toList());
	}
}
