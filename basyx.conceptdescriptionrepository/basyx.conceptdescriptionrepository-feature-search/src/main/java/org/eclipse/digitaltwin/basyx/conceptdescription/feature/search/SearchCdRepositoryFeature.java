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

package org.eclipse.digitaltwin.basyx.conceptdescription.feature.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepositoryFactory;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.ConceptDescriptionRepositoryFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@ConditionalOnExpression("#{${" + SearchCdRepositoryFeature.FEATURENAME + ".enabled:false} or ${basyx.feature.experimental.search.enabled:false}}")
@Component
public class SearchCdRepositoryFeature implements ConceptDescriptionRepositoryFeature {
	public static final String FEATURENAME = "basyx.cdrepository.feature.experimental.search";
	public static final String DEFAULT_INDEX = "aas-index";
	private final ElasticsearchClient esclient;

	@Value("#{${" + FEATURENAME + ".enabled:false} or ${basyx.feature.experimental.search.enabled:false}}")
	private boolean enabled;

	@Value("${" + SearchCdRepositoryFeature.FEATURENAME + ".indexname:" + SearchCdRepositoryFeature.DEFAULT_INDEX + "}")
	private String indexName;

	@Autowired
	public SearchCdRepositoryFeature(ElasticsearchClient client) {
		this.esclient = client;
	}

	@Override
	public ConceptDescriptionRepositoryFactory decorate(ConceptDescriptionRepositoryFactory aasServiceFactory) {
		return new SearchCdRepositoryFactory(aasServiceFactory, esclient, indexName);
	}

	@Override
	public void initialize() {
	}

	@Override
	public void cleanUp() {
	}

	@Override
	public String getName() {
		return "CdRepository Search";
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
