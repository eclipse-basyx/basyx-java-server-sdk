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

package org.eclipse.digitaltwin.basyx.aasenvironment.client;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;

/**
 * TestFixture
 *
 * @author mateusmolina
 *
 */
public class TestFixture {
	public static final String AAS_PRE1_ID = "aasPre1";
	public static final String SM_PRE1_ID = "smPre1";

	public static final String AAS_POS1_ID = "aasPos1";
	public static final String SM_POS1_ID = "smPos1";

	public static AssetAdministrationShell buildAasPre1() {
		return new DefaultAssetAdministrationShell.Builder().id(AAS_PRE1_ID).build();
	}

	public static Reference buildSmPre1Ref() {
		return new DefaultReference.Builder().build();
	}

	public static Submodel buildSmPre1() {
		return new DefaultSubmodel.Builder().id(SM_PRE1_ID).build();
	}

	public static AssetAdministrationShell buildAasPos1() {
		return new DefaultAssetAdministrationShell.Builder().id(AAS_POS1_ID).build();
	}

	public static AssetAdministrationShellDescriptor buildAasPos1Descriptor() {
		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor();
		descriptor.id(AAS_POS1_ID);
		return descriptor;
	}

	public static SubmodelDescriptor buildSmPos1Descriptor() {
		SubmodelDescriptor descriptor = new SubmodelDescriptor();
		descriptor.id(SM_POS1_ID);
		return descriptor;
	}

	public static Submodel buildSmPos1() {
		return new DefaultSubmodel.Builder().id(SM_POS1_ID).build();
	}
}
