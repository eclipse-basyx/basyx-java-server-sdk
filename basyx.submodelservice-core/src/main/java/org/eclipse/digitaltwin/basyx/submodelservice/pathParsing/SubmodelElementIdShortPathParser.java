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
package org.eclipse.digitaltwin.basyx.submodelservice.pathParsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;

/**
 * Class for parsing an idShortPath
 * 
 * @author fried
 * 
 */
public class SubmodelElementIdShortPathParser {

	/**
	 * Splits an idShortPath
	 * 
	 * @param idShortPath
	 * @return A stack containing all idShortTokens of the idShortPath
	 * 
	 */
	public Stack<PathToken> parsePathTokens(String idShortPath) {
		try {
			String splitted[] = splitIdShortPathAtDots(idShortPath);
			return generateTokenStackFromSplittedArray(splitted);
		} catch (ElementDoesNotExistException e) {
			throw new ElementDoesNotExistException(idShortPath);
		}
	}

	private static String[] splitIdShortPathAtDots(String idShortPath) {
		return idShortPath.split("\\.");
	}

	private static String getIdShortWithoutIndices(String idShort) {
		return idShort.split("\\[")[0];
	}

	private static Stack<PathToken> generateTokenStackFromSplittedArray(String[] splitted) {
		Stack<PathToken> tokenStack = new Stack<>();
		for (int i = splitted.length - 1; i >= 0; i--) {
			List<Integer> indices = getAllIndices(splitted[i]);
			for (int ix = indices.size() - 1; ix >= 0; ix--) {
				tokenStack.push(new ListIndexPathToken(indices.get(ix).toString()));
			}
			tokenStack.push(new CollectionIdShortPathToken(getIdShortWithoutIndices(splitted[i])));
		}
		return tokenStack;
	}

	private static List<Integer> getAllIndices(String idShortToken) throws ElementDoesNotExistException {
		List<Integer> indices = new ArrayList<>();
		while (hasOpeningBrackets(idShortToken)) {

			int occurance = getIndexOfOpeningBracket(idShortToken);
			int end = getIndexOfClosingBracket(idShortToken);

			throwExceptionIfClosingBracketIsMissing(end);
			throwExceptionIfInvalidCharacterAfterClosingBracket(idShortToken, end);
			throwExceptionIfOpeningBracketIsInsideBrackets(idShortToken, occurance, end);

			int index = extractIndex(idShortToken, occurance, end);

			indices.add(index);
			idShortToken = idShortToken.substring(end + 1);

		}
		throwExceptionIfTooManyClosingBracketsExist(idShortToken);

		return indices;
	}

	private static boolean hasOpeningBrackets(String idShortToken) {
		return getIndexOfOpeningBracket(idShortToken) != -1;
	}

	private static int getIndexOfOpeningBracket(String idShortToken) {
		return idShortToken.indexOf('[');
	}

	private static int getIndexOfClosingBracket(String idShortToken) {
		return idShortToken.indexOf(']');
	}

	private static void throwExceptionIfClosingBracketIsMissing(int end) throws ElementDoesNotExistException {
		if (end == -1) {
			throw new ElementDoesNotExistException();
		}
	}

	private static void throwExceptionIfInvalidCharacterAfterClosingBracket(String idShort, int end)
			throws ElementDoesNotExistException {
		if (idShort.length() - 1 > end) {
			if (idShort.charAt(end + 1) != '[') {
				throw new ElementDoesNotExistException();
			}
		}
	}

	private static void throwExceptionIfOpeningBracketIsInsideBrackets(String idShort, int occurance, int end)
			throws ElementDoesNotExistException {
		if (idShort.substring(occurance + 1, end).indexOf('[') != -1) {
			throw new ElementDoesNotExistException();
		}
	}

	private static int extractIndex(String idShortToken, int occurance, int end) {
		String currentIndice = idShortToken.substring(occurance + 1, end);
		try {
			int index = Integer.valueOf(currentIndice);
			throwExceptionIfIndexIsInvalid(index);
			return index;
		} catch (NumberFormatException doesNotMatter) {
		}
		throw new ElementDoesNotExistException();
	}

	private static void throwExceptionIfIndexIsInvalid(int index) throws ElementDoesNotExistException {
		if (index < 0) {
			throw new ElementDoesNotExistException();
		}
	}

	private static void throwExceptionIfTooManyClosingBracketsExist(String idShortToken) {
		if (getIndexOfClosingBracket(idShortToken) != -1) {
			throw new ElementDoesNotExistException();
		}
	}

}
