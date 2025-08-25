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

package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.service.Impl.DescriptionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DescriptionServiceImplTest {

    private DescriptionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DescriptionServiceImpl();
    }

    @Test
    void testGetDescriptionNotNull() {
      log.info("Started unit test - testGetDescriptionNotNull()");
        Map<String, List<String>> description = service.getDescription();
        assertNotNull(description, "Description map should not be null");
        log.info("Successfully conducted unit test");
    }

    @Test
    void testGetDescriptionContainsProfilesKey() {
        log.info("Started unit test - testGetDescriptionContainsProfilesKey()");
        Map<String, List<String>> description = service.getDescription();
        assertTrue(description.containsKey("profiles"), "Description map should contain 'profiles' key");
        log.info("Successfully conducted unit test");
    }

    @Test
    void testProfilesListSize() {
        log.info("Started unit test - testProfilesListSize()");
        Map<String, List<String>> description = service.getDescription();
        List<String> profiles = description.get("profiles");
        assertEquals(3, profiles.size(), "Profiles list should contain exactly 3 URLs");
        log.info("Successfully conducted unit test");
    }

    @Test
    void testProfilesContent() {
        log.info("Started unit test - testProfilesContent()");
        Map<String, List<String>> description = service.getDescription();
        List<String> profiles = description.get("profiles");
        assertTrue(profiles.contains("https://admin-shell.io/aas/API/3/0/AssetAdministrationShellRegistryServiceSpecification/SSP-001"));
        assertTrue(profiles.contains("https://admin-shell.io/aas/API/3/0/AssetAdministrationShellRegistryServiceSpecification/SSP-002"));
        assertTrue(profiles.contains("https://admin-shell.io/aas/API/3/0/DiscoveryServiceSpecification/SSP-001"));
        log.info("Successfully conducted unit test ");
    }
}
