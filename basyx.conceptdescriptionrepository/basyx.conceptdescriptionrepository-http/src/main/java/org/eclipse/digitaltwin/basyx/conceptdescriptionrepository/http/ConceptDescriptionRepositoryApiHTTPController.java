package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.http;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-03-21T12:35:49.719724407Z[GMT]")
@RestController
public class ConceptDescriptionRepositoryApiHTTPController implements ConceptDescriptionRepositoryHTTPApi {

	private ConceptDescriptionRepository repository;

	@Autowired
	public ConceptDescriptionRepositoryApiHTTPController(ConceptDescriptionRepository conceptDescriptionRepository) {
		this.repository = conceptDescriptionRepository;
	}

	public ResponseEntity<Void> deleteConceptDescriptionById(
			@Parameter(in = ParameterIn.PATH, description = "The Concept Description’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("cdIdentifier") Base64UrlEncodedIdentifier cdIdentifier) {
		repository.deleteConceptDescription(cdIdentifier.getIdentifier());
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	public ResponseEntity<List<ConceptDescription>> getAllConceptDescriptions(@Parameter(in = ParameterIn.QUERY, description = "The Concept Description’s IdShort" ,schema=@Schema()) @Valid @RequestParam(value = "idShort", required = false) String idShort,@Parameter(in = ParameterIn.QUERY, description = "IsCaseOf reference (BASE64-URL-encoded)" ,schema=@Schema()) @Valid @RequestParam(value = "isCaseOf", required = false) String isCaseOf,@Parameter(in = ParameterIn.QUERY, description = "DataSpecification reference (BASE64-URL-encoded)" ,schema=@Schema()) @Valid @RequestParam(value = "dataSpecificationRef", required = false) String dataSpecificationRef) {
        return new ResponseEntity<List<ConceptDescription>>(new ArrayList<>(repository.getAllConceptDescriptions()), HttpStatus.OK);
    }

	public ResponseEntity<ConceptDescription> getConceptDescriptionById(
			@Parameter(in = ParameterIn.PATH, description = "The Concept Description’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("cdIdentifier") Base64UrlEncodedIdentifier cdIdentifier) {
		return new ResponseEntity<ConceptDescription>(repository.getConceptDescription(cdIdentifier.getIdentifier()),
				HttpStatus.OK);
	}

	public ResponseEntity<ConceptDescription> postConceptDescription(
			@Parameter(in = ParameterIn.DEFAULT, description = "Concept Description object", required = true, schema = @Schema()) @Valid @RequestBody ConceptDescription body) {
		repository.createConceptDescription(body);
		return new ResponseEntity<ConceptDescription>(body, HttpStatus.CREATED);
	}

	public ResponseEntity<Void> putConceptDescriptionById(
			@Parameter(in = ParameterIn.PATH, description = "The Concept Description’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("cdIdentifier") Base64UrlEncodedIdentifier cdIdentifier,
			@Parameter(in = ParameterIn.DEFAULT, description = "Concept Description object", required = true, schema = @Schema()) @Valid @RequestBody ConceptDescription body) {
		repository.updateConceptDescription(cdIdentifier.getIdentifier(), body);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

}
