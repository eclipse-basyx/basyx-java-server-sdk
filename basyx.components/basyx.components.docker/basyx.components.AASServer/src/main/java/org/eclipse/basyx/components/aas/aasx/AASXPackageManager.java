/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.aas.aasx;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.factory.aasx.AASXToMetamodelConverter;
import org.xml.sax.SAXException;

/**
 * @deprecated Renamed and moved to SDK. Please use AASXToMetamodelConverter
 * @author schnicke
 *
 */
@Deprecated
public class AASXPackageManager extends AASXToMetamodelConverter {

	public AASXPackageManager(String path) {
		super(path);
	}

	@Override
	protected Path getRootFolder() throws IOException, URISyntaxException {
		URI uri = AASXPackageManager.class.getProtectionDomain().getCodeSource().getLocation().toURI();
		URI parent = new File(uri).getParentFile().toURI();
		return Paths.get(parent);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<org.eclipse.basyx.support.bundle.AASBundle> retrieveAASBundles()
			throws IOException, ParserConfigurationException, SAXException, InvalidFormatException {
		Set<? extends AASBundle> bundles = super.retrieveAASBundles();
		return repackAASBundle(bundles);
	}

	/**
	 * @param bundles
	 * @return
	 */
	private Set<org.eclipse.basyx.support.bundle.AASBundle> repackAASBundle(Set<? extends AASBundle> bundles) {
		return bundles.stream().map(b -> new org.eclipse.basyx.support.bundle.AASBundle(b.getAAS(), b.getSubmodels()))
				.collect(Collectors.toSet());
	}
}
