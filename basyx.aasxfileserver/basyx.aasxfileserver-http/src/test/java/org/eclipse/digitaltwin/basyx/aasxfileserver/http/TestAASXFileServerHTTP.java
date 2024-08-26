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

import org.eclipse.digitaltwin.aas4j.v3.model.PackageDescription;
import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServer;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.assertTrue;

@SpringBootTest(classes = DummyAASXFileServer.class)
@AutoConfigureMockMvc
public class TestAASXFileServerHTTP extends AbstractTestNGSpringContextTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private AASXFileServer aasxFileServer;

    @BeforeClass
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        this.aasxFileServer = (AASXFileServer) webApplicationContext.getBean("getAASXFileServer");
    }

    @AfterMethod
    public void cleanUp() {
        try {
            this.aasxFileServer.getAllAASXPackageIds("", new PaginationInfo(0, "")).getResult().stream().forEach(p -> this.aasxFileServer.deleteAASXByPackageId(p.getPackageId()));
        }
        catch (Exception ignored) {
        }
    }

    @Test
    public void testDeleteNonExistingPackage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/packages/{packageId}", new Base64UrlEncodedIdentifier("nonExistingPackageId").getEncodedIdentifier()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetNonExistingPackage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/packages/{packageId}", new Base64UrlEncodedIdentifier("nonExistingPackageId").getEncodedIdentifier()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPostAASXPackageWithoutFile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/packages")
                    .param("aasIds", new Base64UrlEncodedIdentifier("aasId1").getEncodedIdentifier())
                    .param("fileName", new Base64UrlEncodedIdentifier("testFileName").getEncodedIdentifier()))
                .andExpect(status().isBadRequest());
        assertTrue(isPackageNotPresent("aasId1"));
    }

    @Test
    public void testPostAASXPackageWithoutParams() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .multipart("/packages")
                .file(new MockMultipartFile("file", "test.aasx", "application/asset-administration-shell-package+xml", fileToByteArray(loadAASXFromResources("test.aasx")))))
                .andExpect(status().isBadRequest());
        assertTrue(isPackageNotPresent(""));
    }

    @Test
    public void testPutNonExistingPackage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .multipart("/packages/{packageId}", new Base64UrlEncodedIdentifier("nonExistingPackageId").getEncodedIdentifier())
                .file(new MockMultipartFile("file", "", MediaType.APPLICATION_JSON_VALUE, "".getBytes()))
                .param("aasIds", new Base64UrlEncodedIdentifier("aasId1").getEncodedIdentifier())
                .param("fileName", new Base64UrlEncodedIdentifier("testFileName").getEncodedIdentifier())
                .with(request -> {request.setMethod("PUT"); return request;}))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPostAASXPackage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .multipart("/packages")
                .file(new MockMultipartFile("file", "test.aasx", "application/asset-administration-shell-package+xml", fileToByteArray(loadAASXFromResources("test.aasx"))))
                .param("aasIds", new Base64UrlEncodedIdentifier("aasId1").getEncodedIdentifier())
                .param("fileName", new Base64UrlEncodedIdentifier("testFileName").getEncodedIdentifier()))
        .andExpect(status().isCreated());

        assertTrue(isPackagePresentOneTime("aasId1"));
    }


    @Test
    public void testPutAASXPackage() throws Exception {
        PackageDescription packageDescription = createAASXPackageOnServer();

        assertTrue(isPackageNotPresent("aasId1"));

        mockMvc.perform(MockMvcRequestBuilders
                .multipart("/packages/{packageId}",new Base64UrlEncodedIdentifier(packageDescription.getPackageId()).getEncodedIdentifier())
                .file(new MockMultipartFile("file", "", MediaType.APPLICATION_JSON_VALUE, fileToByteArray(loadAASXFromResources("test2.aasx"))))
                .param("aasIds", new Base64UrlEncodedIdentifier("aasId1").getEncodedIdentifier())
                .param("fileName", new Base64UrlEncodedIdentifier("testFileName").getEncodedIdentifier())
                .with(request -> {request.setMethod("PUT"); return request;}))
                .andExpect(status().isNoContent());

        assertTrue(isPackagePresentOneTime("aasId1"));
    }

    @Test
    public void testDeletePackage() throws Exception {
        PackageDescription packageDescription = createAASXPackageOnServer();

        assertTrue(isPackagePresentOneTime(""));

        try {
            mockMvc.perform(MockMvcRequestBuilders
                    .delete("/packages/{packageId}", new Base64UrlEncodedIdentifier(packageDescription.getPackageId()).getEncodedIdentifier()))
                    .andExpect(status().isNoContent());
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(isPackageNotPresent(""));
    }

    @Test
    public void testGetPackage() throws Exception {
        PackageDescription packageDescription = createAASXPackageOnServer();

        assertTrue(isPackagePresentOneTime(""));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/packages/{packageId}", new Base64UrlEncodedIdentifier(packageDescription.getPackageId()).getEncodedIdentifier()))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllPackages() throws Exception {
        createAASXPackageOnServer();
        createAASXPackageOnServer();

        mockMvc.perform(MockMvcRequestBuilders
                .get("/packages"))
                .andExpect(status().isOk());
    }

    private boolean isPackageNotPresent(String aasId) {
        return this.aasxFileServer.getAllAASXPackageIds(aasId, new PaginationInfo(0, "")).getResult().size() == 0;
    }

    private boolean isPackagePresentOneTime(String aasId) {
        return this.aasxFileServer.getAllAASXPackageIds(aasId, new PaginationInfo(0, "")).getResult().size() == 1;
    }

    private PackageDescription createAASXPackageOnServer() throws Exception {
        return this.aasxFileServer.createAASXPackage(new ArrayList<>(),fileToInputStream(loadAASXFromResources("test.aasx")), "test.aasx");
    }
    private File loadAASXFromResources(String aasxFileName){
        return new File(getClass().getClassLoader().getResource(aasxFileName).getFile());
    }

    private InputStream fileToInputStream(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    private byte[] fileToByteArray(File file){
        return  file.toPath().toAbsolutePath().toString().getBytes();
    }
}