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

package org.eclipse.digitaltwin.basyx.submodelregistry.feature.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorageFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Hierarchical {@link SubmodelRegistryStorage} feature
 *
 * When this feature is enabled, Submodel Descriptors will be indexed with ElasticSearch
 *
 * @author jannisjung, zielstor
 */

@Component
@ConditionalOnExpression("#{${" + SearchSubmodelRegistryFeature.FEATURENAME + ".enabled:false} or ${basyx.feature.experimental.search.enabled:false}}")
@Order(1)
public class SearchSubmodelRegistryFeature implements SubmodelRegistryStorageFeature {
    public final static String FEATURENAME = "basyx.submodelregistry.feature.experimental.search";
    public static final String DEFAULT_INDEX = "sm-descr-index";
    private final ElasticsearchClient esclient;

    public SearchSubmodelRegistryFeature(ElasticsearchClient esclient) {
        this.esclient = esclient;
    }

    @Value("#{${" + FEATURENAME + ".enabled:false} or ${basyx.feature.experimental.search.enabled:false}}")
    private boolean enabled;

    @Value("${" + FEATURENAME + ".indexname:" + DEFAULT_INDEX + "}")
    private String indexName;

    @Override
    public SubmodelRegistryStorage decorate(SubmodelRegistryStorage smRegistryStorage) {
       return new SearchSubmodelRegistryStorage(smRegistryStorage, esclient, indexName);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return FEATURENAME;
    }
}
