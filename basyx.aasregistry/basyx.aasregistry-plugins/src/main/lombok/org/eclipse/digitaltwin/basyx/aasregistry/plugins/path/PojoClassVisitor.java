/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.aasregistry.plugins.path;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

interface PojoClassVisitor {

	void startRelation(PojoRelation relation);

	void endRelation(PojoRelation relation);

	void onSubTypeRelation(String parent, List<String> subTypes);

	boolean startType(String name, boolean isRoot);

	void endType();

	void stop();

	@Builder
	@Getter
	@ToString
	@EqualsAndHashCode
	@RequiredArgsConstructor
	static class PojoRelation {

		private final String subject;
		private final String methodName;
		private final String fieldName;
		private final String range;
		private final PojoRelationType type;
		private final boolean isRootRelation;
		private final boolean isPrimitive;

		enum PojoRelationType {
			FUNCTIONAL, LIST, MAP
		}

		public boolean isComplex() {
			return !isPrimitive;
		}
	}

}