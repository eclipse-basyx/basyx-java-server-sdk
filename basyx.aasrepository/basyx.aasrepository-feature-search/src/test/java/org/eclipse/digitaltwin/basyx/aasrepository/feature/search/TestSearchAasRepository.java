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
package org.eclipse.digitaltwin.basyx.aasrepository.feature.search;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
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
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TestSearchAasRepository {
    private static ConfigurableApplicationContext appContext;
    private static AasRepository searchBackend;
    private static SearchAasRepositoryApiHTTPController searchAPI;

    @BeforeClass
    public static void startAasRepo() throws Exception {
        appContext = new SpringApplicationBuilder(DummySearchAasRepositoryComponent.class).run(new String[] {});
        searchBackend = appContext.getBean(AasRepository.class);
        searchAPI = appContext.getBean(SearchAasRepositoryApiHTTPController.class);
        preloadShells();
        await().atMost(10, SECONDS).until(() ->
                !searchBackend.getAllAas(null, null, new PaginationInfo(0, "")).getResult().isEmpty()
        );
    }

    @Test
    public void testRepo() throws FileNotFoundException, DeserializationException {
        File file = new File(TestSearchAasRepository.class.getResource("/query.json").getFile());
        AASQuery query = queryFromFile(file);
        ResponseEntity<QueryResponse> result = searchAPI.queryAssetAdministrationShells(query, -1, new Base64UrlEncodedCursor(""));
        QueryResponse response = result.getBody();
        assert response != null;
        List<AssetAdministrationShell> shells = response.result.stream().map(o->(AssetAdministrationShell)o).toList();
        Assert.assertEquals(4,shells.size());
    }

    private static AASQuery queryFromFile(File file) throws FileNotFoundException, DeserializationException {
        JsonDeserializer deserializer = new JsonDeserializer();
        return deserializer.read(new FileInputStream(file), AASQuery.class);
    }

    private static Environment envFromFile(File file) throws FileNotFoundException, DeserializationException {
        JsonDeserializer deserializer = new JsonDeserializer();
        return deserializer.read(new FileInputStream(file), Environment.class);
    }

    private static void preloadShells() throws FileNotFoundException, DeserializationException {
        File file = new File(TestSearchAasRepository.class.getResource("/Example-Full.json").getFile());
        Environment env = envFromFile(file);
        for(AssetAdministrationShell aas : env.getAssetAdministrationShells()) {
            searchBackend.createAas(aas);
        }
    }

    @AfterClass
    public static void shutdownAasRepo() {
        searchBackend.getAllAas(null, null, new PaginationInfo(0, "")).getResult().forEach(aas -> {
            try {
                searchBackend.deleteAas(aas.getId());
            } catch (Exception e) {
                // Ignore exceptions during cleanup
            }
        });
        appContext.close();
    }

}
