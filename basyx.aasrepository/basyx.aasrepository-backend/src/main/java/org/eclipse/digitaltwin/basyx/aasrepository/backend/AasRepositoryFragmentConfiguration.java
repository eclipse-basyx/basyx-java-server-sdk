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

package org.eclipse.digitaltwin.basyx.aasrepository.backend;

import org.eclipse.digitaltwin.basyx.aasservice.AasServiceOperations;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.core.support.RepositoryComposition.RepositoryFragments;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFragment;
import org.springframework.lang.NonNull;

/**
 * Configures the Spring Data repository fragments for the
 * {@link AasRepositoryBackend}
 * 
 * Requires a bean with the name "aasServiceOperations". It is composed with the
 * {@link AasRepositoryBackend} as a {@link RepositoryFragment}
 * 
 * @author mateusmolina
 */
@Configuration
@ConditionalOnBean(name="aasServiceOperations")
public class AasRepositoryFragmentConfiguration implements BeanPostProcessor {

    private final AasServiceOperations aasServiceOperations;

    public AasRepositoryFragmentConfiguration(@Qualifier("aasServiceOperations") AasServiceOperations aasServiceOperations) {
        this.aasServiceOperations = aasServiceOperations;
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (!(bean instanceof RepositoryFactoryBeanSupport<?, ?, ?> beanfactory))
            return bean;

        if (!beanfactory.getObjectType().equals(AasRepositoryBackend.class))
            return bean;

        RepositoryFragment<AasServiceOperations> fragment = RepositoryFragment.implemented(AasServiceOperations.class, aasServiceOperations);
        RepositoryFragments fragments = RepositoryFragments.of(fragment);
        beanfactory.setRepositoryFragments(fragments);

        return bean;
    }

}