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

package org.eclipse.digitaltwin.basyx.aasrepository.http;

import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Integration-test for false AAS API usage
 *
 * @author fried
 */
public class TestAasRepositoryHTTPError extends AasRepositoryHTTPErrorSuite {

    private static ConfigurableApplicationContext appContext;

    @BeforeClass
    public static void startAasRepo() {
        appContext = new SpringApplicationBuilder(DummyAasRepositoryComponent.class).profiles("httptests").run();
    }

    @AfterClass
    public static void stopAasRepo() {
        appContext.close();
    }

    @Override
    protected String getURL() {
        return "http://localhost:8080/shells";
    }

    @Override
    public void resetRepository() {
        AasRepository repo = appContext.getBean(AasRepository.class);
        repo.getAllAas(null, null, PaginationInfo.NO_LIMIT)
                .getResult()
                .stream()
                .map(aas -> aas.getId())
                .forEach(repo::deleteAas);
    }
}
