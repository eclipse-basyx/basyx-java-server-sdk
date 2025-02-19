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
package org.eclipse.digitaltwin.basyx.submodelrepository.feature.operationdispatching.java;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.ExecutionState;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationResult;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationResult;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */
public class TestOperationValues {

	private TestOperationValues() {
	}

	public static OperationRequest requestForInt(int... values) {
		return new DefaultOperationRequest.Builder().inputArguments(intInputArguments(values)).build();
	}
	
	public static OperationRequest requestForString(String... values) {
		return new DefaultOperationRequest.Builder().inputArguments(stringInputArguments(values)).build();
	}

	public static OperationResult resultForInt(int... values) {
		return new DefaultOperationResult.Builder().success(true).outputArguments(intInputArguments(values)).build();
	}
	
	public static OperationResult resultForString(String... values) {
		return new DefaultOperationResult.Builder().success(true).outputArguments(stringInputArguments(values)).success(true).build();
	}
	
	private static List<OperationVariable> intInputArguments(int... values) {
		return Arrays.stream(values).mapToObj(TestOperationValues::toIntProperty).map(TestOperationValues::toOperationVariable).collect(Collectors.toList());
	}
	
	private static List<OperationVariable> stringInputArguments(String... values) {
		return Arrays.stream(values).map(TestOperationValues::toStringProperty).map(TestOperationValues::toOperationVariable).collect(Collectors.toList());
	}
	
	public static Property toStringProperty(String value) {
		return new DefaultProperty.Builder().value(String.valueOf(value)).valueType(DataTypeDefXsd.STRING).build();
	}
	
	public static Property toIntProperty(int value) {
		return new DefaultProperty.Builder().value(String.valueOf(value)).valueType(DataTypeDefXsd.INT).build();
	}
    
	public static OperationVariable toOperationVariable(Property prop) {
		return new DefaultOperationVariable.Builder().value(prop).build();
	}
	
	
}