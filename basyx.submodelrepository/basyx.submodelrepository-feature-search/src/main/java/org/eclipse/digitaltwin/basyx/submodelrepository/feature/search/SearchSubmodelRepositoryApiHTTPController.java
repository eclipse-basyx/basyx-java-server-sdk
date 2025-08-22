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

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.querycore.query.executor.ESQueryExecutor;
import org.eclipse.digitaltwin.basyx.querycore.query.model.AASQuery;
import org.eclipse.digitaltwin.basyx.querycore.query.model.QueryResponse;
import org.eclipse.digitaltwin.basyx.querycore.query.model.QueryResult;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-01-10T15:59:05.892Z[GMT]")
@RestController
@ConditionalOnExpression("#{${" + SearchSubmodelRepositoryFeature.FEATURENAME + ".enabled:false} or ${basyx.feature.experimental.search.enabled:false}}")
public class SearchSubmodelRepositoryApiHTTPController implements SearchSubmodelRepositoryHTTPApi {

	private final ElasticsearchClient client;
	private final SubmodelRepository backend;

	@Value("${" + SearchSubmodelRepositoryFeature.FEATURENAME + ".indexname:" + SearchSubmodelRepositoryFeature.DEFAULT_INDEX + "}")
	private String indexName;

	@Autowired
	public SearchSubmodelRepositoryApiHTTPController(ElasticsearchClient client, SubmodelRepository backend) {
		this.client = client;
		this.backend = backend;
	}

	@Override
	public ResponseEntity<QueryResponse> querySubmodels(AASQuery query, Integer limit, Base64UrlEncodedCursor cursor) {
        QueryResponse queryResponse = null;
        try {
			if (query.get$select() != null && query.get$select().equals("id")) {
				queryResponse = getQueryResponse(query, limit, cursor);
			} else {
				// Hard Code to only retrieve ids -> Fetching the actual Submodels from MongoDB
				query.set$select("id");
				queryResponse = getQueryResponse(query, limit, cursor);
				queryResponse.paging_metadata.resulType = "Submodel";
				List<Submodel> submodels = new ArrayList<>();
				for (Object id : queryResponse.result) {
					String identifier = ((ObjectNode) id).get("id").asText();
					Submodel submodel = backend.getSubmodel(identifier);
					submodels.add(submodel);

				}
				//Stream the submodel list to a generic List<Object>
                queryResponse.result = submodels.stream()
						.map(sm -> (Object) sm)
						.toList();
			}
		} catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<QueryResponse>(queryResponse, HttpStatus.OK);
	}

	private QueryResponse getQueryResponse(AASQuery query, Integer limit, Base64UrlEncodedCursor cursor) throws IOException {
		QueryResponse queryResponse;
		ESQueryExecutor executor = new ESQueryExecutor(client, indexName, "Submodel");
		queryResponse = executor.executeQueryAndGetResponse(query, limit, cursor);
		return queryResponse;
	}

}
