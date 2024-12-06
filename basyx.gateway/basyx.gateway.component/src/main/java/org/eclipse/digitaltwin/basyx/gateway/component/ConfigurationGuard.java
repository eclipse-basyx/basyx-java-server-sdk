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

package org.eclipse.digitaltwin.basyx.gateway.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that prints error and warning messages to inform the user about possible misconfiguration
 *
 * @author fried
 */
@Component
public class ConfigurationGuard implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationGuard.class);

    @Value("${basyx.gateway.aas-repository:#{null}}")
    public String aasRepositoryURL;
    @Value("${basyx.gateway.submodel-repository:#{null}}")
    public String submodelRepositoryURL;
    @Value("${basyx.gateway.aas-registry:#{null}}")
    public String aasRegistryURL;
    @Value("${basyx.gateway.submodel-registry:#{null}}")
    public String submodelRegistryURL;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<String> missingNonRequiredProperties = getMissingNonRequiredProperties();
        if (!missingNonRequiredProperties.isEmpty()) {
            printWarning(missingNonRequiredProperties);
        }

        logger.info(":::::::::::::::: BaSyx Gateway Configuration ::::::::::::::::");
        if (aasRepositoryURL != null) {
            logger.info(":: Default AAS Repository URL:         " + aasRepositoryURL);
        }
        if (submodelRepositoryURL != null) {
            logger.info(":: Default Submodel Repository URL:    " + submodelRepositoryURL);
        }
        if (aasRegistryURL != null) {
            logger.info(":: Default AAS Registry URL:           " + aasRegistryURL);
        }
        if (submodelRegistryURL != null) {
            logger.info(":: Default Submodel Registry URL:      " + submodelRegistryURL);
        }
        logger.info(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::\n");
    }


    private static void printWarning(List<String> missingNonRequiredProperties) {
        System.err.println(
                "\n:::::: BaSyx Gateway Startup Warning ::::::" +
                "\n\nThe following, recommended, properties are missing: \n"
                + missingNonRequiredProperties.stream()
                .map(prop -> "- " + prop)
                .collect(Collectors.joining("\n"))+"\n"
        );
    }

    private List<String> getMissingNonRequiredProperties() {
        List<String> missingNonRequiredProperties = new ArrayList<>();
        if(this.aasRegistryURL == null) {
            missingNonRequiredProperties.add("basyx.gateway.aas-registry");
        }
        if(this.submodelRegistryURL == null) {
            missingNonRequiredProperties.add("basyx.gateway.submodel-registry");
        }
        if(this.aasRepositoryURL == null) {
            missingNonRequiredProperties.add("basyx.gateway.aas-repository");
        }
        if(this.submodelRegistryURL == null) {
            missingNonRequiredProperties.add("basyx.gateway.submodel-repository");
        }
        return missingNonRequiredProperties;
    }
}
