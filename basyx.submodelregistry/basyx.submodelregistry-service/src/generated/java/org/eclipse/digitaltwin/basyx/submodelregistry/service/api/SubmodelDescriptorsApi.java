/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.eclipse.digitaltwin.basyx.submodelregistry.service.api;

import org.eclipse.digitaltwin.basyx.submodelregistry.model.GetSubmodelDescriptorsResult;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.Result;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T16:44:56.230082400+02:00[Europe/Berlin]")
@Validated
@Controller
@Tag(name = "Submodel Registry", description = "the Submodel Registry API")
public interface SubmodelDescriptorsApi {

    default SubmodelDescriptorsApiDelegate getDelegate() {
        return new SubmodelDescriptorsApiDelegate() {};
    }

    /**
     * DELETE /submodel-descriptors : Deletes all Submodel Descriptors
     *
     * @return No content (status code 204)
     */
    @Operation(
        operationId = "deleteAllSubmodelDescriptors",
        summary = "Deletes all Submodel Descriptors",
        tags = { "Submodel Registry" },
        responses = {
            @ApiResponse(responseCode = "204", description = "No content")
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/submodel-descriptors"
    )
    default ResponseEntity<Void> deleteAllSubmodelDescriptors(
        
    ) {
        return getDelegate().deleteAllSubmodelDescriptors();
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
     */
    @Operation(
        operationId = "deleteSubmodelDescriptorById",
        summary = "Deletes a Submodel Descriptor, i.e. de-registers a submodel",
        tags = { "Submodel Registry" },
        responses = {
            @ApiResponse(responseCode = "204", description = "Submodel Descriptor deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/submodel-descriptors/{submodelIdentifier}",
        produces = { "application/json" }
    )
    default ResponseEntity<Void> deleteSubmodelDescriptorById(
        @Parameter(name = "submodelIdentifier", description = "The Submodel’s unique id (UTF8-BASE64-URL-encoded)", required = true, in = ParameterIn.PATH) @PathVariable("submodelIdentifier") byte[] submodelIdentifier
    ) {
        String submodelIdentifierFromBase64EncodedParam = submodelIdentifier == null ? null : new String(java.util.Base64.getUrlDecoder().decode(submodelIdentifier), java.nio.charset.StandardCharsets.UTF_8);
        return getDelegate().deleteSubmodelDescriptorById(submodelIdentifierFromBase64EncodedParam);
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
     */
    @Operation(
        operationId = "getAllSubmodelDescriptors",
        summary = "Returns all Submodel Descriptors",
        tags = { "Submodel Registry" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Requested Submodel Descriptors", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = GetSubmodelDescriptorsResult.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            }),
            @ApiResponse(responseCode = "default", description = "Default error handling for unmentioned status codes", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/submodel-descriptors",
        produces = { "application/json" }
    )
    default ResponseEntity<GetSubmodelDescriptorsResult> getAllSubmodelDescriptors(
        @Min(1) @Parameter(name = "limit", description = "The maximum number of elements in the response array", in = ParameterIn.QUERY) @Valid @RequestParam(value = "limit", required = false) Integer limit,
        @Parameter(name = "cursor", description = "A server-generated identifier retrieved from pagingMetadata that specifies from which position the result listing should continue", in = ParameterIn.QUERY) @Valid @RequestParam(value = "cursor", required = false) String cursor
    ) {
        return getDelegate().getAllSubmodelDescriptors(limit, cursor);
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
     */
    @Operation(
        operationId = "getSubmodelDescriptorById",
        summary = "Returns a specific Submodel Descriptor",
        tags = { "Submodel Registry" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Requested Submodel Descriptor", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = SubmodelDescriptor.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            }),
            @ApiResponse(responseCode = "default", description = "Default error handling for unmentioned status codes", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/submodel-descriptors/{submodelIdentifier}",
        produces = { "application/json" }
    )
    default ResponseEntity<SubmodelDescriptor> getSubmodelDescriptorById(
        @Parameter(name = "submodelIdentifier", description = "The Submodel’s unique id (UTF8-BASE64-URL-encoded)", required = true, in = ParameterIn.PATH) @PathVariable("submodelIdentifier") byte[] submodelIdentifier
    ) {
        String submodelIdentifierFromBase64EncodedParam = submodelIdentifier == null ? null : new String(java.util.Base64.getUrlDecoder().decode(submodelIdentifier), java.nio.charset.StandardCharsets.UTF_8);
        return getDelegate().getSubmodelDescriptorById(submodelIdentifierFromBase64EncodedParam);
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
     */
    @Operation(
        operationId = "postSubmodelDescriptor",
        summary = "Creates a new Submodel Descriptor, i.e. registers a submodel",
        tags = { "Submodel Registry" },
        responses = {
            @ApiResponse(responseCode = "201", description = "Submodel Descriptor created successfully", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = SubmodelDescriptor.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            }),
            @ApiResponse(responseCode = "409", description = "Conflict, a resource which shall be created exists already. Might be thrown if a Submodel or SubmodelElement with the same ShortId is contained in a POST request.", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            }),
            @ApiResponse(responseCode = "default", description = "Default error handling for unmentioned status codes", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/submodel-descriptors",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<SubmodelDescriptor> postSubmodelDescriptor(
        @Parameter(name = "SubmodelDescriptor", description = "Submodel Descriptor object", required = true) @Valid @RequestBody SubmodelDescriptor submodelDescriptor
    ) {
        return getDelegate().postSubmodelDescriptor(submodelDescriptor);
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
     */
    @Operation(
        operationId = "putSubmodelDescriptorById",
        summary = "Updates an existing Submodel Descriptor",
        tags = { "Submodel Registry" },
        responses = {
            @ApiResponse(responseCode = "204", description = "Submodel Descriptor updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request, e.g. the request parameters of the format of the request body is wrong.", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            }),
            @ApiResponse(responseCode = "default", description = "Default error handling for unmentioned status codes", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/submodel-descriptors/{submodelIdentifier}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<Void> putSubmodelDescriptorById(
        @Parameter(name = "submodelIdentifier", description = "The Submodel’s unique id (UTF8-BASE64-URL-encoded)", required = true, in = ParameterIn.PATH) @PathVariable("submodelIdentifier") byte[] submodelIdentifier,
        @Parameter(name = "SubmodelDescriptor", description = "Submodel Descriptor object", required = true) @Valid @RequestBody SubmodelDescriptor submodelDescriptor
    ) {
        String submodelIdentifierFromBase64EncodedParam = submodelIdentifier == null ? null : new String(java.util.Base64.getUrlDecoder().decode(submodelIdentifier), java.nio.charset.StandardCharsets.UTF_8);
        return getDelegate().putSubmodelDescriptorById(submodelIdentifierFromBase64EncodedParam, submodelDescriptor);
     }

}