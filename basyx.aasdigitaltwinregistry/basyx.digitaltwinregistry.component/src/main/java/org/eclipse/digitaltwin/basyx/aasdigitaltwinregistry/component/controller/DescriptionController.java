package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
public class DescriptionController {

    Map<String, List<String>> response;

    @GetMapping(value = "/description", produces = "application/json")
    public ResponseEntity<Map<String, List<String>>> getDescription() {
        Map<String, List<String>> response = new HashMap<>();
        response.put("profiles", List.of(
                "https://admin-shell.io/aas/API/3/0/AssetAdministrationShellRegistryServiceSpecification/SSP-001",
                "https://admin-shell.io/aas/API/3/0/AssetAdministrationShellRegistryServiceSpecification/SSP-002",
                "https://admin-shell.io/aas/API/3/0/DiscoveryServiceSpecification/SSP-001"
        ));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
