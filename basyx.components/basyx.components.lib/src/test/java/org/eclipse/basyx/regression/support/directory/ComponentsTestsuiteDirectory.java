/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.support.directory;

import org.eclipse.basyx.vab.registry.memory.VABInMemoryRegistry;




/**
 * Implement the test suite directory service with pre-configured directory entries
 * 
 * @author kuhn
 *
 */
public class ComponentsTestsuiteDirectory extends VABInMemoryRegistry {

	
	/**
	 * Constructor - load all directory entries
	 */
	public ComponentsTestsuiteDirectory() {
		// Populate with entries from base implementation
		super();

		// Define mappings
		// - SQL provider mappings
		addMapping("SQLTestSubmodel",          "http://localhost:8080/basys.components/Testsuite/components/BaSys/1.0/provider/sqlsm/");
		addMapping("sampleDB.SQLTestAAS",      "http://localhost:8080/basys.components/Testsuite/components/BaSys/1.0/provider/sqlsm/");
		// - CFG provider mappings
		addMapping("CfgFileTestAAS",           "http://localhost:8080/basys.components/Testsuite/components/BaSys/1.0/provider/cfgsm/");
		addMapping("sampleCFG.CfgFileTestAAS", "http://localhost:8080/basys.components/Testsuite/components/BaSys/1.0/provider/cfgsm/");
		// - Raw CFG provider mappings
		addMapping("AASProvider",              "http://localhost:8080/basys.components/Testsuite/components/BaSys/1.0/provider/rawcfgsm/");
		addMapping("RawCfgFileTestAAS",              "http://localhost:8080/basys.components/Testsuite/components/BaSys/1.0/provider/rawcfgsm/");
		addMapping("sampleRawCFG.RawCfgFileTestAAS", "http://localhost:8080/basys.components/Testsuite/components/BaSys/1.0/provider/rawcfgsm/");
		// - XQuery provider mappings
		addMapping("XMLXQueryFileTestAAS",     "http://localhost:8080/basys.components/Testsuite/components/BaSys/1.0/provider/xmlxquery/");
		// - Processengine mappings
		addMapping("coilcar",                  "http://localhost:8080/basys.components/Testsuite/Processengine/coilcar/");
		addMapping("submodel1",                "http://localhost:8080/basys.components/Testsuite/Processengine/coilcar/");
	}	
}
