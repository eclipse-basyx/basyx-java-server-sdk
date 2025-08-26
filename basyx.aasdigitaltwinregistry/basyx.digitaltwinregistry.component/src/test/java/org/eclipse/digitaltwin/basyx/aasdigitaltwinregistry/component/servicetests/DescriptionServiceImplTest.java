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

 package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.servicetests;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;

 import java.util.List;
 import java.util.Map;

 import lombok.extern.slf4j.Slf4j;
 import org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.service.Impl.DescriptionServiceImpl;
 import org.junit.Before;
 import org.junit.Test;

 @Slf4j
 public class DescriptionServiceImplTest {

     private DescriptionServiceImpl descriptionService;

     @Before
     public void setUp() {
         descriptionService = new DescriptionServiceImpl();
     }

     @Test
     public void testGetDescriptionNotNull() {
         log.info("Started unit test - testGetDescriptionNotNull");
         Map<String, List<String>> result = descriptionService.getDescription();
         assertTrue(result.containsKey("profiles"));
         assertEquals(3, result.get("profiles").size());
         log.info("Successfully conducted unit test");
     }

     @Test
     public void testGetDescriptionProfilesContent() {
         log.info("Started unit test - testGetDescriptionProfilesContent");
         Map<String, List<String>> result = descriptionService.getDescription();
         List<String> profiles = result.get("profiles");
         assertTrue(profiles.contains("https://admin-shell.io/aas/API/3/0/AssetAdministrationShellRegistryServiceSpecification/SSP-001"));
         assertTrue(profiles.contains("https://admin-shell.io/aas/API/3/0/AssetAdministrationShellRegistryServiceSpecification/SSP-002"));
         assertTrue(profiles.contains("https://admin-shell.io/aas/API/3/0/DiscoveryServiceSpecification/SSP-001"));
         log.info("Successfully conducted unit test");
     }

     @Test
     public void testGetDescriptionOnlyProfilesKey() {
         log.info("Started unit test - testGetDescriptionOnlyProfilesKey");
         Map<String, List<String>> result = descriptionService.getDescription();
         assertEquals(1, result.size());
         assertTrue(result.containsKey("profiles"));
         log.info("Successfully conducted unit test");
     }
 }
