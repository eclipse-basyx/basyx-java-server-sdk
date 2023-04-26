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

package org.eclipse.digitaltwin.basyx.submodelrepository;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import static org.eclipse.digitaltwin.basyx.core.utils.BaSyxUtil.isSame;

/**
* Utility class for {@link SubmodelRepository} containing helper
* functions
* 
* @author danish
*
*/
public class SubmodelRepositoryUtil {

	private SubmodelRepositoryUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Checks passed {@link Submodel} if they both have same identifier and same idShort
	 * 
	 * @apiNote If any of the passed parameters has null idShort, then it only checks the identifier

	 * @param oldSubmodel
	 * @param newSubmodel
	 * @return
	 */
	public static boolean haveSameIdentifications(Submodel oldSubmodel, Submodel newSubmodel) {
		if (isIdShortNull(oldSubmodel.getIdShort()) || isIdShortNull(newSubmodel.getIdShort()))
			return isSame(oldSubmodel.getId(), newSubmodel.getId());
		
		return isSame(oldSubmodel.getId(), newSubmodel.getId()) && isSame(oldSubmodel.getIdShort(), newSubmodel.getIdShort());
	}
	
	private static boolean isIdShortNull(String idShort) {
		return idShort == null;
	}

}
