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
package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.h2;
import jakarta.transaction.Transactional;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryServiceSuite;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests the AasDiscoveryService with MongoDb as backend
 * 
 * @author danish, mateusmolina
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestH2AasDiscoveryService extends AasDiscoveryServiceSuite {

	@Autowired
	AasDiscoveryService aasDiscoveryService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Before
	@Transactional
	public void cleanup() {
		jdbcTemplate.execute("DELETE FROM keys");
		jdbcTemplate.execute("DELETE FROM references");
		jdbcTemplate.execute("DELETE FROM specific_asset_ids");
		jdbcTemplate.execute("DELETE FROM aas_asset_links");
		jdbcTemplate.execute("DELETE FROM aas_discovery_document");
	}

	@Override
	protected AasDiscoveryService getAasDiscoveryService() {
		return aasDiscoveryService;
	}

}
