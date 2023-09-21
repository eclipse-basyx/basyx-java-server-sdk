package org.eclipse.digitaltwin.basyx.aasregistry.service.authorization.rbac;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleSet;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Validated
@RestController
@Tag(name = "Asset Administration Shell Registry RBAC", description = "RBAC API for the Asset Administration Shell Registry")
public class RbacApi {
    private final IRbacService service;

    public RbacApi(IRbacService service) {
        this.service = service;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/aas-descriptors-rbac"
    )
    public ResponseEntity<RbacRuleSet> putRBACRule() {
        return ResponseEntity.of(Optional.ofNullable(service.getRbacRuleSet()));
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/aas-descriptors-rbac",
            consumes = { "application/json" }
    )
    public void putRBACRule(
            @Parameter(name = "AASDescriptor", description = "Asset Administration Shell Descriptor object", required = true) @Valid @RequestBody RbacRule rbacRule
    ) {
        service.addRule(rbacRule);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/aas-descriptors-rbac",
            consumes = { "application/json" }
    )
    public void deleteRBACRule(
            @Parameter(name = "AASDescriptor", description = "Asset Administration Shell Descriptor object", required = true) @Valid @RequestBody RbacRule rbacRule
    ) {
        service.removeRule(rbacRule);
    }
}
