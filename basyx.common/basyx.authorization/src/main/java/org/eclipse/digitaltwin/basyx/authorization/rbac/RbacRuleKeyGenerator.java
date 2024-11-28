/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.authorization.rbac;

import java.util.Base64;

/**
 * A helper class to generate the key based on hash of combination of role, {@link Action}, and the concrete {@link TargetInformation}
 * class.
 * 
 * @author danish
 */
public class RbacRuleKeyGenerator {
	
	/**
	 * Generates the key based on hash of combination of role, {@link Action}, and the concrete {@link TargetInformation} 
	 * class.
	 * 
	 * <p> For e.g., role = Engineer, Action = READ, TargetInformation Class = org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.AasTargetInformation.java
	 * <br>
	 * So the hash code of concatenation of these is: -1428731317.
	 * 
	 * </p>
	 * 
	 * @param role
	 * @param action
	 * @param clazz
	 * @return
	 */
	public static String generateKey(String role, String action, String clazz) {
		 String combinedString = String.valueOf(role + action + clazz);
		 
		return Base64.getEncoder().encodeToString(combinedString.getBytes());
	}

}
