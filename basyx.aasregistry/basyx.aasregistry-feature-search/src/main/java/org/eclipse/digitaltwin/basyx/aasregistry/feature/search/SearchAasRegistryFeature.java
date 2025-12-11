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
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorageFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Hierarchical {@link AasRegistryStorage} feature
 *
 * When this feature is enabled, AAS Descriptors will be indexed with ElasticSearch
 *
 * @author jannisjung, zielstor
 */

@Component
@ConditionalOnExpression("#{${" + SearchAasRegistryFeature.FEATURENAME + ".enabled:false} or ${basyx.feature.experimental.search.enabled:false}}")
@Order(1)
public class SearchAasRegistryFeature implements AasRegistryStorageFeature {
    public final static String FEATURENAME = "basyx.aasregistry.feature.experimental.search";
    public static final String DEFAULT_INDEX = "aas-descr-index";
    private final ElasticsearchClient esclient;

    public SearchAasRegistryFeature(ElasticsearchClient esclient) {
        this.esclient = esclient;
    }

    @Value("#{${" + FEATURENAME + ".enabled:false} or ${basyx.feature.experimental.search.enabled:false}}")
    private boolean enabled;

    @Value("${" + FEATURENAME + ".indexname:" + DEFAULT_INDEX + "}")
    private String indexName;

    @Override
    public AasRegistryStorage decorate(AasRegistryStorage aasRegistryStorage) {
       return new SearchAasRegistryStorage(aasRegistryStorage, esclient, indexName);
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
