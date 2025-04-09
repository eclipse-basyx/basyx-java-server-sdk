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
package org.eclipse.digitaltwin.basyx.submodelservice.pathparsing;

import java.util.Stack;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;

/**
 * Class for getting a Hierarchical SubmodelElement in a Submodel via a idShort
 * Path
 * 
 * @author fried
 *
 */
public class HierarchicalSubmodelElementParser {
	private Submodel submodel;
	private SubmodelElementIdShortPathParser pathParser;

	/**
	 * Creates a HierarchicalSubmodelElementParser
	 * 
	 * @param submodel the submodel
	 * 
	 */
	public HierarchicalSubmodelElementParser(Submodel submodel) {
		this.submodel = submodel;
		this.pathParser = new SubmodelElementIdShortPathParser();
	}

	/**
	 * Returns the nested SubmodelElement given in the idShortPath
	 * 
	 * @param idShortPath
	 *            the idShortPath of the SubmodelElement
	 * 
	 * @return the nested SubmodelElement
	 * @throws ElementDoesNotExistException
	 * 
	 */
	public SubmodelElement getSubmodelElementFromIdShortPath(String idShortPath) throws ElementDoesNotExistException {
		Stack<PathToken> idShortPathTokenStack = pathParser.parsePathTokens(idShortPath);

		return getLastElementOfStack(idShortPathTokenStack);
	}
	
	/**
	 * Returns the IdShortPath of parent SubmodelElement
	 * 
	 * <pre>
	 * e.g., 
	 * 
	 * SubmodelElementCollection.Property -> SubmodelElementCollection
	 * SubmodelElementList.SubmodelElementCollection.File -> SubmodelElementList.SubmodelElementCollection
	 * Property -> Property
	 * SubmodelElementList.SubmodelElementCollection -> SubmodelElementList
	 * 
	 * </pre>
	 * @param idShortPath
	 * @return the IdShortPath of parent SubmodelElement
	 */
	public String getIdShortPathOfParentElement(String idShortPath) {

		boolean isLastElementABracket = idShortPath.endsWith("]");
		if (isLastElementABracket){
			int lastElementBracketIndex = idShortPath.lastIndexOf("[");
			return idShortPath.substring(0, lastElementBracketIndex);
		}

		int lastElementIdShortIndex = idShortPath.lastIndexOf(".");

		if (lastElementIdShortIndex == -1)
			return idShortPath;

		return idShortPath.substring(0, lastElementIdShortIndex);
	}

	private SubmodelElement getLastElementOfStack(Stack<PathToken> idShortPathTokenStack) {
		PathToken nextToken = idShortPathTokenStack.pop();
		SubmodelElement nextElement = getFirstSubmodelElementFromStack(nextToken.getToken());
		while (!idShortPathTokenStack.isEmpty()) {
			nextToken = idShortPathTokenStack.pop();
			nextElement = nextToken.getSubmodelElement(nextElement);
		}

		return nextElement;
	}

	private SubmodelElement getFirstSubmodelElementFromStack(String rootElementIdShort) {
		return submodel.getSubmodelElements().stream().filter(sme -> sme.getIdShort().equals(rootElementIdShort))
				.findAny().orElseThrow(() -> new ElementDoesNotExistException());
	}

}
