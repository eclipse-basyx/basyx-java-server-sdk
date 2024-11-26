package org.eclipse.digitaltwin.basyx.aasregistry.service.api;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.model.GetAssetAdministrationShellDescriptorsResult;
import org.eclipse.digitaltwin.basyx.aasregistry.model.GetSubmodelDescriptorsResult;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Result;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
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
 * A delegate to be called by the {@link ShellDescriptorsApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:35.975734200+01:00[Europe/Berlin]")
public interface ShellDescriptorsApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * DELETE /shell-descriptors : Deletes all Asset Administration Shell Descriptors
     *
     * @return No content (status code 204)
     * @see ShellDescriptorsApi#deleteAllShellDescriptors
     */
    default ResponseEntity<Void> deleteAllShellDescriptors() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * DELETE /shell-descriptors/{aasIdentifier} : Deletes an Asset Administration Shell Descriptor, i.e. de-registers an AAS
     *
     * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
     * @return Asset Administration Shell Descriptor deleted successfully (status code 204)
     *         or Bad Request, e.g. the request parameters of the format of the request body is wrong. (status code 400)
     *         or Not Found (status code 404)
     *         or Internal Server Error (status code 500)
     *         or Default error handling for unmentioned status codes (status code 200)
     * @see ShellDescriptorsApi#deleteAssetAdministrationShellDescriptorById
     */
    default ResponseEntity<Void> deleteAssetAdministrationShellDescriptorById( String  aasIdentifier) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * DELETE /shell-descriptors/{aasIdentifier}/submodel-descriptors/{submodelIdentifier} : Deletes a Submodel Descriptor, i.e. de-registers a submodel
     *
     * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
     * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
     * @return Submodel Descriptor deleted successfully (status code 204)
     *         or Bad Request, e.g. the request parameters of the format of the request body is wrong. (status code 400)
     *         or Not Found (status code 404)
     *         or Internal Server Error (status code 500)
     *         or Default error handling for unmentioned status codes (status code 200)
     * @see ShellDescriptorsApi#deleteSubmodelDescriptorByIdThroughSuperpath
     */
    default ResponseEntity<Void> deleteSubmodelDescriptorByIdThroughSuperpath( String  aasIdentifier,
         String  submodelIdentifier) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /shell-descriptors : Returns all Asset Administration Shell Descriptors
     *
     * @param limit The maximum number of elements in the response array (optional)
     * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
     * @param assetKind The Asset&#39;s kind (Instance or Type) (optional)
     * @param assetType The Asset&#39;s type (UTF8-BASE64-URL-encoded) (optional)
     * @return Requested Asset Administration Shell Descriptors (status code 200)
     *         or Bad Request, e.g. the request parameters of the format of the request body is wrong. (status code 400)
     *         or Forbidden (status code 403)
     *         or Internal Server Error (status code 500)
     *         or Default error handling for unmentioned status codes (status code 200)
     * @see ShellDescriptorsApi#getAllAssetAdministrationShellDescriptors
     */
    default ResponseEntity<GetAssetAdministrationShellDescriptorsResult> getAllAssetAdministrationShellDescriptors( Integer  limit,
         String  cursor,
         AssetKind  assetKind,
         String  assetType) {
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
     * GET /shell-descriptors/{aasIdentifier}/submodel-descriptors : Returns all Submodel Descriptors
     *
     * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
     * @param limit The maximum number of elements in the response array (optional)
     * @param cursor A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue (optional)
     * @return Requested Submodel Descriptors (status code 200)
     *         or Bad Request, e.g. the request parameters of the format of the request body is wrong. (status code 400)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Internal Server Error (status code 500)
     *         or Default error handling for unmentioned status codes (status code 200)
     * @see ShellDescriptorsApi#getAllSubmodelDescriptorsThroughSuperpath
     */
    default ResponseEntity<GetSubmodelDescriptorsResult> getAllSubmodelDescriptorsThroughSuperpath( String  aasIdentifier,
         Integer  limit,
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
     * GET /shell-descriptors/{aasIdentifier} : Returns a specific Asset Administration Shell Descriptor
     *
     * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
     * @return Requested Asset Administration Shell Descriptor (status code 200)
     *         or Bad Request, e.g. the request parameters of the format of the request body is wrong. (status code 400)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Internal Server Error (status code 500)
     *         or Default error handling for unmentioned status codes (status code 200)
     * @see ShellDescriptorsApi#getAssetAdministrationShellDescriptorById
     */
    default ResponseEntity<AssetAdministrationShellDescriptor> getAssetAdministrationShellDescriptorById( String  aasIdentifier) {
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
     * GET /shell-descriptors/{aasIdentifier}/submodel-descriptors/{submodelIdentifier} : Returns a specific Submodel Descriptor
     *
     * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
     * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
     * @return Requested Submodel Descriptor (status code 200)
     *         or Bad Request, e.g. the request parameters of the format of the request body is wrong. (status code 400)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Internal Server Error (status code 500)
     *         or Default error handling for unmentioned status codes (status code 200)
     * @see ShellDescriptorsApi#getSubmodelDescriptorByIdThroughSuperpath
     */
    default ResponseEntity<SubmodelDescriptor> getSubmodelDescriptorByIdThroughSuperpath( String  aasIdentifier,
         String  submodelIdentifier) {
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
     * POST /shell-descriptors : Creates a new Asset Administration Shell Descriptor, i.e. registers an AAS
     *
     * @param assetAdministrationShellDescriptor Asset Administration Shell Descriptor object (required)
     * @return Asset Administration Shell Descriptor created successfully (status code 201)
     *         or Bad Request, e.g. the request parameters of the format of the request body is wrong. (status code 400)
     *         or Forbidden (status code 403)
     *         or Conflict, a resource which shall be created exists already. Might be thrown if a Submodel or SubmodelElement with the same ShortId is contained in a POST request. (status code 409)
     *         or Internal Server Error (status code 500)
     *         or Default error handling for unmentioned status codes (status code 200)
     * @see ShellDescriptorsApi#postAssetAdministrationShellDescriptor
     */
    default ResponseEntity<AssetAdministrationShellDescriptor> postAssetAdministrationShellDescriptor(AssetAdministrationShellDescriptor assetAdministrationShellDescriptor) {
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
     * POST /shell-descriptors/{aasIdentifier}/submodel-descriptors : Creates a new Submodel Descriptor, i.e. registers a submodel
     *
     * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
     * @param submodelDescriptor Submodel Descriptor object (required)
     * @return Submodel Descriptor created successfully (status code 201)
     *         or Bad Request, e.g. the request parameters of the format of the request body is wrong. (status code 400)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Conflict, a resource which shall be created exists already. Might be thrown if a Submodel or SubmodelElement with the same ShortId is contained in a POST request. (status code 409)
     *         or Internal Server Error (status code 500)
     *         or Default error handling for unmentioned status codes (status code 200)
     * @see ShellDescriptorsApi#postSubmodelDescriptorThroughSuperpath
     */
    default ResponseEntity<SubmodelDescriptor> postSubmodelDescriptorThroughSuperpath( String  aasIdentifier,
        SubmodelDescriptor submodelDescriptor) {
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
     * PUT /shell-descriptors/{aasIdentifier} : Updates an existing Asset Administration Shell Descriptor
     *
     * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
     * @param assetAdministrationShellDescriptor Asset Administration Shell Descriptor object (required)
     * @return Asset Administration Shell Descriptor updated successfully (status code 204)
     *         or Bad Request, e.g. the request parameters of the format of the request body is wrong. (status code 400)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Internal Server Error (status code 500)
     *         or Default error handling for unmentioned status codes (status code 200)
     * @see ShellDescriptorsApi#putAssetAdministrationShellDescriptorById
     */
    default ResponseEntity<Void> putAssetAdministrationShellDescriptorById( String  aasIdentifier,
        AssetAdministrationShellDescriptor assetAdministrationShellDescriptor) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PUT /shell-descriptors/{aasIdentifier}/submodel-descriptors/{submodelIdentifier} : Updates an existing Submodel Descriptor
     *
     * @param aasIdentifier The Asset Administration Shell’s unique id (UTF8-BASE64-URL-encoded) (required)
     * @param submodelIdentifier The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
     * @param submodelDescriptor Submodel Descriptor object (required)
     * @return Submodel Descriptor updated successfully (status code 204)
     *         or Bad Request, e.g. the request parameters of the format of the request body is wrong. (status code 400)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Internal Server Error (status code 500)
     *         or Default error handling for unmentioned status codes (status code 200)
     * @see ShellDescriptorsApi#putSubmodelDescriptorByIdThroughSuperpath
     */
    default ResponseEntity<Void> putSubmodelDescriptorByIdThroughSuperpath( String  aasIdentifier,
         String  submodelIdentifier,
        SubmodelDescriptor submodelDescriptor) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
