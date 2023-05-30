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

class PojoClassVisitorDelegate implements PojoClassVisitor {

	private PojoClassVisitor[] visitors;

	public PojoClassVisitorDelegate(PojoClassVisitor... visitors) {
		this.visitors = visitors;
	}

	@Override
	public void startRelation(PojoRelation relation) {
		for (PojoClassVisitor eachVisitor : visitors) {
			eachVisitor.startRelation(relation);
		}
	}

	@Override
	public void endRelation(PojoRelation relation) {
		for (PojoClassVisitor eachVisitor : visitors) {
			eachVisitor.endRelation(relation);
		}
	}

	@Override
	public void onSubTypeRelation(String parent, List<String> subTypes) {
		for (PojoClassVisitor eachVisitor : visitors) {
			eachVisitor.onSubTypeRelation(parent, subTypes);
		}
	}

	@Override
	public boolean startType(String name, boolean isRoot) {
		boolean doContinue = true;
		for (PojoClassVisitor eachVisitor : visitors) {
			if (!eachVisitor.startType(name, isRoot)) {
				doContinue = false;
			}
		}
		return doContinue;
	}

	@Override
	public void endType() {
		for (PojoClassVisitor eachVisitor : visitors) {
			eachVisitor.endType();
		}
	}

	@Override
	public void stop() {
		for (PojoClassVisitor eachVisitor : visitors) {
			eachVisitor.stop();
		}
	}

}
