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
package org.eclipse.digitaltwin.basyx.submodelservice;

import static org.junit.Assert.assertEquals;

import java.util.Stack;

import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.submodelservice.pathParsing.PathToken;
import org.eclipse.digitaltwin.basyx.submodelservice.pathParsing.SubmodelElementIdShortPathParser;
import org.junit.Test;

/**
 * 
 * Test for the SubmodelElementIdShortPathParser
 * 
 * @author fried
 *
 */
public class SubmodelElementIdShortPathParserTest {

	private static final String ID_SHORT_PATH_FIRST_PART = "SubmodelElement";
	private static final int INDEX_ONE = 100;
	private static final int INDEX_TWO = 23;
	private static final String ID_SHORT_PATH_SECOND_PART_ID_SHORT = "SubmodelElementList";
	private static final String ID_SHORT_PATH_SECOND_PART_INDICES = "[" + INDEX_ONE + "][" + INDEX_TWO + "]";
	private static final String ID_SHORT_PATH_SECOND_PART = ID_SHORT_PATH_SECOND_PART_ID_SHORT
			+ ID_SHORT_PATH_SECOND_PART_INDICES;
	private static final String ID_SHORT_PATH_THIRD_PART = "SubmodelElementProperty";
	private static final String ID_SHORT_PATH = ID_SHORT_PATH_FIRST_PART + "." + ID_SHORT_PATH_SECOND_PART + "."
			+ ID_SHORT_PATH_THIRD_PART;
	private static final String ID_SHORT_WITH_SPECIAL_CHARACTERS = "doesNotMatter-,;_'*+~?=)({}&%$§!";
	private static final String INVALID_ID_SHORT_PATH = "test[0].hallo[9.hello";

	@Test
	public void idShortParsedCorrectly() throws ElementDoesNotExistException {
		SubmodelElementIdShortPathParser pathParser = new SubmodelElementIdShortPathParser();
		Stack<PathToken> tokenStack = pathParser.parsePathTokens(ID_SHORT_PATH);
		assertEquals(ID_SHORT_PATH_FIRST_PART, tokenStack.pop().getToken());
		assertEquals(ID_SHORT_PATH_SECOND_PART_ID_SHORT, tokenStack.pop().getToken());
		assertEquals(INDEX_ONE, (int) Integer.valueOf(tokenStack.pop().getToken()));
		assertEquals(INDEX_TWO, (int) Integer.valueOf(tokenStack.pop().getToken()));
		assertEquals(ID_SHORT_PATH_THIRD_PART, tokenStack.pop().getToken());
	}

	@Test(expected = Exception.class)
	public void invalidIdShortPathThrowsError() throws Exception {
		SubmodelElementIdShortPathParser pathParser = new SubmodelElementIdShortPathParser();
		pathParser.parsePathTokens(INVALID_ID_SHORT_PATH);
	}

	@Test
	public void idShortWithSpecialCharactersDoesNotThrowError() {
		SubmodelElementIdShortPathParser pathParser = new SubmodelElementIdShortPathParser();
		Stack<PathToken> tokenStack = pathParser.parsePathTokens(ID_SHORT_WITH_SPECIAL_CHARACTERS);
		assertEquals(ID_SHORT_WITH_SPECIAL_CHARACTERS, tokenStack.pop().getToken());
	}
}