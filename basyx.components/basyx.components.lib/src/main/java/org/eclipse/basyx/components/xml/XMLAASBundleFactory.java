/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.xml;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @deprecated Moved to SDK.
 * @author schnicke
 *
 */
@Deprecated
public class XMLAASBundleFactory extends org.eclipse.basyx.aas.factory.xml.XMLAASBundleFactory {

	public XMLAASBundleFactory(String xmlContent) {
		super(xmlContent);
	}

	public XMLAASBundleFactory(Path xmlFile) throws IOException {
		super(xmlFile);
	}
}
