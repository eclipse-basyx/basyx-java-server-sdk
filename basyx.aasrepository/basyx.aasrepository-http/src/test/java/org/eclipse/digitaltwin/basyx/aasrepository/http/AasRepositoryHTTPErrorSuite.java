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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

/**
 * Tests false usage of the AAS Repository HTTP API
 *
 * @author fried
 */
public abstract class AasRepositoryHTTPErrorSuite {

    private static final String NON_EXISTENT_ID = "nonExisting";

    protected abstract String getURL();

    @Before
    @After
    public abstract void resetRepository();

    @Test
    public void getNonExistentAas_returns404() throws IOException {
        String url = getSpecificAasAccessURL(NON_EXISTENT_ID);

        CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(url);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
    }

    @Test
    public void deleteNonExistentAas_returns404() throws IOException {
        String url = getSpecificAasAccessURL(NON_EXISTENT_ID);

        CloseableHttpResponse response = BaSyxHttpTestUtils.executeDeleteOnURL(url);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
    }

    @Test
    public void postInvalidJson_returns400() throws IOException {
        String malformedJson = "{ \"idShort\": \"invalid\" "; // kein Abschluss

        CloseableHttpResponse response = BaSyxHttpTestUtils.executePostOnURL(getURL(), malformedJson);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
    }

    @Test
    public void putAssetInformationOnNonExistentAas_returns404() throws IOException {
        String json = BaSyxHttpTestUtils.readJSONStringFromClasspath("assetInfoSimple.json");

        String url = getSpecificAasAccessURL(NON_EXISTENT_ID) + "/asset-information";

        CloseableHttpResponse response = BaSyxHttpTestUtils.executePutOnURL(url, json);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
    }

    @Test
    public void addDuplicateSubmodelReference_returns409() throws IOException {
        // Vorbereitung
        BaSyxHttpTestUtils.executePostOnURL(getURL(), BaSyxHttpTestUtils.readJSONStringFromClasspath("AasSimple_1.json"));
        String ref = BaSyxHttpTestUtils.readJSONStringFromClasspath("SingleSubmodelReference_1.json");

        String submodelRefUrl = getSpecificAasAccessURL("customIdentifier") + "/submodel-refs";

        BaSyxHttpTestUtils.executePostOnURL(submodelRefUrl, ref); // erster Versuch erfolgreich
        CloseableHttpResponse second = BaSyxHttpTestUtils.executePostOnURL(submodelRefUrl, ref); // zweiter Versuch

        assertEquals(HttpStatus.CONFLICT.value(), second.getCode());
    }

    @Test
    public void getWithInvalidCursor_returns400() throws IOException {
        // Vorbereitung
        BaSyxHttpTestUtils.executePostOnURL(getURL(), BaSyxHttpTestUtils.readJSONStringFromClasspath("AasSimple_1.json"));

        String url = getURL() + "?cursor=x";
        CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(url);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
    }

    private String getSpecificAasAccessURL(String aasId) {
        return getURL() + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(aasId);
    }
}
