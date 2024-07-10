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

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestAASXFileServerHTTP {

    private static ConfigurableApplicationContext appContext;
    private static final String ACCESS_URL = "http://localhost:4050";

    @BeforeClass
    public static void setUpClass(){
        appContext = new SpringApplication(DummyAASXFileServer.class).run(new String[]{});
    }

    @Test
    public void testAASXFileLifecycle() throws IOException, NoSuchAlgorithmException, ParseException {

        File aasxFile = loadAASXFromResources("test.aasx");

        ArrayList<String> aasIds = new ArrayList<>();
        aasIds.add(new Base64UrlEncodedIdentifier("testAasId").getEncodedIdentifier());

        Base64UrlEncodedIdentifier fileName = new Base64UrlEncodedIdentifier("test");

        HttpPost post = BaSyxHttpTestUtils.createPostRequestWithFileForFileServer(getUploadURL(),aasIds,aasxFile,fileName.getEncodedIdentifier());
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(post);

        assertEquals(201,response.getCode());
        BaSyxPackageDescription packageDescription = getBaSyxPackageDescriptionFromResponse(BaSyxHttpTestUtils.getResponseAsString(response));

        CloseableHttpResponse getResponse = BaSyxHttpTestUtils.executeGetOnURL(getSpecificPackageURL(new Base64UrlEncodedIdentifier(packageDescription.getPackageId()).getEncodedIdentifier()));
        assertEquals(200,getResponse.getCode());
        InputStream responseBodyStream = getResponse.getEntity().getContent();
        // Create a temporary file
        File tempFile = File.createTempFile("downloaded", ".aasx");

        // Write the InputStream to the file
        try (OutputStream outStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = responseBodyStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }
        }

        assertTrue(FileUtils.contentEquals(aasxFile, tempFile));

        CloseableHttpResponse deleteResponse = BaSyxHttpTestUtils.executeDeleteOnURL(getSpecificPackageURL(new Base64UrlEncodedIdentifier(packageDescription.getPackageId()).getEncodedIdentifier()));

        assertEquals(204,deleteResponse.getCode());

        CloseableHttpResponse getResponseAfterDelete = BaSyxHttpTestUtils.executeGetOnURL(getSpecificPackageURL(new Base64UrlEncodedIdentifier(packageDescription.getPackageId()).getEncodedIdentifier()));

        assertEquals(404,getResponseAfterDelete.getCode());

    }

    private static BaSyxPackageDescription getBaSyxPackageDescriptionFromResponse(String responseBody) throws IOException {
        // Create an ObjectMapper instance
        ObjectMapper mapper = new ObjectMapper();

        // Parse the response body to a BaSyxPackageDescription object
        BaSyxPackageDescription packageDescription = mapper.readValue(responseBody, BaSyxPackageDescription.class);
        return packageDescription;
    }

    @Test
    public void testUpdateAASXFile() throws IOException, ParseException {
        File aasxFile = loadAASXFromResources("test.aasx");
        ArrayList<String> aasIds = new ArrayList<>();
        aasIds.add(new Base64UrlEncodedIdentifier("testAasId").getEncodedIdentifier());
        HttpPost post = BaSyxHttpTestUtils.createPostRequestWithFileForFileServer(getUploadURL(),aasIds,aasxFile, new Base64UrlEncodedIdentifier("test1").getEncodedIdentifier());
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(post);
        BaSyxPackageDescription description = getBaSyxPackageDescriptionFromResponse(BaSyxHttpTestUtils.getResponseAsString(response));
        String packageId = description.getPackageId();
        String encodedPackageId = new Base64UrlEncodedIdentifier(packageId).getEncodedIdentifier();

        File updatedAASXFile = loadAASXFromResources("test2.aasx");
        HttpPut put = BaSyxHttpTestUtils.updatePutRequestWithFileForFileServer(getUploadURL(), encodedPackageId, aasIds, updatedAASXFile, new Base64UrlEncodedIdentifier("test2").getEncodedIdentifier());
        CloseableHttpResponse response2 = client.execute(put);

        assertEquals(204,response2.getCode());
        CloseableHttpResponse getResponse = BaSyxHttpTestUtils.executeGetOnURL(getSpecificPackageURL(encodedPackageId));

        InputStream responseBodyStream = getResponse.getEntity().getContent();
        File tempFile = File.createTempFile("downloaded", ".aasx");

        // Write the InputStream to the file
        try (OutputStream outStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = responseBodyStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }
        }
        assertTrue(FileUtils.contentEquals(updatedAASXFile, tempFile));
    }

    public File loadAASXFromResources(String aasxFileName){
        return new File(getClass().getClassLoader().getResource(aasxFileName).getFile());
    }

    private String getUploadURL(){
        return ACCESS_URL + "/packages";
    }

    private String getSpecificPackageURL(String packageId){
        return ACCESS_URL + "/packages/" + packageId;
    }
}
