/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasxfileserver.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.BaSyxPackageDescription;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestAASXFileServerHTTP {

    public static final String TEST_AASX_FILENAME = "test.aasx";
    private static ConfigurableApplicationContext appContext;
    private static final String ACCESS_URL = "http://localhost:4050";

    @BeforeClass
    public static void setUpClass(){
        appContext = new SpringApplication(DummyAASXFileServer.class).run(new String[]{});
    }

    @AfterClass
    public static void tearDownClass(){
        appContext.close();
    }

    @Test
    public void testAASXFileLifecycle() throws IOException, NoSuchAlgorithmException, ParseException {
        File aasxFile = loadAASXFromResources(TEST_AASX_FILENAME);

        CloseableHttpClient client = HttpClients.createDefault();

        ArrayList<String> aasIds = getDummyAasIdList();

        CloseableHttpResponse uploadFileResponse = uploadFile(client,aasIds,aasxFile);
        assertEquals(201,uploadFileResponse.getCode());

        BaSyxPackageDescription packageDescription = getBaSyxPackageDescriptionFromResponse(BaSyxHttpTestUtils.getResponseAsString(uploadFileResponse));
        CloseableHttpResponse getResponse = BaSyxHttpTestUtils.executeGetOnURL(getSpecificPackageURL(new Base64UrlEncodedIdentifier(packageDescription.getPackageId()).getEncodedIdentifier()));
        assertEquals(200,getResponse.getCode());

        assertFileContentIsEqualToResponse(getResponse, aasxFile);

        deletePackageAndAssertResponseCode(packageDescription);

        asserPackageIsDeleted(packageDescription);

    }

    @Test
    public void testUpdateAASXFile() throws IOException, ParseException {
        File aasxFile = loadAASXFromResources(TEST_AASX_FILENAME);

        ArrayList<String> aasIds = getDummyAasIdList();

        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(post);
        BaSyxPackageDescription description = getBaSyxPackageDescriptionFromResponse(BaSyxHttpTestUtils.getResponseAsString(response));
        String packageId = description.getPackageId();
        String encodedPackageId = new Base64UrlEncodedIdentifier(packageId).getEncodedIdentifier();

        File updatedAASXFile = loadAASXFromResources("test2.aasx");
        updatePackageAndAssertResponse(encodedPackageId, aasIds, updatedAASXFile, client);

        assertPackagesUpdated(encodedPackageId, updatedAASXFile);
    }

    private void assertPackagesUpdated(String encodedPackageId, File updatedAASXFile) throws IOException {
        CloseableHttpResponse getResponse = BaSyxHttpTestUtils.executeGetOnURL(getSpecificPackageURL(encodedPackageId));

        File tempFile = File.createTempFile("downloaded", ".aasx");

        writeInputStreamToFile(getResponse,tempFile);
        assertTrue(FileUtils.contentEquals(updatedAASXFile, tempFile));
    }

    private void updatePackageAndAssertResponse(String encodedPackageId, ArrayList<String> aasIds, File updatedAASXFile, CloseableHttpClient client) throws IOException {
        HttpPut put = BaSyxHttpTestUtils.updatePutRequestWithFileForFileServer(getUploadURL(), encodedPackageId, aasIds, updatedAASXFile, new Base64UrlEncodedIdentifier("test2").getEncodedIdentifier());
        CloseableHttpResponse response2 = client.execute(put);

        assertEquals(204,response2.getCode());
    }

    private static String getPackageIdFromUploadResponseBody(CloseableHttpResponse response) throws IOException, ParseException {
        BaSyxPackageDescription description = getBaSyxPackageDescriptionFromResponse(BaSyxHttpTestUtils.getResponseAsString(response));
        String packageId = description.getPackageId();
        String encodedPackageId = new Base64UrlEncodedIdentifier(packageId).getEncodedIdentifier();
        return encodedPackageId;
    }

    private File loadAASXFromResources(String aasxFileName){
        return new File(getClass().getClassLoader().getResource(aasxFileName).getFile());
    }

    private String getUploadURL(){
        return ACCESS_URL + "/packages";
    }

    private String getSpecificPackageURL(String packageId){
        return ACCESS_URL + "/packages/" + packageId;
    }

    private CloseableHttpResponse uploadFile(CloseableHttpClient client, List<String> aasIds, File aasxFile) throws IOException {
        Base64UrlEncodedIdentifier fileName = new Base64UrlEncodedIdentifier("test");

        HttpPost post = BaSyxHttpTestUtils.createPostRequestWithFileForFileServer(getUploadURL(),aasIds,aasxFile,fileName.getEncodedIdentifier());

        CloseableHttpResponse response = client.execute(post);
        return response;
    }

    private static void writeInputStreamToFile(CloseableHttpResponse getResponse, File tempFile) throws IOException {
        InputStream responseBodyStream = getResponse.getEntity().getContent();
        try (OutputStream outStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = responseBodyStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }
        }
    }

    private static void assertFileContentIsEqualToResponse(CloseableHttpResponse getResponse, File aasxFile) throws IOException {
        File tempFile = File.createTempFile("downloaded", ".aasx");
        writeInputStreamToFile(getResponse, tempFile);
        assertTrue(FileUtils.contentEquals(aasxFile, tempFile));
    }

    private void deletePackageAndAssertResponseCode(BaSyxPackageDescription packageDescription) throws IOException {
        CloseableHttpResponse deleteResponse = BaSyxHttpTestUtils.executeDeleteOnURL(getSpecificPackageURL(new Base64UrlEncodedIdentifier(packageDescription.getPackageId()).getEncodedIdentifier()));
        assertEquals(204,deleteResponse.getCode());
    }

    private void asserPackageIsDeleted(BaSyxPackageDescription packageDescription) throws IOException {
        CloseableHttpResponse getResponseAfterDelete = BaSyxHttpTestUtils.executeGetOnURL(getSpecificPackageURL(new Base64UrlEncodedIdentifier(packageDescription.getPackageId()).getEncodedIdentifier()));
        assertEquals(404,getResponseAfterDelete.getCode());
    }

    private static BaSyxPackageDescription getBaSyxPackageDescriptionFromResponse(String responseBody) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        BaSyxPackageDescription packageDescription = mapper.readValue(responseBody, BaSyxPackageDescription.class);
        return packageDescription;
    }

    private static ArrayList<String> getDummyAasIdList() {
        ArrayList<String> aasIds = new ArrayList<>();
        aasIds.add(new Base64UrlEncodedIdentifier("testAasId").getEncodedIdentifier());
        return aasIds;
    }
}
