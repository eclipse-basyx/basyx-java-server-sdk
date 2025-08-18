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
package org.eclipse.digitaltwin.basyx.submodelrepository.feature.search;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.querycore.query.model.AASQuery;
import org.eclipse.digitaltwin.basyx.querycore.query.model.QueryResponse;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
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

public class TestSearchSubmodelRepository {
    private static ConfigurableApplicationContext appContext;
    private static SubmodelRepository searchBackend;
    private static SearchSubmodelRepositoryApiHTTPController searchAPI;

    @BeforeClass
    public static void startSmRepo() throws Exception {
        appContext = new SpringApplicationBuilder(DummySearchSubmodelRepositoryComponent.class).run(new String[] {});
        searchBackend = appContext.getBean(SubmodelRepository.class);
        searchAPI = appContext.getBean(SearchSubmodelRepositoryApiHTTPController.class);
        preloadSubmodels();
    }

    @Test
    public void testRepo() throws FileNotFoundException, DeserializationException {
        File file = new File(TestSearchSubmodelRepository.class.getResource("/contains.json").getFile());
        AASQuery query = queryFromFile(file);
        ResponseEntity<QueryResponse> result = searchAPI.querySubmodels(query, -1, new Base64UrlEncodedCursor(""));
        QueryResponse response = result.getBody();
        assert response != null;
        List<Submodel> submodels = response.result.stream().map(o->(Submodel)o).toList();
        Assert.assertEquals(3,submodels.size());
    }

    private static AASQuery queryFromFile(File file) throws FileNotFoundException, DeserializationException {
        JsonDeserializer deserializer = new JsonDeserializer();
        return deserializer.read(new FileInputStream(file), AASQuery.class);
    }

    private static Environment envFromFile(File file) throws FileNotFoundException, DeserializationException {
        JsonDeserializer deserializer = new JsonDeserializer();
        return deserializer.read(new FileInputStream(file), Environment.class);
    }

    private static void preloadSubmodels() throws FileNotFoundException, DeserializationException {
        File file = new File(TestSearchSubmodelRepository.class.getResource("/Example-Full.json").getFile());
        Environment env = envFromFile(file);
        for(Submodel submodel : env.getSubmodels()) {
            searchBackend.createSubmodel(submodel);
        }
    }

    @AfterClass
    public static void shutdownAasRepo() {
        searchBackend.getAllSubmodels(new PaginationInfo(0, "")).getResult().forEach(submodel -> {
            try {
                searchBackend.deleteSubmodel(submodel.getId());
            } catch (Exception e) {
                // Ignore exceptions during cleanup
            }
        });
        appContext.close();
    }

    private String getURL() {
        return "http://localhost:8081/submodels";
    }

}
