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

import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceFactory;
import org.eclipse.digitaltwin.basyx.aasservice.backend.AasBackend;
import org.eclipse.digitaltwin.basyx.aasservice.backend.CrudAasServiceFactory;
import org.eclipse.digitaltwin.basyx.core.filerepository.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Factory component for the {@link CrudAasRepository}
 * 
 * @author mateusmolina
 * 
 */
@Component
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${basyx.backend:}')")
public class CrudAasRepositoryFactory implements AasRepositoryFactory {

	static final String DEFAULT_AAS_REPO_NAME = "aas-repo";

	private final AasBackend aasRepositoryBackend;
	private final AasServiceFactory aasServiceFactory;
	private final String aasRepositoryName;

	public CrudAasRepositoryFactory(AasBackend aasRepositoryBackend, AasServiceFactory aasServiceFactory, @Value("${basyx.aasrepo.name:"+DEFAULT_AAS_REPO_NAME+"}") String aasRepositoryName) {
		this.aasRepositoryBackend = aasRepositoryBackend;
		this.aasServiceFactory = aasServiceFactory;
		this.aasRepositoryName = aasRepositoryName;
	}

	@Override
	public AasRepository create() {
		return new CrudAasRepository(aasRepositoryBackend, aasServiceFactory, aasRepositoryName);
	}

	/**
	 * Creates a new {@link Builder} for internal use
	 */
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private AasBackend aasRepositoryBackend;
		private FileRepository fileRepository;
		private Optional<String> aasRepositoryName = Optional.empty();

		public Builder backend(AasBackend aasRepositoryBackend) {
			this.aasRepositoryBackend = aasRepositoryBackend;
			return this;
		}

		public Builder fileRepository(FileRepository fileRepository) {
			this.fileRepository = fileRepository;
			return this;
		}

		public Builder repositoryName(String aasRepositoryName) {
			this.aasRepositoryName = Optional.of(aasRepositoryName);
			return this;
		}

		public CrudAasRepositoryFactory buildFactory() {
			assert aasRepositoryBackend != null;
			assert fileRepository != null;

			AasServiceFactory aasServiceFactory = new CrudAasServiceFactory(aasRepositoryBackend, fileRepository);

			return new CrudAasRepositoryFactory(aasRepositoryBackend, aasServiceFactory, aasRepositoryName.orElse(DEFAULT_AAS_REPO_NAME));
		}

		public AasRepository create() {
			return buildFactory().create();
		}
	}

}
