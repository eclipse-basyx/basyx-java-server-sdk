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

package org.eclipse.digitaltwin.basyx.submodelrepository.backend;

import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelFileOperations;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelOperations;
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
 * {@link SubmodelRepositoryBackend}
 * 
 * Requires a bean with the name "submodelOperations". It is composed with the
 * {@link SubmodelRepositoryBackend} as a {@link RepositoryFragment}
 * 
 * @author mateusmolina
 */
@Configuration
@ConditionalOnBean(name="submodelOperations")
public class SubmodelRepositoryFragmentConfiguration implements BeanPostProcessor {

    private final SubmodelOperations submodelOperations;
    private final SubmodelFileOperations submodelFileOperations;

    public SubmodelRepositoryFragmentConfiguration(@Qualifier("submodelOperations") SubmodelOperations submodelOperations, @Qualifier("submodelFileOperations") SubmodelFileOperations submodelFileOperations) {
        this.submodelOperations = submodelOperations;
        this.submodelFileOperations = submodelFileOperations;
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (!(bean instanceof RepositoryFactoryBeanSupport<?, ?, ?> beanfactory))
            return bean;

        if (!beanfactory.getObjectType().equals(SubmodelRepositoryBackend.class))
            return bean;

        RepositoryFragment<SubmodelOperations> fragment1 = RepositoryFragment.implemented(SubmodelOperations.class, submodelOperations);
        RepositoryFragment<SubmodelFileOperations> fragment2 = RepositoryFragment.implemented(SubmodelFileOperations.class, submodelFileOperations);
        RepositoryFragments fragments = RepositoryFragments.of(fragment1, fragment2);
        beanfactory.setRepositoryFragments(fragments);

        return bean;
    }

}