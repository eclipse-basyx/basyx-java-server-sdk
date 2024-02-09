package org.eclipse.digitaltwin.basyx.http.description;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.digitaltwin.aas4j.v3.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Registry and Discovery Interface", description = "the Registry and Discovery Interface API")
public class DescriptionController {

  private final SortedSet<Profile> profiles;

  @Autowired
  public DescriptionController(List<ProfileDeclaration> declarations) {
    profiles = new TreeSet<>();
    for (ProfileDeclaration declaration : declarations) {
      SortedSet<Profile> profilesOfDeclaration = declaration.getProfiles();
      profiles.addAll(profilesOfDeclaration);
    }
  }

  @Operation(operationId = "getDescription",
      summary = "Returns the self-describing information of a network resource (ServiceDescription)",
      tags = {"Registry and Discovery Interface"}, responses = {
      @ApiResponse(responseCode = "200", description = "Requested Description", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceDescription.class))}),
      @ApiResponse(responseCode = "403", description = "Forbidden",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))}),
      @ApiResponse(responseCode = "default", description = "Default error handling for unmentioned status codes",
          content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))})})
  @RequestMapping(method = RequestMethod.GET, value = "/description", produces = {"application/json"})
  public ResponseEntity<ServiceDescription> getDescription() {
    ServiceDescription serviceDescription = new ServiceDescription();
    serviceDescription.profiles(new ArrayList<>(profiles));
    return new ResponseEntity<>(serviceDescription, HttpStatus.OK);
  }
}
