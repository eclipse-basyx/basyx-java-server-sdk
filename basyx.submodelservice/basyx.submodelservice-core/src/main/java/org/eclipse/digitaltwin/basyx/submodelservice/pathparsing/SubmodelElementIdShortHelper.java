/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

public class SubmodelElementIdShortHelper {
	
	/**
	 * Check whether the given idShortPath is a simple idShort or a path of idShorts
	 * 
	 * @param idShortPath
	 * @return true if the given idShortPath is a path of IdShorts
	 */
	public static boolean isNestedIdShortPath(String idShortPath) {
		return hasDot(idShortPath)|| hasOpeningBrackets(idShortPath);
	}

	/**
	 * Extract the idShort of the direct parent (submodel element collection) of the
	 * nested submodel element
	 * 
	 * @param idShortPath
	 *            - A path to the nested target submodel element
	 * @return idShort of the direct parent of the nested submodel element
	 */
	public static String extractDirectParentSubmodelElementCollectionIdShort(String idShortPath) {
		return idShortPath.substring(0, idShortPath.lastIndexOf("."));
		
	}
	
	/**
	 * Extract the idShort of the direct parent (submodel element list) of the
	 * nested submodel element
	 * 
	 * @param idShortPath
	 *            - A path to the nested target submodel element
	 * @return idShort of the direct parent of the nested submodel element
	 */
	public static String extractDirectParentSubmodelElementListIdShort(String idShortPath) {
		if(hasOpeningBrackets(idShortPath) && hasDot(idShortPath)) {
			int indexDot = idShortPath.lastIndexOf(".");
			int indexBracket = idShortPath.lastIndexOf("[");
			if(indexDot > indexBracket)  return idShortPath.substring(0, indexDot);
			
			return idShortPath.substring(0, indexBracket);
		}
		
		return idShortPath.substring(0, idShortPath.lastIndexOf("["));
	}
	
	public static boolean isDirectParentASubmodelElementList(String idShortPath) {
		if(hasOpeningBrackets(idShortPath) && hasDot(idShortPath)) {
			int indexDot = idShortPath.lastIndexOf(".");
			int indexBracket = idShortPath.lastIndexOf("[");
			return indexDot < indexBracket;
		}
		if(hasOpeningBrackets(idShortPath)) return true;
		return false;
	}

	private static boolean hasDot(String idShortPath) {
		return idShortPath.contains(".");
	}
	
	private static boolean hasOpeningBrackets(String idShortPath) {
		return idShortPath.contains("[");
	}

}
