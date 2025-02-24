/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasxfileserver.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServer;
import org.springframework.validation.annotation.Validated;

/**
 * Specifies the PackagesBody for {@link AASXFileServer}
 */
@Validated
@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-06-22T10:58:56.694021713Z[GMT]")
public class PackagesBody {

	private List<String> aasIds = null;
	private String fileName = null;
	private String filePath = null;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	private String packageId = null;

	public PackagesBody aasIds(List<String> aasIds) {
		this.aasIds = aasIds;
		return this;
	}

	public PackagesBody addAasIdsItem(String aasIdsItem) {
		if (this.aasIds == null) {
			this.aasIds = new ArrayList<String>();
		}
		this.aasIds.add(aasIdsItem);
		return this;
	}

	public List<String> getAasIds() {
		return aasIds;
	}

	public void setAasIds(List<String> aasIds) {
		this.aasIds = aasIds;
	}

	public PackagesBody fileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public PackagesBody packageId(String packageId) {
		this.packageId = packageId;
		return this;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PackagesBody packagesBody = (PackagesBody) o;
		return Objects.equals(this.aasIds, packagesBody.aasIds) && Objects.equals(this.fileName, packagesBody.fileName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(aasIds, fileName);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class PackagesBody {\n");

		sb.append("    aasIds: ").append(toIndentedString(aasIds)).append("\n");
		sb.append("    fileName: ").append(toIndentedString(fileName)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
