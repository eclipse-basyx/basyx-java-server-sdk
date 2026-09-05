/*******************************************************************************
 * Copyright (C) 2026 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.internal.AasXmlNamespaceContext;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.internal.deserialization.SubmodelElementsDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.internal.serialization.SubmodelElementsSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * AAS4J's Entity XML mixin omits the {@code specificAssetId} item wrapper that
 * {@code AssetInformation} declares. Without it, Entity {@code specificAssetIds}
 * deserialize as empty objects and JSON GET responses emit {@code [{}]}.
 */
public abstract class EntitySpecificAssetIdsXmlMixin {

	@JacksonXmlProperty(namespace = AasXmlNamespaceContext.AAS_URI, localName = "statements")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonSerialize(using = SubmodelElementsSerializer.class)
	@JsonDeserialize(using = SubmodelElementsDeserializer.class)
	public abstract List<SubmodelElement> getStatements();

	@JacksonXmlProperty(namespace = AasXmlNamespaceContext.AAS_URI, localName = "globalAssetId")
	public abstract void setGlobalAssetID(String globalAssetID);

	@JacksonXmlProperty(namespace = AasXmlNamespaceContext.AAS_URI, localName = "specificAssetId")
	@JacksonXmlElementWrapper(namespace = AasXmlNamespaceContext.AAS_URI, localName = "specificAssetIds")
	public abstract List<SpecificAssetId> getSpecificAssetIds();

	@JacksonXmlProperty(namespace = AasXmlNamespaceContext.AAS_URI, localName = "specificAssetId")
	@JacksonXmlElementWrapper(namespace = AasXmlNamespaceContext.AAS_URI, localName = "specificAssetIds")
	public abstract void setSpecificAssetIds(List<SpecificAssetId> specificAssetIds);
}
