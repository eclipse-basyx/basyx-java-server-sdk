package org.eclipse.digitaltwin.basyx.submodelregistry.service.api;

import org.eclipse.digitaltwin.basyx.submodelregistry.model.GetSubmodelDescriptorsResult;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.Result;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

/**
 * A delegate to be called by the {@link SubmodelDescriptorsApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T16:44:56.230082400+02:00[Europe/Berlin]")
public interface SubmodelDescriptorsApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * DELETE /submodel-descriptors : Deletes all Submodel Descriptors
     *
     * @return No content (status code 204)
     * @see SubmodelDescriptorsApi#deleteAllSubmodelDescriptors
     */
    default ResponseEntity<Void> deleteAllSubmodelDescriptors() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * DELETE /submodel-descriptors/{submodelIdentifier} : Deletes a Submodel Descriptor, i.e. de-registers a submodel
     *
     * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
     * @return Submodel Descriptor deleted successfully (status code 204)
     *         or Bad Request, e.g. the request parameters of the format of the request body is wrong. (status code 400)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Internal Server Error (status code 500)
     * @see SubmodelDescriptorsApi#deleteSubmodelDescriptorById
     */
    default ResponseEntity<Void> deleteSubmodelDescriptorById( String  submodelIdentifier) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /submodel-descriptors : Returns all Submodel Descriptors
     *
     * @param limit The maximum number of elements in the response array (optional)
     * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
     * @return Requested Submodel Descriptors (status code 200)
     *         or Bad Request, e.g. the request parameters of the format of the request body is wrong. (status code 400)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Internal Server Error (status code 500)
     *         or Default error handling for unmentioned status codes (status code 200)
     * @see SubmodelDescriptorsApi#getAllSubmodelDescriptors
     */
    default ResponseEntity<GetSubmodelDescriptorsResult> getAllSubmodelDescriptors( Integer  limit,
         String  cursor) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "null";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /submodel-descriptors/{submodelIdentifier} : Returns a specific Submodel Descriptor
     *
     * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
     * @return Requested Submodel Descriptor (status code 200)
     *         or Bad Request, e.g. the request parameters of the format of the request body is wrong. (status code 400)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Internal Server Error (status code 500)
     *         or Default error handling for unmentioned status codes (status code 200)
     * @see SubmodelDescriptorsApi#getSubmodelDescriptorById
     */
    default ResponseEntity<SubmodelDescriptor> getSubmodelDescriptorById( String  submodelIdentifier) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "null";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /submodel-descriptors : Creates a new Submodel Descriptor, i.e. registers a submodel
     *
     * @param submodelDescriptor Submodel Descriptor object (required)
     * @return Submodel Descriptor created successfully (status code 201)
     *         or Bad Request, e.g. the request parameters of the format of the request body is wrong. (status code 400)
     *         or Forbidden (status code 403)
     *         or Conflict, a resource which shall be created exists already. Might be thrown if a Submodel or SubmodelElement with the same ShortId is contained in a POST request. (status code 409)
     *         or Default error handling for unmentioned status codes (status code 200)
     * @see SubmodelDescriptorsApi#postSubmodelDescriptor
     */
    default ResponseEntity<SubmodelDescriptor> postSubmodelDescriptor(SubmodelDescriptor submodelDescriptor) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "null";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PUT /submodel-descriptors/{submodelIdentifier} : Updates an existing Submodel Descriptor
     *
     * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
     * @param submodelDescriptor Submodel Descriptor object (required)
     * @return Submodel Descriptor updated successfully (status code 204)
     *         or Bad Request, e.g. the request parameters of the format of the request body is wrong. (status code 400)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Internal Server Error (status code 500)
     *         or Default error handling for unmentioned status codes (status code 200)
     * @see SubmodelDescriptorsApi#putSubmodelDescriptorById
     */
    default ResponseEntity<Void> putSubmodelDescriptorById( String  submodelIdentifier,
        SubmodelDescriptor submodelDescriptor) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
