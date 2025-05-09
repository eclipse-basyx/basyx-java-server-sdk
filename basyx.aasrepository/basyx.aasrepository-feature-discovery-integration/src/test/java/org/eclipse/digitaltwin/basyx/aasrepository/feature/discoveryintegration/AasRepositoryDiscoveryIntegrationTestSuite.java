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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.discoveryintegration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetId;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.core.exceptions.AssetLinkDoesNotExistException;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test suite to test the AAS Repository Integration with the AAS Discovery Service.
 * Extend this Class to test your implementation.
 *
 * @author fried
 */
public abstract class AasRepositoryDiscoveryIntegrationTestSuite {

    protected abstract AasDiscoveryService getDiscoveryService();
    protected abstract String getAASRepositoryURL();

    // -------------------- CREATE TESTS --------------------

    @Test
    public void createAASWithGlobalAssetId() {
        executeCreateTestWithProperties(true, false);
    }

    @Test
    public void createAASWithSpecificAssetId() {
        executeCreateTestWithProperties(false, true);
    }

    @Test
    public void createAASWithGlobalAssetIdAndSpecificAssetId() {
        executeCreateTestWithProperties(true, true);
    }

    @Test(expected = AssetLinkDoesNotExistException.class)
    public void createAASWithoutGlobalAndSpecificAssetId_expectNoDiscoveryLink() {
        executeCreateTestWithProperties(false, false);
    }

    // -------------------- UPDATE TESTS --------------------

    @Test
    public void updateAAS_withGlobalAssetId() throws IOException {
        try {
            executeCreateTestWithProperties(false, false);
            fail();
        } catch (AssetLinkDoesNotExistException e) {
            executeUpdateTestWithProperties(true, false);
        }
    }

    @Test
    public void updateAAS_withSpecificAssetId() {
        try {
            executeCreateTestWithProperties(false, false);
            fail();
        } catch (AssetLinkDoesNotExistException e) {
            executeUpdateTestWithProperties(false, true);
        }
    }

    @Test(expected = AssetLinkDoesNotExistException.class)
    public void updateAAS_withoutGlobalAndSpecificAssetId_expectNoDiscoveryLink() {
        executeCreateTestWithProperties(true, false);
        executeUpdateTestWithProperties(false, false);
    }

    @Test
    public void updateAAS_withGlobalAndSpecificAssetId() {
        try {
            executeCreateTestWithProperties(false, false);
            fail();
        } catch (AssetLinkDoesNotExistException e) {
            executeUpdateTestWithProperties(true, true);
        }
    }

    @Test
    public void updateAAS_withoutChange() {
        executeCreateTestWithProperties(true, false);
        executeUpdateTestWithProperties(true, false);
        assertOnlyGlobalAssetIdIsSet(getDemoAAS(true,false));
    }

    @Test
    public void updateAAS() throws IOException {
        AssetAdministrationShell aas = getDemoAAS(true, true);
        postAAS(aas);

        addAssetIdToAAS(aas);
        updateAAS(aas);

        assertNewAssetLinksAreSet(aas);
    }

    @Test
    public void updateAAS_AssetInformation() throws IOException {
        AssetAdministrationShell aas = getDemoAAS(true, true);
        postAAS(aas);

        addAssetIdToAAS(aas);
        AssetInformation newInfo = aas.getAssetInformation();
        String assetInfoJSON = new ObjectMapper().writeValueAsString(newInfo);

        CloseableHttpResponse putResponse = BaSyxHttpTestUtils.executePutOnURL(getAASAssetInformationURL(aas), assetInfoJSON);
        throwErrorIfRequestWasUnsuccessful(putResponse, "PUT request to AAS Repository failed with status code: ");
        putResponse.close();

        assertNewAssetLinksAreSet(aas);
    }

    // -------------------- DELETE TEST --------------------

    @Test(expected = AssetLinkDoesNotExistException.class)
    public void deleteAAS_expectDiscoveryLinkRemoved() throws IOException {
        executeCreateTestWithProperties(true, true);
        AssetAdministrationShell aas = getDemoAAS(false, false);
        BaSyxHttpTestUtils.executeDeleteOnURL(getAASURL(aas));
        getDiscoveryService().getAllAssetLinksById(aas.getId());
    }

    // -------------------- HELPER METHODS --------------------

    private String getAASURL(AssetAdministrationShell aas) {
        return getAASRepositoryURL() + "/" + encode(aas.getId());
    }

    private String getAASAssetInformationURL(AssetAdministrationShell aas) {
        return getAASRepositoryURL() + "/" + encode(aas.getId()) + "/asset-information";
    }

    private void executeCreateTestWithProperties(boolean globalAssetId, boolean specificAssetId) {
        AssetAdministrationShell aas = getDemoAAS(globalAssetId, specificAssetId);
        int expectedCount = (globalAssetId ? 1 : 0) + (specificAssetId ? 1 : 0);
        try {
            postAAS(aas);
            List<SpecificAssetId> assetIds = getDiscoveryService().getAllAssetLinksById(aas.getId());
            assertEquals(expectedCount, assetIds.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeUpdateTestWithProperties(boolean globalAssetId, boolean specificAssetId) {
        AssetAdministrationShell aas = getDemoAAS(globalAssetId, specificAssetId);
        int expectedCount = (globalAssetId ? 1 : 0) + (specificAssetId ? 1 : 0);
        try {
            String aasJSON = new ObjectMapper().writeValueAsString(aas);
            CloseableHttpResponse putResponse = BaSyxHttpTestUtils.executePutOnURL(
                    getAASURL(aas), aasJSON);
            throwErrorIfRequestWasUnsuccessful(putResponse, "PUT request to AAS Repository failed with status code: ");
            putResponse.close();
            List<SpecificAssetId> assetIds = getDiscoveryService().getAllAssetLinksById(aas.getId());
            assertEquals(expectedCount, assetIds.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void postAAS(AssetAdministrationShell aas) throws IOException {
        String aasJSON = new ObjectMapper().writeValueAsString(aas);
        CloseableHttpResponse postResponse = BaSyxHttpTestUtils.executePostOnURL(getAASRepositoryURL(), aasJSON);
        throwErrorIfRequestWasUnsuccessful(postResponse, "POST request to AAS Repository failed with status code: ");
        postResponse.close();
    }

    private void updateAAS(AssetAdministrationShell aas) throws IOException {
        String newAasJSON = new ObjectMapper().writeValueAsString(aas);
        CloseableHttpResponse putResponse = BaSyxHttpTestUtils.executePutOnURL(
                getAASURL(aas), newAasJSON);
        throwErrorIfRequestWasUnsuccessful(putResponse, "PUT request to AAS Repository failed with status code: ");
        putResponse.close();
    }

    private void assertNewAssetLinksAreSet(AssetAdministrationShell aas) {
        List<SpecificAssetId> assetIds = getDiscoveryService().getAllAssetLinksById(aas.getId());
        assertEquals(3, assetIds.size());
        assertEquals("test-specific-asset-id", assetIds.get(0).getName());
        assertEquals("test-specific-asset-id-value", assetIds.get(0).getValue());
        assertEquals("test-specific-asset-id-2", assetIds.get(1).getName());
        assertEquals("test-specific-asset-id-value-2", assetIds.get(1).getValue());
    }

    private void assertOnlyGlobalAssetIdIsSet(AssetAdministrationShell aas) {
        List<SpecificAssetId> assetIds = getDiscoveryService().getAllAssetLinksById(aas.getId());
        assertEquals(1, assetIds.size());
        assertEquals("globalAssetId", assetIds.get(0).getName());
        assertEquals("test-global-asset-id", assetIds.get(0).getValue());
    }

    private static void addAssetIdToAAS(AssetAdministrationShell aas) {
        AssetInformation info = aas.getAssetInformation();
        List<SpecificAssetId> assetIds = info.getSpecificAssetIds();
        assetIds.add(new DefaultSpecificAssetId.Builder()
                .name("test-specific-asset-id-2")
                .value("test-specific-asset-id-value-2")
                .build());
        info.setSpecificAssetIds(assetIds);
        aas.setAssetInformation(info);
    }

    private static void throwErrorIfRequestWasUnsuccessful(CloseableHttpResponse response, String message) {
        if (response.getCode() / 100 != 2) {
            throw new RuntimeException(message + response.getCode());
        }
    }

    private static String encode(String id) {
        return new Base64UrlEncodedIdentifier(id).getEncodedIdentifier();
    }

    protected AssetAdministrationShell getDemoAAS(boolean hasGlobalAssetId, boolean hasSpecificAssetId) {
        DefaultAssetInformation.Builder assetInfoBuilder = new DefaultAssetInformation.Builder();
        if (hasGlobalAssetId) assetInfoBuilder.globalAssetId("test-global-asset-id");
        if (hasSpecificAssetId) {
            assetInfoBuilder.specificAssetIds(new DefaultSpecificAssetId.Builder()
                    .name("test-specific-asset-id")
                    .value("test-specific-asset-id-value")
                    .build());
        }
        return new DefaultAssetAdministrationShell.Builder()
                .id("test-aas-id")
                .idShort("taas")
                .assetInformation(assetInfoBuilder.build())
                .build();
    }
}
