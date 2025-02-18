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


package org.eclipse.digitaltwin.basyx.submodelservice.example;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.ModellingKind;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.operation.InvokableOperation;

import com.google.common.collect.Lists;

/**
 * Factory for creating an example submodel to be hosted as standalone submodel
 * 
 * @author schnicke
 */
public class ExampleSubmodelFactory {

	public Submodel create() {
		List<LangStringTextType> description = new ArrayList<LangStringTextType>();
		description.add(new DefaultLangStringTextType.Builder().language("de-DE")
				.text("Test")
				.build());
		List<LangStringNameType> displayName = new ArrayList<LangStringNameType>();
		displayName.add(new DefaultLangStringNameType.Builder().language("de-DE")
				.text("Test")
				.build());
		List<Key> refKeys = new ArrayList<Key>();
		refKeys.add(new DefaultKey.Builder().value("123")
				.build());

		SubmodelElement sme1 = new DefaultProperty.Builder()
				.value("123")
				.idShort("test")
				.build();
		Operation square = createInvokableOperation();
		List<SubmodelElement> smeList = Lists.newArrayList(sme1, square);

		Submodel submodel = new DefaultSubmodel.Builder().category("TestCategory")
				.description(description)
				.displayName(displayName)
				.id("Example")
				.idShort("example")
				.kind(ModellingKind.INSTANCE)
				.semanticId(new DefaultReference.Builder().keys(refKeys)
						.build())
				.submodelElements(smeList)
				.build();

		return submodel;
	}

	private static Operation createInvokableOperation() {
		return new InvokableOperation.Builder()
				.idShort("squareOperation")
				.inputVariables(createIntOperationVariable("input"))
				.outputVariables(createIntOperationVariable("result"))
				.invokable(ExampleSubmodelFactory::square)
				.build();
	}

	private static OperationVariable createOperationVariable(Property val) {
		return new DefaultOperationVariable.Builder().value(val).build();
	}

	private static DefaultOperationVariable createIntOperationVariable(String idShort) {
		return new DefaultOperationVariable.Builder().value(new DefaultProperty.Builder().idShort(idShort).valueType(DataTypeDefXsd.INT).build()).build();
	}
	
	private static OperationVariable[] square(OperationVariable[] inputs) {
		Property in = (Property) inputs[0].getValue();
		Integer val = Integer.valueOf(in.getValue());
		Integer squared = val * val;
		in.setValue(squared.toString());
		in.setIdShort("result");
		return new OperationVariable[] { createOperationVariable(in) };
	}
}
