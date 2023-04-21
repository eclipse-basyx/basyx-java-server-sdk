package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.http;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.http.factory.FilterConceptDescriptionFactory;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-03-21T12:35:49.719724407Z[GMT]")
@RestController
public class ConceptDescriptionRepositoryApiHTTPController implements ConceptDescriptionRepositoryHTTPApi {
	
	private static final Logger logger = LoggerFactory.getLogger(ConceptDescriptionRepositoryApiHTTPController.class);
	
	private final ObjectMapper objectMapper;
	private ConceptDescriptionRepository repository;
	
	@Autowired
	public ConceptDescriptionRepositoryApiHTTPController(ConceptDescriptionRepository conceptDescriptionRepository, ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.repository = conceptDescriptionRepository;
	}

	public ResponseEntity<Void> deleteConceptDescriptionById(
			@Parameter(in = ParameterIn.PATH, description = "The Concept Description’s unique id (BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("cdIdentifier") Base64UrlEncodedIdentifier cdIdentifier) {
		repository.deleteConceptDescription(cdIdentifier.getIdentifier());
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	public ResponseEntity<List<ConceptDescription>> getAllConceptDescriptions(@Parameter(in = ParameterIn.QUERY, description = "The Concept Description’s IdShort" ,schema=@Schema()) @Valid @RequestParam(value = "idShort", required = false) String idShort,@Parameter(in = ParameterIn.QUERY, description = "IsCaseOf reference (BASE64-URL-encoded)" ,schema=@Schema()) @Valid @RequestParam(value = "isCaseOf", required = false) Base64UrlEncodedIdentifier isCaseOf,@Parameter(in = ParameterIn.QUERY, description = "DataSpecification reference (BASE64-URL-encoded)" ,schema=@Schema()) @Valid @RequestParam(value = "dataSpecificationRef", required = false) Base64UrlEncodedIdentifier dataSpecificationRef) {
		
		if (!hasPermittedParameters(idShort, getDecodedIdentifier(isCaseOf), getDecodedIdentifier(dataSpecificationRef))) {
			logger.error("Only one parameter should be set per request");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		FilterConceptDescriptionFactory factory = new FilterConceptDescriptionFactory(repository, idShort, getReference(getDecodedIdentifier(isCaseOf)), getReference(getDecodedIdentifier(dataSpecificationRef)));
		
		if (isIdShortParamSet(idShort))
			return new ResponseEntity<List<ConceptDescription>>(factory.create(), HttpStatus.OK);
		
		if (isIsCaseOfParamSet(getDecodedIdentifier(isCaseOf))) {
			Reference reference = getReference(getDecodedIdentifier(isCaseOf));
			
			if (reference == null)
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			
			return new ResponseEntity<List<ConceptDescription>>(factory.create(), HttpStatus.OK);
		}
		
		if (isDataSpecificationRefSet(getDecodedIdentifier(dataSpecificationRef))) {
			Reference reference = getReference(getDecodedIdentifier(dataSpecificationRef));
			
			if (reference == null)
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			
			return new ResponseEntity<List<ConceptDescription>>(factory.create(), HttpStatus.OK);
		}
			
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
	
	private boolean hasPermittedParameters(String idShort, String isCaseOf, String dataSpecificationRef) {

		return !areAllParametersSet(idShort, isCaseOf, dataSpecificationRef)
				&& (areAllParametersEmpty(idShort, isCaseOf, dataSpecificationRef)
				|| (isIdShortParamSet(idShort) ^ isIsCaseOfParamSet(isCaseOf)
						^ isDataSpecificationRefSet(dataSpecificationRef)));
	}

	private boolean areAllParametersEmpty(String idShort, String isCaseOf, String dataSpecificationRef) {
		return !isIdShortParamSet(idShort) && !isIsCaseOfParamSet(isCaseOf)
				&& !isDataSpecificationRefSet(dataSpecificationRef);
	}

	private boolean areAllParametersSet(String idShort, String isCaseOf, String dataSpecificationRef) {
		return isIdShortParamSet(idShort) && isIsCaseOfParamSet(isCaseOf) && isDataSpecificationRefSet(dataSpecificationRef);
	}

	private boolean isDataSpecificationRefSet(String dataSpecificationRef) {
		return dataSpecificationRef != null && !dataSpecificationRef.isBlank();
	}

	private boolean isIsCaseOfParamSet(String isCaseOf) {
		return isCaseOf != null && !isCaseOf.isBlank();
	}

	private boolean isIdShortParamSet(String idShort) {
		return idShort != null && !idShort.isBlank();
	}

	private Reference getReference(String serializedReference) {
		if (serializedReference == null)
			return null;

		Reference reference = null;

		try {
			reference = objectMapper.readValue(serializedReference, DefaultReference.class);
		} catch (JsonProcessingException e) {
			logger.error("Couldn't serialize response for content type application/json", e);
		}

		return reference;
	}

	private String getDecodedIdentifier(Base64UrlEncodedIdentifier identifier) {
		return identifier == null ? null : identifier.getIdentifier();
	}

}
