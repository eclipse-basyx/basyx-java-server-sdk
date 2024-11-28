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

package org.eclipse.digitaltwin.basyx.aasenvironment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.visitor.AssetAdministrationShellElementWalkerVisitor;
import org.eclipse.digitaltwin.aas4j.v3.model.AnnotatedRelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.BasicEventElement;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Entity;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Extension;
import org.eclipse.digitaltwin.aas4j.v3.model.HasExtensions;
import org.eclipse.digitaltwin.aas4j.v3.model.HasSemantics;
import org.eclipse.digitaltwin.aas4j.v3.model.Identifiable;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.MultiLanguageProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifiable;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifier;
import org.eclipse.digitaltwin.aas4j.v3.model.Referable;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceElement;
import org.eclipse.digitaltwin.aas4j.v3.model.RelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;

/**
 * Collects all the IDs of {@link ConceptDescription} from the
 * {@link Environment}
 * 
 * @author danish
 *
 */
public class ConceptDescriptionIdCollector implements AssetAdministrationShellElementWalkerVisitor {

	private Environment env;
	private Set<String> conceptDescriptionIds = new HashSet<>();

	public ConceptDescriptionIdCollector(Environment env) {
		this.env = env;
	}

	/**
	 * Collects all the IDs of {@link ConceptDescription} by visiting all the
	 * elements of type {@link HasSemantics}
	 * 
	 * @return set containing IDs of found {@link ConceptDescription}
	 */
	public Set<String> collect() {
		visit(env);

		return conceptDescriptionIds;
	}

	@Override
	public void visit(HasSemantics hasSemantics) {
		if (hasSemantics == null) {
			return;
		}

		hasSemantics.getSupplementalSemanticIds().forEach(this::visit);

		if (hasSemantics.getSemanticId() == null)
			return;

		visit(hasSemantics.getSemanticId());
		conceptDescriptionIds.addAll(getConceptDescriptionIds(hasSemantics.getSemanticId().getKeys()));
	}

	@Override
	public void visit(AnnotatedRelationshipElement annotatedRelationshipElement) {
		if (annotatedRelationshipElement == null) {
			return;
		}

		visit((HasSemantics) annotatedRelationshipElement);

		AssetAdministrationShellElementWalkerVisitor.super.visit(annotatedRelationshipElement);
	}

	@Override
	public void visit(BasicEventElement basicEvent) {
		if (basicEvent == null) {
			return;
		}

		visit((HasSemantics) basicEvent);

		AssetAdministrationShellElementWalkerVisitor.super.visit(basicEvent);
	}

	@Override
	public void visit(HasExtensions hasExtensions) {
		if (hasExtensions == null) {
			return;
		}

		if (hasExtensions instanceof HasSemantics)
			visit((HasSemantics) hasExtensions);

		AssetAdministrationShellElementWalkerVisitor.super.visit(hasExtensions);
	}

	@Override
	public void visit(Identifiable identifiable) {
		if (identifiable == null) {
			return;
		}

		if (identifiable instanceof HasSemantics)
			visit((HasSemantics) identifiable);

		AssetAdministrationShellElementWalkerVisitor.super.visit(identifiable);
	}

	@Override
	public void visit(SpecificAssetId specificAssetId) {
		if (specificAssetId == null) {
			return;
		}

		visit((HasSemantics) specificAssetId);

		AssetAdministrationShellElementWalkerVisitor.super.visit(specificAssetId);
	}

	@Override
	public void visit(MultiLanguageProperty multiLanguageProperty) {
		if (multiLanguageProperty == null) {
			return;
		}

		visit((HasSemantics) multiLanguageProperty);

		AssetAdministrationShellElementWalkerVisitor.super.visit(multiLanguageProperty);
	}

	@Override
	public void visit(Property property) {
		if (property == null) {
			return;
		}

		visit((HasSemantics) property);

		AssetAdministrationShellElementWalkerVisitor.super.visit(property);
	}

	@Override
	public void visit(Qualifiable qualifiable) {
		if (qualifiable == null) {
			return;
		}

		if (qualifiable instanceof HasSemantics)
			visit((HasSemantics) qualifiable);

		AssetAdministrationShellElementWalkerVisitor.super.visit(qualifiable);
	}

	@Override
	public void visit(Qualifier qualifier) {
		if (qualifier == null) {
			return;
		}

		visit((HasSemantics) qualifier);

		AssetAdministrationShellElementWalkerVisitor.super.visit(qualifier);
	}

	@Override
	public void visit(Referable referable) {
		if (referable == null) {
			return;
		}

		if (referable instanceof HasSemantics)
			visit((HasSemantics) referable);

		AssetAdministrationShellElementWalkerVisitor.super.visit(referable);
	}

	@Override
	public void visit(ReferenceElement referenceElement) {
		if (referenceElement == null) {
			return;
		}

		visit((HasSemantics) referenceElement);

		AssetAdministrationShellElementWalkerVisitor.super.visit(referenceElement);
	}

	@Override
	public void visit(RelationshipElement relationshipElement) {
		if (relationshipElement == null) {
			return;
		}

		visit((HasSemantics) relationshipElement);

		AssetAdministrationShellElementWalkerVisitor.super.visit(relationshipElement);
	}

	@Override
	public void visit(Entity entity) {
		if (entity == null) {
			return;
		}

		visit((HasSemantics) entity);

		AssetAdministrationShellElementWalkerVisitor.super.visit(entity);
	}

	@Override
	public void visit(Extension extension) {
		if (extension == null) {
			return;
		}

		visit((HasSemantics) extension);

		AssetAdministrationShellElementWalkerVisitor.super.visit(extension);
	}

	@Override
	public void visit(Submodel submodel) {
		if (submodel == null) {
			return;
		}

		if (submodel instanceof HasSemantics)
			visit((HasSemantics) submodel);

		AssetAdministrationShellElementWalkerVisitor.super.visit(submodel);
	}

	@Override
	public void visit(SubmodelElementCollection submodelElementCollection) {
		if (submodelElementCollection == null) {
			return;
		}

		visit((HasSemantics) submodelElementCollection);

		AssetAdministrationShellElementWalkerVisitor.super.visit(submodelElementCollection);
	}

	@Override
	public void visit(Operation operation) {
		if (operation == null) {
			return;
		}

		visit((HasSemantics) operation);

		AssetAdministrationShellElementWalkerVisitor.super.visit(operation);
	}

	private Set<String> getConceptDescriptionIds(List<Key> keys) {
		return keys.stream().map(Key::getValue).collect(Collectors.toSet());
	}

}
