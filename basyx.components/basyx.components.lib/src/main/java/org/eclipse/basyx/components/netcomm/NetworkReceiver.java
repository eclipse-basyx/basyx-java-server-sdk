/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.netcomm;




/**
 * Interface for TCP receiver components
 * 
 * @author kuhn
 *
 */
public interface NetworkReceiver {

	
	/**
	 * Received a string from network connection 
	 */
	public void onReceive(byte[] rxData);
}
