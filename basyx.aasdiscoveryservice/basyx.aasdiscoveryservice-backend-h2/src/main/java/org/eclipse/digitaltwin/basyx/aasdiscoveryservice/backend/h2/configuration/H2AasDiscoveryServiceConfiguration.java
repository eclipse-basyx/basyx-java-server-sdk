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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.h2.configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.h2.backend.H2CrudAasDiscoveryFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Properties;

/**
 * Configuration to add H2 to the Spring Context if basyx.backend is set to "InMemory".
 * It dynamically injects the required properties only if they are not already set.
 *
 * Author: fried
 */
@Configuration
@ConditionalOnProperty(name = "basyx.backend", havingValue = "InMemory")
@EnableJpaRepositories(basePackages = "org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.h2.backend")
@EntityScan(basePackages = {
        "org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.h2.backend",
        "org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.h2.dto"
})
@Import(H2CrudAasDiscoveryFactory.class)
public class H2AasDiscoveryServiceConfiguration {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Injects H2 properties dynamically only if they are not set in application.properties.
     */
    public H2AasDiscoveryServiceConfiguration(Environment environment) {
        if (environment instanceof ConfigurableEnvironment configurableEnvironment) {
            MutablePropertySources propertySources = configurableEnvironment.getPropertySources();
            Properties properties = new Properties();

            // Only set the property if it is missing
            setPropertyIfMissing(environment, properties, "spring.h2.console.enabled", "true");
            setPropertyIfMissing(environment, properties, "spring.datasource.url", "jdbc:h2:mem:testdb");
            setPropertyIfMissing(environment, properties, "spring.datasource.driverClassName", "org.h2.Driver");
            setPropertyIfMissing(environment, properties, "spring.datasource.username", "sa");
            setPropertyIfMissing(environment, properties, "spring.datasource.password", "");
            setPropertyIfMissing(environment, properties, "spring.jpa.hibernate.ddl-auto", "create-drop");
            setPropertyIfMissing(environment, properties, "spring.jpa.database-platform", "org.hibernate.dialect.H2Dialect");

            if (!properties.isEmpty()) {
                propertySources.addFirst(new PropertiesPropertySource("inMemoryH2Config", properties));
            }
        }
    }

    /**
     * Sets a property only if it is not already present in the environment.
     */
    private void setPropertyIfMissing(Environment environment, Properties properties, String key, String defaultValue) {
        if (!environment.containsProperty(key)) {
            properties.put(key, defaultValue);
        }
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
