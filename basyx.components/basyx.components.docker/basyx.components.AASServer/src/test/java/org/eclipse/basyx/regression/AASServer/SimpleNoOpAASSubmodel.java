/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.AASServer;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.testsuite.regression.submodel.restapi.SimpleAASSubmodel;

public class SimpleNoOpAASSubmodel extends SimpleAASSubmodel {

	public SimpleNoOpAASSubmodel() {
		this("SimpleAASSubmodel");
	}

	public SimpleNoOpAASSubmodel(String idShort) {
		super(idShort);

		// Remove operations
		deleteSubmodelElement("complex");
		deleteSubmodelElement("simple");
		deleteSubmodelElement("exception1");
		deleteSubmodelElement("exception2");

		Map<String, ISubmodelElement> elems = this.getSubmodelElements();
		SubmodelElementCollection root = (SubmodelElementCollection) elems.get("containerRoot");
		SubmodelElementCollection opContainer = (SubmodelElementCollection) root.getSubmodelElement("container");
		opContainer.deleteSubmodelElement("operationId");
		Operation opReplacement = new Operation("operationId");
		opContainer.addSubmodelElement(opReplacement);
	}

}
