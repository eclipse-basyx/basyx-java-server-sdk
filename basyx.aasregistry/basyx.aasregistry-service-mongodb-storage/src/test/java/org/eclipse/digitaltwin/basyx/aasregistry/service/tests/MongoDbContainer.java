/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.aasregistry.service.tests;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class MongoDbContainer extends GenericContainer<MongoDbContainer> {

	private static final DockerImageName DEFAULT_IMAGE = DockerImageName.parse("mongo:6.0.5");

	private final String connectionStringTemplate;


	public MongoDbContainer(String userName, String password) {
		this(DEFAULT_IMAGE, userName, password);
	}
	
	public MongoDbContainer(DockerImageName image, String userName, String password) {
		super(image);
		this.connectionStringTemplate = "mongodb://" + userName + ":" + password + "@%s:%d/%s";
		withExposedPorts(27017).withEnv("MONGO_INITDB_ROOT_USERNAME", userName).withEnv("MONGO_INITDB_ROOT_PASSWORD", password);
	}

	public String getMongoDbUri(String database) {
		return String.format(connectionStringTemplate, getHost(), getFirstMappedPort(), database);
	}

}
