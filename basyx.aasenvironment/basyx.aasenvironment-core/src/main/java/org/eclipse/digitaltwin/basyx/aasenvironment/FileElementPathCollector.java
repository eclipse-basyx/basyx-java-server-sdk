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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.internal.visitor.AssetAdministrationShellElementWalkerVisitor;
import org.eclipse.digitaltwin.aas4j.v3.model.Capability;
import org.eclipse.digitaltwin.aas4j.v3.model.DataElement;
import org.eclipse.digitaltwin.aas4j.v3.model.Entity;
import org.eclipse.digitaltwin.aas4j.v3.model.EventElement;
import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.RelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;

/**
 * Finds and collects all the element paths as a list of {@link SubmodelElement} leading to the
 * {@link File} element of a {@link Submodel}
 * 
 * @author danish
 *
 */
public class FileElementPathCollector implements AssetAdministrationShellElementWalkerVisitor {

	private Submodel submodel;

	private Stack<SubmodelElement> submodelElementStack = new Stack<>();

	private List<List<SubmodelElement>> fileElementPathCandidates = new ArrayList<>();

	public FileElementPathCollector(Submodel submodel) {
		this.submodel = submodel;
	}

	/**
	 * Finds and collects all the element paths as a list of {@link SubmodelElement} leading to the
	 * {@link File} element of a {@link Submodel}
	 * 
	 * @return list containing all the file elements paths
	 */
	public List<List<SubmodelElement>> collect() {
		visit(submodel);

		return fileElementPathCandidates;
	}

	@Override
	public void visit(Submodel submodel) {
		if (submodel == null)
			return;

		submodel.getSubmodelElements().forEach(x -> visit(x));
	}

	@Override
	public void visit(SubmodelElement submodelElement) {
		if (submodelElement == null)
			return;

		Class<?> type = submodelElement.getClass();
		if (RelationshipElement.class.isAssignableFrom(type)) {
			visit((RelationshipElement) submodelElement);
		} else if (DataElement.class.isAssignableFrom(type)) {
			visit((DataElement) submodelElement);
		} else if (Capability.class.isAssignableFrom(type)) {
			visit((Capability) submodelElement);
		} else if (SubmodelElementCollection.class.isAssignableFrom(type)) {
			visit((SubmodelElementCollection) submodelElement);
		} else if (SubmodelElementList.class.isAssignableFrom(type)) {
			visit((SubmodelElementList) submodelElement);
		} else if (Operation.class.isAssignableFrom(type)) {
			visit((Operation) submodelElement);
		} else if (EventElement.class.isAssignableFrom(type)) {
			visit((EventElement) submodelElement);
		} else if (Entity.class.isAssignableFrom(type)) {
			visit((Entity) submodelElement);
		}
	}

	@Override
	public void visit(File file) {
		if (file == null)
			return;

		submodelElementStack.push(file);

		fileElementPathCandidates.add(new ArrayList<>(submodelElementStack));

		submodelElementStack.pop();
	}

	@Override
	public void visit(SubmodelElementCollection submodelElementCollection) {
		if (submodelElementCollection == null)
			return;

		submodelElementStack.push(submodelElementCollection);

		submodelElementCollection.getValue().forEach(x -> visit(x));

		submodelElementStack.pop();
	}

	public void visit(SubmodelElementList submodelElementList) {
		if (submodelElementList == null)
			return;

		submodelElementStack.push(submodelElementList);

		submodelElementList.getValue().forEach(x -> visit(x));

		submodelElementStack.pop();
	}

}
