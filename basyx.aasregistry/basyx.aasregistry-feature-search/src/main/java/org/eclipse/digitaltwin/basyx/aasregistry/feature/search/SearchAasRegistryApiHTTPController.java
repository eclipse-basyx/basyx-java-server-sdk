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

package org.eclipse.digitaltwin.basyx.aasregistry.feature.search;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.esql.ElasticsearchEsqlClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.querycore.query.AASQuery;
import org.eclipse.digitaltwin.basyx.querycore.query.QueryPaging;
import org.eclipse.digitaltwin.basyx.querycore.query.QueryResponse;
import org.eclipse.digitaltwin.basyx.querycore.query.QueryResult;
import org.eclipse.digitaltwin.basyx.querycore.query.converter.ElasticSearchRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2025-06-18T09:42:17.580283867Z[GMT]")
@RestController
public class SearchAasRegistryApiHTTPController implements SearchAasRegistryHTTPApi {

    private static final Logger log = LoggerFactory.getLogger(SearchAasRegistryApiHTTPController.class);

    private final ElasticsearchClient esClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public SearchAasRegistryApiHTTPController(ObjectMapper objectMapper, ElasticsearchClient esClient) {
        this.esClient = esClient;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<QueryResponse<AssetAdministrationShellDescriptor>> queryAssetAdministrationShellDescriptors(Integer limit, String cursor, AASQuery query) {
        ElasticSearchRequestBuilder builder = new ElasticSearchRequestBuilder();
        SearchRequest searchRequest = builder.buildSearchRequest(query, SearchAasRegistryStorage.ES_INDEX);
        try {
            SearchResponse<Object> response = esClient.search(searchRequest, Object.class);
            List<Hit<Object>> hits = response.hits().hits();
            List<AssetAdministrationShellDescriptor> descriptors = hits.stream()
                    .map(hit -> objectMapper.convertValue(hit.source(), AssetAdministrationShellDescriptor.class))
                    .collect(Collectors.toList());

            QueryPaging queryPaging = new QueryPaging("not implemented", "AssetAdministrationShellDescriptors");
            QueryResult<AssetAdministrationShellDescriptor> queryResult = new QueryResult<AssetAdministrationShellDescriptor>(descriptors);
            QueryResponse<AssetAdministrationShellDescriptor> queryResponse = new QueryResponse<>(queryPaging, queryResult);
            return ResponseEntity.ok(queryResponse);
        } catch (IOException e) {
            log.error("Error executing search request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
