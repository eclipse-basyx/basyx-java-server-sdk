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

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepositoryFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.CrudSubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.SubmodelBackend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

/**
 * Simple Submodel repository factory that creates a
 * {@link CrudSubmodelRepository} with a backend provider and a service factory
 *
 * @author mateusmolina, danish
 */
@Component
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${basyx.backend:}')")
public class CrudSubmodelRepositoryFactory implements SubmodelRepositoryFactory {

    static final String DEFAULT_REPOSITORY_NAME = "sm-repo";

    private final SubmodelBackend backend;
    private final SubmodelServiceFactory submodelServiceFactory;
    private final String submodelRepositoryName;
    private Optional<Collection<Submodel>> submodels = Optional.empty();

    @Autowired
    public CrudSubmodelRepositoryFactory(SubmodelBackend submodelRepositoryBackend, SubmodelServiceFactory submodelServiceFactory, @Value("${basyx.smrepo.name:" + DEFAULT_REPOSITORY_NAME + "}") String submodelRepositoryName) {
        this.backend = submodelRepositoryBackend;
        this.submodelServiceFactory = submodelServiceFactory;
        this.submodelRepositoryName = submodelRepositoryName;
    }

    public void setRemoteCollection(Collection<Submodel> submodels) {
        this.submodels = Optional.of(submodels);
    }

    @Override
    public SubmodelRepository create() {
        return submodels.map(submodelCollection -> new CrudSubmodelRepository(backend, submodelServiceFactory, submodelRepositoryName, submodelCollection))
                .orElseGet(() -> new CrudSubmodelRepository(backend, submodelServiceFactory, submodelRepositoryName));
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private SubmodelBackend submodelBackend;
        private FileRepository fileRepository;
        private Optional<String> repositoryName = Optional.empty();
        private Optional<Collection<Submodel>> submodels = Optional.empty();

        public Builder backend(SubmodelBackend submodelBackend) {
            this.submodelBackend = submodelBackend;
            return this;
        }

        public Builder fileRepository(FileRepository fileRepository) {
            this.fileRepository = fileRepository;
            return this;
        }

        public Builder repositoryName(String repositoryName){
            this.repositoryName = Optional.of(repositoryName);
            return this;
        }

        public Builder remoteCollection(Collection<Submodel> submodels){
            this.submodels = Optional.of(submodels);
            return this;
        }

        public CrudSubmodelRepositoryFactory buildFactory(){
            assert submodelBackend != null;
            assert fileRepository != null;

            CrudSubmodelServiceFactory submodelServiceFactory = new CrudSubmodelServiceFactory(submodelBackend, fileRepository);

            CrudSubmodelRepositoryFactory submodelRepositoryFactory = new CrudSubmodelRepositoryFactory(submodelBackend, submodelServiceFactory, repositoryName.orElse(DEFAULT_REPOSITORY_NAME));

            submodels.ifPresent(submodelRepositoryFactory::setRemoteCollection);

            return submodelRepositoryFactory;
        }

        public SubmodelRepository create(){
            return buildFactory().create();
        }

    }

}
