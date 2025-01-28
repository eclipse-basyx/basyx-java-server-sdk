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

package org.eclipse.digitaltwin.basyx.aasrepository;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.core.Filter;

/**
 * An implementation of {@link Filter} for the AAS. It defines attributes for
 * filter operations.
 * 
 * @author danish
 */
public class AasFilter implements Filter {

	private List<String> aasIds;
	private String idShort;
	private AssetKind assetKind;
	private String assetType;
	private List<SpecificAssetId> specificAssetIds;

	@Override
	public List<String> getIds() {
		return aasIds;
	}

	public String getIdShort() {
		return idShort;
	}

	public void setIds(List<String> aasIds) {
		this.aasIds = aasIds;
	}

	public AssetKind getAssetKind() {
		return assetKind;
	}

	public void setAssetKind(AssetKind assetKind) {
		this.assetKind = assetKind;
	}

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public List<SpecificAssetId> getSpecificAssetIds() {
		return specificAssetIds;
	}

	public void setSpecificAssetIds(List<SpecificAssetId> specificAssetIds) {
		this.specificAssetIds = specificAssetIds;
	}

	public void setIdShort(String idShort) {
		this.idShort = idShort;
	}

}
