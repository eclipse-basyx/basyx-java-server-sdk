/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.component;

import static org.junit.Assert.assertEquals;

import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 
 * Test the configuration of the AasRepository's name
 *
 * @author danish, kammognie
 *
 */
public class TestConceptDescriptionRepositoryName {
	private static final String CONFIGURED_CD_REPO_NAME = "configured-cd-repo-name";
	private static final String BASYX_CDREPO_NAME_KEY = "basyx.cdrepo.name";
	private static ConfigurableApplicationContext appContext;

	public void startContext() {
		appContext = new SpringApplication(ConceptDescriptionRepositoryComponent.class).run(new String[] {});
	}

	public static void closeContext() {
		appContext.close();
	}

	@Test
	public void getDefaultRepoName() {
		startContext();
		
		ConceptDescriptionRepository repo = appContext.getBean(ConceptDescriptionRepository.class);
		
		assertEquals("cd-repo", repo.getName());
		
		closeContext();
	}

	@Test
	public void getConfiguredRepoName() {
		configureRepoNamePropertyAndStartContext();
		
		ConceptDescriptionRepository repo = appContext.getBean(ConceptDescriptionRepository.class);
		
		assertEquals(CONFIGURED_CD_REPO_NAME, repo.getName());
		
		resetRepoNamePropertyAndCloseContext();
	}

	private void resetRepoNamePropertyAndCloseContext() {
		System.clearProperty(BASYX_CDREPO_NAME_KEY);
		
		closeContext();
	}

	private void configureRepoNamePropertyAndStartContext() {
		System.setProperty(BASYX_CDREPO_NAME_KEY, CONFIGURED_CD_REPO_NAME);
		
		startContext();
	}

}
