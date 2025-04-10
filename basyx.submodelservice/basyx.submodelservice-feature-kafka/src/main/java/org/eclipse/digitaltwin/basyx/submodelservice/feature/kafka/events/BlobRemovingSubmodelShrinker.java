/*******************************************************************************
 * Copyright (C) 2024 DFKI GmbH (https://www.dfki.de/en/web)
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
 * 
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka.events;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.AnnotatedRelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.Blob;
import org.eclipse.digitaltwin.aas4j.v3.model.DataElement;
import org.eclipse.digitaltwin.aas4j.v3.model.Entity;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAnnotatedRelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultBlob;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEntity;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.springframework.util.CollectionUtils;

/**
 * @author geso02 (Sonnenberg DFKI GmbH)
 */
public class BlobRemovingSubmodelShrinker implements SubmodelShrinker {


	@Override
	public Submodel shrinkSubmodel(Submodel submodel) {		
		// do not retain blob content
		return new DefaultSubmodel.Builder().administration(submodel.getAdministration())
				.category(submodel.getCategory()).description(submodel.getDescription())
				.displayName(submodel.getDisplayName())
				.embeddedDataSpecifications(submodel.getEmbeddedDataSpecifications())
				.extensions(submodel.getExtensions()).id(submodel.getId()).idShort(submodel.getIdShort())
				.kind(submodel.getKind()).qualifiers(submodel.getQualifiers()).semanticId(submodel.getSemanticId())
				.submodelElements(shrinkSubmodelElements(submodel.getSubmodelElements()))
				.supplementalSemanticIds(submodel.getSupplementalSemanticIds()).build();
	}

	@Override
	public SubmodelElement shrinkSubmodelElement(SubmodelElement from) {
		if (from instanceof DataElement) {
			return shrinkDataElement((DataElement) from);
		} else if (from instanceof Entity) {
			return shrinkEntity((Entity) from);
		} else if (from instanceof SubmodelElementCollection) {
			return shrinkSubmodelElementCollection((SubmodelElementCollection) from);
		} else if (from instanceof SubmodelElementList) {
			return shrinkSubmodelElementList((SubmodelElementList) from);
		} else if (from instanceof Operation) {
			return shrinkOperation((Operation) from);
		} else if (from instanceof AnnotatedRelationshipElement) {
			return shrinkAnnotatedRelationshipElement((AnnotatedRelationshipElement)from);
		}
		return from;
	}

	private List<SubmodelElement> shrinkSubmodelElements(List<SubmodelElement> submodelElements) {
		if (CollectionUtils.isEmpty(submodelElements)) {
			return submodelElements;
		}
		return submodelElements.stream().map(this::shrinkSubmodelElement).collect(Collectors.toList());
	}

	private DataElement shrinkDataElement(DataElement from) {
		if (from instanceof Blob) {
			return shrinkBlob((Blob) from);
		}
		return from;
	}

	private AnnotatedRelationshipElement shrinkAnnotatedRelationshipElement(AnnotatedRelationshipElement from) {
		List<DataElement> annotations = from.getAnnotations();
		if (CollectionUtils.isEmpty(annotations)) {
			return from;
		}
		DefaultAnnotatedRelationshipElement toReturn = new DefaultAnnotatedRelationshipElement();
		
		toReturn.setFirst(from.getFirst());
		toReturn.setSecond(toReturn.getSecond());
		toReturn.setAnnotations(annotations.stream().map(this::shrinkDataElement).collect(Collectors.toList()));
		return applySubmodelElementValues(from, toReturn);
	}

	private Operation shrinkOperation(Operation from) {
		List<OperationVariable> inVars = from.getInputVariables();
		List<OperationVariable> inOutVars = from.getInoutputVariables();
		List<OperationVariable> outVars = from.getOutputVariables();
		if (CollectionUtils.isEmpty(inVars) && CollectionUtils.isEmpty(inOutVars) && CollectionUtils.isEmpty(outVars)) {
			return from;
		}
		DefaultOperation op = new DefaultOperation();
		if (!CollectionUtils.isEmpty(inVars)) {
			op.setInputVariables(inVars.stream().map(this::shrinkOperationVariable).collect(Collectors.toList()));
		}
		if (!CollectionUtils.isEmpty(inOutVars)) {
			op.setInoutputVariables(inOutVars.stream().map(this::shrinkOperationVariable).collect(Collectors.toList()));
		}
		if (!CollectionUtils.isEmpty(outVars)) {
			op.setOutputVariables(outVars.stream().map(this::shrinkOperationVariable).collect(Collectors.toList()));
		}
		return applySubmodelElementValues(from, op);
	}

	private OperationVariable shrinkOperationVariable(OperationVariable var) {
		SubmodelElement elem = var.getValue();
		if (elem == null) {
			return var;
		}
		return new DefaultOperationVariable.Builder().value(shrinkSubmodelElement(elem)).build();
	}

	private SubmodelElementList shrinkSubmodelElementList(SubmodelElementList from) {
		List<SubmodelElement> elements = from.getValue();
		if (CollectionUtils.isEmpty(elements)) {
			return from;
		}
		DefaultSubmodelElementList list = new DefaultSubmodelElementList();
		list.setValue(elements.stream().map(this::shrinkSubmodelElement).collect(Collectors.toList()));
		return applySubmodelElementValues(from, list);
	}

	private SubmodelElementCollection shrinkSubmodelElementCollection(SubmodelElementCollection from) {
		List<SubmodelElement> elements = from.getValue();
		if (CollectionUtils.isEmpty(elements)) {
			return from;
		}
		DefaultSubmodelElementCollection collection = new DefaultSubmodelElementCollection();
		collection.setValue(elements.stream().map(this::shrinkSubmodelElement).collect(Collectors.toList()));
		return applySubmodelElementValues(from, collection);
	}

	private Entity shrinkEntity(Entity from) {
		List<SubmodelElement> statements = from.getStatements();
		if (CollectionUtils.isEmpty(statements)) {
			return from;
		}
		DefaultEntity entity = new DefaultEntity();
		entity.setGlobalAssetId(from.getGlobalAssetId());
		entity.setEntityType(from.getEntityType());
		entity.setSpecificAssetIds(from.getSpecificAssetIds());
		entity.setStatements(statements.stream().map(this::shrinkSubmodelElement).collect(Collectors.toList()));
		return applySubmodelElementValues(from, entity);
	}

	private Blob shrinkBlob(Blob blob) {
		DefaultBlob toReturn = new DefaultBlob();
		toReturn.setContentType(blob.getContentType());
		// do not apply the value
		return applySubmodelElementValues(blob, toReturn);
	}

	private <T extends SubmodelElement> T applySubmodelElementValues(T from, T copy) {
		copy.setCategory(from.getCategory());
		copy.setDescription(from.getDescription());
		copy.setDisplayName(from.getDisplayName());
		copy.setEmbeddedDataSpecifications(from.getEmbeddedDataSpecifications());
		copy.setExtensions(from.getExtensions());
		copy.setIdShort(from.getIdShort());
		copy.setQualifiers(from.getQualifiers());
		copy.setSemanticId(from.getSemanticId());
		copy.setSupplementalSemanticIds(from.getSupplementalSemanticIds());
		return copy;
	}

}
