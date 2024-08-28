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
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.mapper.DummyAasDescriptorFactory;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncoder;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.mapper.DummySubmodelDescriptorFactory;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;

/**
 * Test fixture for {@link ConnectedAasManager} and related Components
 *
 * @author mateusmolina
 *
 */
public class TestFixture {
	public static final String AAS_PRE1_ID = "aasPre1";
	public static final String AAS_PRE1_ID_ENCODED = Base64UrlEncoder.encode(AAS_PRE1_ID);
	public static final String AAS_PRE1_IDSHORT = "aasPre1IdShort";
	public static final String AAS_PRE1_GLOBALASSETID = "globalAssetIdPre1";

	public static final String SM_PRE1_ID = "smPre1";
	public static final String SM_PRE1_IDSHORT = "smPre1IdShort";
	public static final String SM_PRE1_ID_ENCODED = Base64UrlEncoder.encode(SM_PRE1_ID);


	public static final String AAS_POS1_ID = "aasPos1";
	public static final String AAS_POS1_ID_ENCODED = Base64UrlEncoder.encode(AAS_POS1_ID);
	public static final String AAS_POS1_IDSHORT = "aasPos1IdShort";
	public static final String AAS_POS1_GLOBALASSETID = "globalAssetIdPos1";

	public static final String SM_POS1_ID = "smPos1";
	public static final String SM_POS1_ID_ENCODED = Base64UrlEncoder.encode(SM_POS1_ID);
	public static final String SM_POS1_IDSHORT = "smPos1IdShort";
	public static final String SM_POS1_SEMANTICID = "smPos1SemanticId";

	private final String aasRepositoryBasePath;
	private final String smRepositoryBasePath;

	public TestFixture(String aasRepositoryBasePath, String smRepositoryBasePath) {
		this.aasRepositoryBasePath = aasRepositoryBasePath;
		this.smRepositoryBasePath = smRepositoryBasePath;
	}

	public AssetAdministrationShell buildAasPre1() {
		return new DefaultAssetAdministrationShell.Builder().id(AAS_PRE1_ID).idShort(AAS_PRE1_IDSHORT).submodels(buildSmPre1Ref()).assetInformation(buildAasPre1AssetInformation()).build();
	}

	public AssetInformation buildAasPre1AssetInformation() {
		return new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE).globalAssetId(AAS_PRE1_GLOBALASSETID).build();
	}

	public AssetAdministrationShellDescriptor buildAasPre1Descriptor() {
		return DummyAasDescriptorFactory.createDummyDescriptor(AAS_PRE1_ID, AAS_PRE1_IDSHORT, AAS_PRE1_GLOBALASSETID, aasRepositoryBasePath);
	}

	public Reference buildSmPre1Ref() {
		return new DefaultReference.Builder().type(ReferenceTypes.MODEL_REFERENCE).keys(new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(SM_PRE1_ID).build()).build();
	}

	public Submodel buildSmPre1() {
		return new DefaultSubmodel.Builder().id(SM_PRE1_ID).idShort(SM_PRE1_IDSHORT).build();
	}

	public SubmodelDescriptor buildSmPre1Descriptor() {
		return DummySubmodelDescriptorFactory.createDummyDescriptor(SM_PRE1_ID, SM_PRE1_IDSHORT, smRepositoryBasePath, null);
	}

	public AssetAdministrationShell buildAasPos1() {
		return new DefaultAssetAdministrationShell.Builder().id(AAS_POS1_ID).idShort(AAS_POS1_IDSHORT).assetInformation(buildAasPos1AssetInformation()).build();
	}

	public AssetInformation buildAasPos1AssetInformation() {
		return new DefaultAssetInformation.Builder().assetKind(AssetKind.INSTANCE).globalAssetId(AAS_POS1_GLOBALASSETID).build();
	}

	public AssetAdministrationShellDescriptor buildAasPos1Descriptor() {
		return DummyAasDescriptorFactory.createDummyDescriptor(AAS_POS1_ID, AAS_POS1_IDSHORT, AAS_POS1_GLOBALASSETID, aasRepositoryBasePath);
	}

	public SubmodelDescriptor buildSmPos1Descriptor() {
		return DummySubmodelDescriptorFactory.createDummyDescriptor(SM_POS1_ID, SM_POS1_IDSHORT, smRepositoryBasePath, new AttributeMapper(ConnectedAasManagerHelper.buildObjectMapper()).mapSemanticId(buildSmPos1SemanticId()));
	}

	public Reference buildSmPos1SemanticId() {
		return new DefaultReference.Builder().type(ReferenceTypes.EXTERNAL_REFERENCE).keys(new DefaultKey.Builder().type(KeyTypes.GLOBAL_REFERENCE).value("https://admin-shell.io/aas/3/0/CustomDataSpecification").build()).build();
	}

	public Submodel buildSmPos1() {
		return new DefaultSubmodel.Builder().id(SM_POS1_ID).idShort(SM_POS1_IDSHORT).semanticId(buildSmPos1SemanticId()).build();
	}

	public Reference buildSmPos1Ref() {
		return new DefaultReference.Builder().type(ReferenceTypes.MODEL_REFERENCE).referredSemanticId(buildSmPos1SemanticId()).keys(new DefaultKey.Builder().type(KeyTypes.SUBMODEL).value(SM_POS1_ID).build()).build();
	}

}
