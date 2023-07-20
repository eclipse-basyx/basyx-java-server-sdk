package org.eclipse.digitaltwin.basyx.submodelregistry.service.api;

import org.eclipse.digitaltwin.basyx.submodelregistry.model.GetSubmodelDescriptorsResult;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.Result;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-18T15:01:47.322351200+02:00[Europe/Berlin]")
@Controller
@RequestMapping("${openapi.dotAASPart2HTTPRESTSubmodelRegistryServiceSpecification.base-path:}")
public class SubmodelDescriptorsApiController implements SubmodelDescriptorsApi {

    private final SubmodelDescriptorsApiDelegate delegate;

    public SubmodelDescriptorsApiController(@Autowired(required = false) SubmodelDescriptorsApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new SubmodelDescriptorsApiDelegate() {});
    }

    @Override
    public SubmodelDescriptorsApiDelegate getDelegate() {
        return delegate;
    }

}
