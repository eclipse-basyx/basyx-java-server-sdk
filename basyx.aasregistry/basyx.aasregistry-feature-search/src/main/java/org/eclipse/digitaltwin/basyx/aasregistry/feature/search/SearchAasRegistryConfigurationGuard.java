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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * Class that prints error and warning messages to inform the user about possible misconfiguration
 *
 * @author jannisjung, aaronzi
 */
@Component
@ConditionalOnExpression("#{${" + SearchAasRegistryFeature.FEATURENAME + ".enabled:false} or ${basyx.feature.experimental.search.enabled:false}}")
public class SearchAasRegistryConfigurationGuard implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(SearchAasRegistryConfigurationGuard.class);

    @Value("${spring.elasticsearch.uris:#{null}}")
    private String elasticsearchUrl;

    @Value("${spring.elasticsearch.username:#{null}}")
    private String elasticsearchUsername;

    @Value("${spring.elasticsearch.password:#{null}}")
    private String elasticsearchPassword;

    @Value("${" + SearchAasRegistryFeature.FEATURENAME + ".indexname:" + SearchAasRegistryFeature.DEFAULT_INDEX + "}")
    private String indexName;

    @Override
    public void afterPropertiesSet() throws Exception {
        boolean error = false;
        logger.info(":::::::::::::::: BaSyx Feature Search Configuration ::::::::::::::::");

        if (elasticsearchUrl == null || elasticsearchUrl.isEmpty()) {
            logger.error("Elasticsearch URL is not configured. Please set the property 'spring.elasticsearch.uris'.");
            error = true;
        } else {
            logger.info("Elasticsearch URL: " + elasticsearchUrl);
        }

        if (elasticsearchUsername == null || elasticsearchUsername.isEmpty()) {
            logger.error("Elasticsearch username is not configured. Please set the property 'spring.elasticsearch.username'.");
            error = true;
        } else {
            logger.info("Elasticsearch Username: " + elasticsearchUsername);
        }

        if (elasticsearchPassword == null || elasticsearchPassword.isEmpty()) {
            logger.error("Elasticsearch password is not configured. Please set the property 'spring.elasticsearch.password'.");
            error = true;
        } else {
            logger.info("Elasticsearch Password: " + "***");
        }

        if (indexName == null || indexName.isEmpty()) {
            logger.error("Index name is not configured. Please set the property '" + SearchAasRegistryFeature.FEATURENAME + ".indexname'.");
            error = true;
        } else {
            logger.info("Index Name: " + indexName);
        }

        logger.info(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");
        if(error){
            System.exit(1);
        }
    }
}
