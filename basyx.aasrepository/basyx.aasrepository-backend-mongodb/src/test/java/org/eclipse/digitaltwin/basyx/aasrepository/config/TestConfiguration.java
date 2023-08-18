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

package org.eclipse.digitaltwin.basyx.aasrepository.config;

import org.eclipse.digitaltwin.basyx.aasrepository.MongoDBAasRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Test configurations for {@link MongoDBAasRepository}
 * 
 * @author danish
 *
 */
@Configuration
public class TestConfiguration {

	@Value("${spring.data.mongodb.host}")
	private String host;

	@Value("${spring.data.mongodb.port}")
	private int port;

	@Value("${spring.data.mongodb.username}")
	private String userName;

	@Value("${spring.data.mongodb.password}")
	private String password;

	@Value("${spring.data.mongodb.database}")
	private String database;

	@Bean
	public MongoDatabaseFactory databaseFactory() {
		String connectionString = createConnectionString();

		MongoClient client = MongoClients.create(connectionString.toString());

		return new SimpleMongoClientDatabaseFactory(client, database);
	}

	private String createConnectionString() {
		return String.format("mongodb://%s:%s@%s:%s", userName, password, host, port);
	}

}
