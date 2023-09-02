package org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization.rbac;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.eclipse.digitaltwin.basyx.authorization.rbac.IRbacStorage;
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
@Tag(name = "Submodel Registry RBAC", description = "RBAC API for the Submodel Registry")
public class RbacApi {
    private final IRbacStorage storage;

    public RbacApi(IRbacStorage storage) {
        this.storage = storage;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/submodel-descriptors-rbac"
    )
    public ResponseEntity<RbacRuleSet> putRBACRule() {
        return ResponseEntity.of(Optional.ofNullable(storage.getRbacRuleSet()));
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/submodel-descriptors-rbac",
            consumes = { "application/json" }
    )
    public void putRBACRule(
            @Parameter(name = "SubmodelDescriptor", description = "Submodel Descriptor object", required = true) @Valid @RequestBody RbacRule rbacRule
    ) {
        storage.addRule(rbacRule);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/submodel-descriptors-rbac",
            consumes = { "application/json" }
    )
    public void deleteRBACRule(
            @Parameter(name = "SubmodelDescriptor", description = "Submodel Descriptor object", required = true) @Valid @RequestBody RbacRule rbacRule
    ) {
        storage.removeRule(rbacRule);
    }
}
