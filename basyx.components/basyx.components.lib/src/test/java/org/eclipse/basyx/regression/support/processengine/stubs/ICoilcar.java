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

public interface ICoilcar {
	/**
	 * a service that moves the coil-car to the expected position
	 * @param position expected position
	 * */
	public int moveTo(int position);
	
	/**
	 * a service that rises the lifter of the coil-car to the expected position
	 * @param position expected position
	 * */
	public int liftTo(int position);
}
