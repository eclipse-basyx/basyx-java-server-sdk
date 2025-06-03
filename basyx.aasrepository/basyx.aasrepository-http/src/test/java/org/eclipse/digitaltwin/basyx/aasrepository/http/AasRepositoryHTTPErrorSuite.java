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
 * Tests fehlerhafte Nutzung der AAS Repository HTTP API
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
