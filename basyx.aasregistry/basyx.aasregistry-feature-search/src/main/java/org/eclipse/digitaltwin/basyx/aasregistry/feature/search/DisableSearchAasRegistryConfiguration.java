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

import org.springframework.boot.actuate.autoconfigure.data.elasticsearch.ElasticsearchReactiveHealthContributorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.elastic.ElasticMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ReactiveElasticsearchClientAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to prevent Elasticsearch from being injected by Spring
 * 
 */
@Configuration
@ConditionalOnExpression("!(${" + SearchAasRegistryFeature.FEATURENAME + ".enabled:false} || ${basyx.feature.experimental.search.enabled:false})")
@EnableAutoConfiguration(exclude = {
        ElasticsearchClientAutoConfiguration.class,
        ElasticsearchRepositoriesAutoConfiguration.class,
        ElasticsearchDataAutoConfiguration.class,
        ElasticsearchRestClientAutoConfiguration.class,
        ElasticsearchReactiveHealthContributorAutoConfiguration.class,
        ElasticMetricsExportAutoConfiguration.class,
        ReactiveElasticsearchClientAutoConfiguration.class,
        ReactiveElasticsearchRepositoriesAutoConfiguration.class
})
public class DisableSearchAasRegistryConfiguration {
}
