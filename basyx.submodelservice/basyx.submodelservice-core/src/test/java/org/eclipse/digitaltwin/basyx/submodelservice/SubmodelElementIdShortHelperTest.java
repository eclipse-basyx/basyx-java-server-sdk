/*******************************************************************************
 * Copyright (C) 2026 the Eclipse BaSyx Authors
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

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAnnotatedRelationshipElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEntity;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.eclipse.digitaltwin.basyx.submodelservice.pathparsing.SubmodelElementIdShortHelper;
import org.junit.Test;

public class SubmodelElementIdShortHelperTest {
	private static final String PARENT_PATH = "root.parent";
	private static final SubmodelElement CHILD = new DefaultProperty.Builder().idShort("child").build();

	@Test
	public void buildsCollectionChildPath() {
		assertEquals("root.parent.child", SubmodelElementIdShortHelper.buildChildIdShortPath(PARENT_PATH, new DefaultSubmodelElementCollection(), CHILD));
	}

	@Test
	public void buildsEntityStatementPath() {
		assertEquals("root.parent.child", SubmodelElementIdShortHelper.buildChildIdShortPath(PARENT_PATH, new DefaultEntity(), CHILD));
	}

	@Test
	public void buildsAnnotatedRelationshipElementAnnotationPath() {
		assertEquals("root.parent.child", SubmodelElementIdShortHelper.buildChildIdShortPath(PARENT_PATH, new DefaultAnnotatedRelationshipElement(), CHILD));
	}

	@Test
	public void buildsListChildPathFromLastIndex() {
		DefaultSubmodelElementList parent = new DefaultSubmodelElementList.Builder().value(List.of(new DefaultProperty(), CHILD)).build();

		assertEquals("root.parent[1]", SubmodelElementIdShortHelper.buildChildIdShortPath(PARENT_PATH, parent, CHILD));
	}
}
