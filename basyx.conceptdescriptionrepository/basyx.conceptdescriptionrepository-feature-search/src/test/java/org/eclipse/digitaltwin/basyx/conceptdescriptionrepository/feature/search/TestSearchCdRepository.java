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
package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.search;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.basyx.conceptdescription.feature.search.SearchCdRepositoryApiHTTPController;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.querycore.query.model.AASQuery;
import org.eclipse.digitaltwin.basyx.querycore.query.model.QueryResponse;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class TestSearchCdRepository {
    private static ConfigurableApplicationContext appContext;
    private static ConceptDescriptionRepository searchBackend;
    private static SearchCdRepositoryApiHTTPController searchAPI;

    @BeforeClass
    public static void startCdRepo() throws Exception {
        appContext = new SpringApplicationBuilder(DummySearchCdRepositoryComponent.class).run(new String[] {});
        searchBackend = appContext.getBean(ConceptDescriptionRepository.class);
        searchAPI = appContext.getBean(SearchCdRepositoryApiHTTPController.class);
        preloadCds();
        waitForData();
    }

    @Test
    public void testRepo() throws FileNotFoundException, DeserializationException {
        File file = new File(TestSearchCdRepository.class.getResource("/query.json").getFile());
        AASQuery query = queryFromFile(file);
        ResponseEntity<QueryResponse> result = searchAPI.queryConceptDescriptions(100, new Base64UrlEncodedCursor(""), query);
        QueryResponse response = result.getBody();
        assert response != null;
        List<ConceptDescription> topHits = response.result.stream().map(o->(ConceptDescription)o).toList();
        Assert.assertEquals(1,topHits.size());
    }

    private static AASQuery queryFromFile(File file) throws FileNotFoundException, DeserializationException {
        JsonDeserializer deserializer = new JsonDeserializer();
        return deserializer.read(new FileInputStream(file), AASQuery.class);
    }

    private static Environment envFromFile(File file) throws FileNotFoundException, DeserializationException {
        JsonDeserializer deserializer = new JsonDeserializer();
        return deserializer.read(new FileInputStream(file), Environment.class);
    }

    private static void preloadCds() throws FileNotFoundException, DeserializationException {
        File file = new File(TestSearchCdRepository.class.getResource("/Example-Full.json").getFile());
        Environment env = envFromFile(file);
        for(ConceptDescription cd : env.getConceptDescriptions()) {
            searchBackend.createConceptDescription(cd);
        }
    }

    @AfterClass
    public static void shutdownCdRepo() {
        resetRepo();
        appContext.close();
    }

    private static void resetRepo() {
        searchBackend.getAllConceptDescriptions(new PaginationInfo(0, "")).getResult().forEach(cd -> {
            try {
                searchBackend.deleteConceptDescription(cd.getId());
            } catch (Exception e) {
                // Ignore exceptions during cleanup
            }
        });
    }

    private static void waitForData() throws InterruptedException {
        Thread.sleep(2000);
    }

}
