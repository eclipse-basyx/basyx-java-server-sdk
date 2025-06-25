/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.querycore.query.model.AASQuery;
import org.eclipse.digitaltwin.basyx.querycore.query.model.QueryResponse;
import org.eclipse.digitaltwin.basyx.querycore.query.executor.ESQueryExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-01-10T15:59:05.892Z[GMT]")
@RestController
@ConditionalOnExpression("#{${" + SearchAasRepositoryFeature.FEATURENAME + ".enabled:false} or ${basyx.feature.search.enabled:false}}")
public class SearchAasRepositoryApiHTTPController implements SearchAasRepositoryHTTPApi {

	private final ElasticsearchClient client;

	@Value("${" + SearchAasRepositoryFeature.FEATURENAME + ".indexname:" + SearchAasRepositoryFeature.DEFAULT_INDEX + "}")
	private String indexName;

	@Autowired
	public SearchAasRepositoryApiHTTPController(ElasticsearchClient client) {
		this.client = client;
	}

	@Override
	public ResponseEntity<QueryResponse> queryAssetAdministrationShells(AASQuery query, Integer limit, Base64UrlEncodedCursor cursor) {
        QueryResponse queryResponse = null;
        try {
			ESQueryExecutor executor = new ESQueryExecutor(client, indexName, "AssetAdministrationShell");
            queryResponse = executor.executeQueryAndGetResponse(query, limit, cursor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<QueryResponse>(queryResponse, HttpStatus.OK);
	}

}
