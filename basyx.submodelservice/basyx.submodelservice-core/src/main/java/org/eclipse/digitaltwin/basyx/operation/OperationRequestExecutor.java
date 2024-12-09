/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationRequest;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationResult;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationResult;

/**
 * Executes an Operation Request
 * 
 * @author mateusmolina
 */
public class OperationRequestExecutor {

    private OperationRequestExecutor() {
    }

    public static OperationResult executeOperationRequestSynchronously(Invokable invokable, OperationRequest request) {
        List<OperationVariable> inVars = new ArrayList<>();
        inVars.addAll(request.getInputArguments());
        inVars.addAll(request.getInoutputArguments());

        List<OperationVariable> result = Arrays.asList(invokable.invoke(inVars.toArray(new OperationVariable[0])));

        List<OperationVariable> outVars = new ArrayList<>(result);
        List<OperationVariable> inoutputVars = new ArrayList<>();

        if (!request.getInoutputArguments().isEmpty()) {
            List<String> inoutputVarsIdShorts = request.getInoutputArguments().stream().map(OperationVariable::getValue).map(SubmodelElement::getIdShort).toList();

            inoutputVars = result.stream().filter(opVar -> inoutputVarsIdShorts.contains(opVar.getValue().getIdShort())).toList();

            outVars.removeAll(inoutputVars);
        }

        return createSuccessOperationResult(outVars, inoutputVars);
    }

    private static OperationResult createSuccessOperationResult(List<OperationVariable> outputVars, List<OperationVariable> inoutputVars) {
        return new DefaultOperationResult.Builder().success(true).outputArguments(outputVars).inoutputArguments(inoutputVars).build();
    }

}
