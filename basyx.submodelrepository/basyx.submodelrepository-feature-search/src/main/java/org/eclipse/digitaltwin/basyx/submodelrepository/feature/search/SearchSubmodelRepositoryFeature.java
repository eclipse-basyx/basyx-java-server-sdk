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
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.SubmodelRepositoryFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@ConditionalOnExpression("#{${" + SearchSubmodelRepositoryFeature.FEATURENAME + ".enabled:false} or ${basyx.feature.search.enabled:false}}")
@Component
public class SearchSubmodelRepositoryFeature implements SubmodelRepositoryFeature {
	public static final String FEATURENAME = "basyx.submodelrepository.feature.search";
	public static final String DEFAULT_INDEX = "sm-index";
	private final ElasticsearchClient esclient;

	@Value("#{${" + FEATURENAME + ".enabled:false} or ${basyx.feature.search.enabled:false}}")
	private boolean enabled;

	@Value("${" + SearchSubmodelRepositoryFeature.FEATURENAME + ".indexname:" + SearchSubmodelRepositoryFeature.DEFAULT_INDEX + "}")
	private String indexName;

	@Autowired
	public SearchSubmodelRepositoryFeature(ElasticsearchClient client) {
		this.esclient = client;
	}

	@Override
	public SubmodelRepositoryFactory decorate(SubmodelRepositoryFactory submodelServiceFactory) {
		return new SearchSubmodelRepositoryFactory(submodelServiceFactory, esclient, indexName);
	}

	@Override
	public void initialize() {
	}

	@Override
	public void cleanUp() {

	}

	@Override
	public String getName() {
		return "SubmodelRepository Search";
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
