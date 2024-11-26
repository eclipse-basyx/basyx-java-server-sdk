package org.eclipse.digitaltwin.basyx.aasregistry.service.api;

import org.eclipse.digitaltwin.basyx.aasregistry.model.Result;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ServiceDescription;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

/**
 * A delegate to be called by the {@link DescriptionApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:35.975734200+01:00[Europe/Berlin]")
public interface DescriptionApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /description : Returns the self-describing information of a network resource (ServiceDescription)
     *
     * @return Requested Description (status code 200)
     *         or Forbidden (status code 403)
     *         or Default error handling for unmentioned status codes (status code 200)
     * @see DescriptionApi#getDescription
     */
    default ResponseEntity<ServiceDescription> getDescription() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"profiles\" : [ \"https://admin-shell.io/aas/API/3/0/AssetAdministrationShellServiceSpecification/SSP-001\", \"https://admin-shell.io/aas/API/3/0/AssetAdministrationShellServiceSpecification/SSP-001\" ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
