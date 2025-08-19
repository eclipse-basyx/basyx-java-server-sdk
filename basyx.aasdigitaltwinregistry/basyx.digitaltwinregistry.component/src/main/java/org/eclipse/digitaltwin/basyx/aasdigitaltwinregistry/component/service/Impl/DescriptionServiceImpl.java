package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.service.DescriptionService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Component
@RequiredArgsConstructor
public class DescriptionServiceImpl implements DescriptionService {

    public Map<String, List<String>> getDescription() {
        Map<String, List<String>> response = new HashMap<>();
        response.put("profiles", List.of(
                "https://admin-shell.io/aas/API/3/0/AssetAdministrationShellRegistryServiceSpecification/SSP-001",
                "https://admin-shell.io/aas/API/3/0/AssetAdministrationShellRegistryServiceSpecification/SSP-002",
                "https://admin-shell.io/aas/API/3/0/DiscoveryServiceSpecification/SSP-001"
        ));
        return response;
    }
}
