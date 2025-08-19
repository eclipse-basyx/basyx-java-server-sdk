package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.service.DescriptionService;
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

    private final DescriptionService descriptionService;

    @GetMapping(value = "/description", produces = "application/json")
    public ResponseEntity<Map<String, List<String>>> getDescription() {
        Map<String, List<String>> response = descriptionService.getDescription();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
