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
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.SpecificAssetId;
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
 * Test suite to test the AAS Repository Integration with the AAS Discovery Service
 * Extend this Class to test your implementation
 *
 * @author fried
 */
public abstract class AasRepositoryDiscoveryIntegrationTestSuite {

    protected abstract AasDiscoveryService getDiscoveryService();

    protected abstract String getAASRepositoryURL();

    @Test
    public void createAASWithGlobalAssetId(){
        executeCreateTestWithProperties(true, false);
    }

    @Test
    public void createAASWithSpecificAssetId(){
        executeCreateTestWithProperties(false, true);
    }

    @Test
    public void createAASWithGlobalAssetIdAndSpecificAssetId(){
        executeCreateTestWithProperties(true, true);
    }

    @Test(expected = AssetLinkDoesNotExistException.class)
    public void createAASWithoutGlobalAndSpecificAssetId_expectNoDiscoveryLink(){
        executeCreateTestWithProperties(false, false);
    }

    @Test
    public void updateAAS_withGlobalAssetId() throws IOException {
        try{
            executeCreateTestWithProperties(false, false);
            fail();
        }catch(AssetLinkDoesNotExistException e){
            executeUpdateTestWithProperties(true, false);
        }
    }

    @Test
    public void updateAAS_withSpecificAssetId(){
        try{
            executeCreateTestWithProperties(false, false);
            fail();
        }catch(AssetLinkDoesNotExistException e){
            executeUpdateTestWithProperties(false, true);
        }
    }

    @Test(expected = AssetLinkDoesNotExistException.class)
    public void updateAAS_withoutGlobalAndSpecificAssetId_expectNoDiscoveryLink(){
        executeCreateTestWithProperties(true, false);
        executeUpdateTestWithProperties(false, false);
    }

    @Test
    public void updateAAS_withGlobalAndSpecificAssetId(){
        try{
            executeCreateTestWithProperties(false, false);
            fail();
        }catch(AssetLinkDoesNotExistException e){
            executeUpdateTestWithProperties(true, true);
        }
    }

    @Test(expected = AssetLinkDoesNotExistException.class)
    public void deleteAAS_expectDiscoveryLinkRemoved() throws IOException {
        executeCreateTestWithProperties(true, true);
        AssetAdministrationShell aas = getDemoAAS(false, false);
        BaSyxHttpTestUtils.executeDeleteOnURL(getAASRepositoryURL()+"/"+new Base64UrlEncodedIdentifier(aas.getId()).getEncodedIdentifier());
        List<SpecificAssetId> assetIds = getDiscoveryService().getAllAssetLinksById(aas.getId());
    }

    private void executeCreateTestWithProperties(boolean globalAssetId, boolean specificAssetId) {
        AssetAdministrationShell aas = getDemoAAS(globalAssetId, specificAssetId);
        int expectedCount = 0;
        expectedCount = globalAssetId ? expectedCount + 1 : expectedCount;
        expectedCount = specificAssetId ? expectedCount + 1 : expectedCount;
        try {
            String aasJSON = new ObjectMapper().writeValueAsString(aas);
            CloseableHttpResponse postResponse = BaSyxHttpTestUtils.executePostOnURL(getAASRepositoryURL(),aasJSON);
            if(postResponse.getCode()/100 != 2){
                throw new RuntimeException("POST request to AAS Repository failed with status code: " + postResponse.getCode());
            }
            postResponse.close();
            List<SpecificAssetId> assetIds = getDiscoveryService().getAllAssetLinksById(aas.getId());
            assertEquals(expectedCount,assetIds.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeUpdateTestWithProperties(boolean globalAssetId, boolean specificAssetId) {
        AssetAdministrationShell aas = getDemoAAS(globalAssetId, specificAssetId);
        int expectedCount = 0;
        expectedCount = globalAssetId ? expectedCount + 1 : expectedCount;
        expectedCount = specificAssetId ? expectedCount + 1 : expectedCount;
        try {
            String aasJSON = new ObjectMapper().writeValueAsString(aas);
            CloseableHttpResponse putResponse = BaSyxHttpTestUtils.executePutOnURL(getAASRepositoryURL()+"/"+new Base64UrlEncodedIdentifier(aas.getId()).getEncodedIdentifier(),aasJSON);
            if(putResponse.getCode()/100 != 2){
                System.out.println(getAASRepositoryURL()+"/"+new Base64UrlEncodedIdentifier(aas.getId()).getEncodedIdentifier());
                throw new RuntimeException("PUT request to AAS Repository failed with status code: " + putResponse.getCode());
            }
            putResponse.close();
            List<SpecificAssetId> assetIds = getDiscoveryService().getAllAssetLinksById(aas.getId());
            assertEquals(expectedCount,assetIds.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected AssetAdministrationShell getDemoAAS(boolean hasGlobalAssetId, boolean hasSpecificAssetId){
        DefaultAssetAdministrationShell.Builder builder = new DefaultAssetAdministrationShell.Builder().id("test-aas-id").idShort("taas");
        DefaultAssetInformation.Builder assetInformation = new DefaultAssetInformation.Builder();
        if(hasGlobalAssetId){
            assetInformation.globalAssetId("test-global-asset-id");
        }
        if(hasSpecificAssetId){
            assetInformation.specificAssetIds(new DefaultSpecificAssetId.Builder().name("test-specific-asset-id").value("test-specific-asset-id-value").build());
        }
        builder.assetInformation(assetInformation.build());
        return builder.build();
    }
}
