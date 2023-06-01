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
package org.eclipse.digitaltwin.basyx.aasenvironment.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.Serializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.AASXSerializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.AasEnvironmentSerialization;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;

public class DefaultAASEnvironmentSerialization implements AasEnvironmentSerialization {
	private AasRepository aasRepository;
	private SubmodelRepository submodelRepository;
	private Serializer jsonSerializer = new JsonSerializer();
	private Serializer xmlSerializer = new XmlSerializer();
	private AASXSerializer aasxSerializer = new AASXSerializer();

	public DefaultAASEnvironmentSerialization(AasRepository aasRepository, SubmodelRepository submodelRepository) {
		this.aasRepository = aasRepository;
		this.submodelRepository = submodelRepository;
	}

	@Override
	public String createJSONAASEnvironmentSerialization(@Valid List<String> aasIds, @Valid List<String> submodelIds) throws SerializationException {
		Environment aasEnvironment = extractShellsAndSubmodels(aasIds, submodelIds);
		return jsonSerializer.write(aasEnvironment);
	}

	@Override
	public String createXMLAASEnvironmentSerialization(@Valid List<String> aasIds, @Valid List<String> submodelIds) throws SerializationException {
		Environment aasEnvironment = extractShellsAndSubmodels(aasIds, submodelIds);

		return xmlSerializer.write(aasEnvironment);
	}

	@Override
	public byte[] createAASXAASEnvironmentSerialization(@Valid List<String> aasIds, @Valid List<String> submodelIds) throws SerializationException, IOException {
		Environment aasEnvironment = extractShellsAndSubmodels(aasIds, submodelIds);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		aasxSerializer.write(aasEnvironment, null, outputStream);
		return outputStream.toByteArray();
	}

	private Environment extractShellsAndSubmodels(List<String> aasIds, List<String> submodelIds) {
		List<AssetAdministrationShell> shells = aasIds.stream().map(aasRepository::getAas).collect(Collectors.toList());
		List<Submodel> submodels = submodelIds.stream().map(submodelRepository::getSubmodel).collect(Collectors.toList());
		Environment aasEnvironment = new DefaultEnvironment();
		aasEnvironment.setAssetAdministrationShells(shells);
		aasEnvironment.setSubmodels(submodels);
		return aasEnvironment;
	}

}
