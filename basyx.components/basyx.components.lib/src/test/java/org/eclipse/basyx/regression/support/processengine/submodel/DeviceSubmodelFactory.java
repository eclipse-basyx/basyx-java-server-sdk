/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.support.processengine.submodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.eclipse.basyx.regression.support.processengine.stubs.ICoilcar;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;


public class DeviceSubmodelFactory {
	public Submodel create(String id, ICoilcar coilcar) {
		// create a single value property
		Property property1 = new Property(0);
		property1.setIdShort("currentPosition");
		
		Property property2 = new Property(0);
		property2.setIdShort("lifterPosition");
		
		Property property3 = new Property(false);
		property3.setIdShort("physicalSpeed");
		
		// create 2 opertations
		Operation op1 = new Operation((Function<Object[], Object>) obj -> {
			return coilcar.liftTo((int)obj[0]);
		});
		op1.setInputVariables(Collections.singletonList(new OperationVariable(new Property("position", 0))));
		op1.setOutputVariables(Collections.singletonList(new OperationVariable(new Property("result", 0))));
		op1.setIdShort("liftTo");
		
		Operation op2 = new Operation((Function<Object[], Object>) obj -> {
			coilcar.moveTo((int)obj[0]);
			return true;
		});
		op2.setInputVariables(Collections.singletonList(new OperationVariable(new Property("position", 0))));
		op2.setOutputVariables(Collections.singletonList(new OperationVariable(new Property("result", 0))));
		op2.setIdShort("moveTo");
		
		// create a list for defined operations
		List<Operation> opList = new ArrayList<>();
		opList.add(op1);
		opList.add(op2);
		// create a list for defined properties
		List<Property> propList = new ArrayList<>();
		propList.add(property1);
		propList.add(property2);
		propList.add(property3);
		// create the sub-model and add the property and operations to the sub-model
		Submodel sm = new Submodel(id, new Identifier(IdentifierType.CUSTOM, id + "Custom"));
		propList.forEach(sm::addSubmodelElement);
		opList.forEach(sm::addSubmodelElement);
		return sm;
	}
}
