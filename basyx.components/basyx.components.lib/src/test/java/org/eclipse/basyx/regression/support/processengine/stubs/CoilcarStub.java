/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.support.processengine.stubs;


public class CoilcarStub implements ICoilcar {
	
	private String serviceCalled;
	private Object parameter;

	@Override
	public int moveTo(int position) {
		serviceCalled = "moveTo";
		parameter = position;
		return position;
	}

	@Override
	public int liftTo(int position) {
		serviceCalled = "liftTo";
		parameter = position;
		return position;
	}

	public String getServiceCalled() {
		return serviceCalled;
	}

	public Object getParameter() {
		return parameter;
	}
}
