package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.http.filter.ConceptDescriptionRepositoryFilter;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.http.pagination.GetConceptDescriptionsResult;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResult;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResultPagingMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-03-21T12:35:49.719724407Z[GMT]")
@RestController
public class ConceptDescriptionRepositoryApiHTTPController implements ConceptDescriptionRepositoryHTTPApi {

	private final ObjectMapper objectMapper;
	private ConceptDescriptionRepository repository;
	private ConceptDescriptionRepositoryFilter repoFilter;

	@Autowired
	public ConceptDescriptionRepositoryApiHTTPController(ConceptDescriptionRepository conceptDescriptionRepository, ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.repository = conceptDescriptionRepository;
		repoFilter = new ConceptDescriptionRepositoryFilter(repository);
	}

	@Override
	public ResponseEntity<Void> deleteConceptDescriptionById(
			@Parameter(in = ParameterIn.PATH, description = "The Concept Description’s unique id (UTF8-BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("cdIdentifier") Base64UrlEncodedIdentifier cdIdentifier) {
		repository.deleteConceptDescription(cdIdentifier.getIdentifier());
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<PagedResult> getAllConceptDescriptions(@Valid String idShort, @Valid Base64UrlEncodedIdentifier isCaseOf, @Valid Base64UrlEncodedIdentifier dataSpecificationRef, @Min(1) @Valid Integer limit,
			@Valid Base64UrlEncodedCursor cursor) {
		if (limit == null) {
			limit = 100;
		}

		String decodedCursor = "";
		if (cursor != null) {
			decodedCursor = cursor.getDecodedCursor();
		}

		Reference isCaseOfReference = getReference(getDecodedValue(isCaseOf));
		Reference dataSpecificationReference = getReference(getDecodedValue(dataSpecificationRef));

		PaginationInfo pInfo = new PaginationInfo(limit, decodedCursor);

		CursorResult<List<ConceptDescription>> filteredResult = repoFilter.filter(idShort, isCaseOfReference, dataSpecificationReference, pInfo);

		String encodedCursor = getEncodedCursorFromCursorResult(filteredResult);

		GetConceptDescriptionsResult paginatedConceptDescription = new GetConceptDescriptionsResult();
		paginatedConceptDescription.setResult(filteredResult.getResult());
		paginatedConceptDescription.setPagingMetadata(new PagedResultPagingMetadata().cursor(encodedCursor));

		return new ResponseEntity<PagedResult>(paginatedConceptDescription, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ConceptDescription> getConceptDescriptionById(
			@Parameter(in = ParameterIn.PATH, description = "The Concept Description’s unique id (UTF8-BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("cdIdentifier") Base64UrlEncodedIdentifier cdIdentifier) {
		return new ResponseEntity<ConceptDescription>(repository.getConceptDescription(cdIdentifier.getIdentifier()), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ConceptDescription> postConceptDescription(@Parameter(in = ParameterIn.DEFAULT, description = "Concept Description object", required = true, schema = @Schema()) @Valid @RequestBody ConceptDescription body) {
		repository.createConceptDescription(body);
		return new ResponseEntity<ConceptDescription>(body, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<Void> putConceptDescriptionById(
			@Parameter(in = ParameterIn.PATH, description = "The Concept Description’s unique id (UTF8-BASE64-URL-encoded)", required = true, schema = @Schema()) @PathVariable("cdIdentifier") Base64UrlEncodedIdentifier cdIdentifier,
			@Parameter(in = ParameterIn.DEFAULT, description = "Concept Description object", required = true, schema = @Schema()) @Valid @RequestBody ConceptDescription body) {
		repository.updateConceptDescription(cdIdentifier.getIdentifier(), body);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	private Reference getReference(String serializedReference) {
		if (serializedReference == null) {
			return null;
		}

		try {
			return objectMapper.readValue(serializedReference, DefaultReference.class);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Passed reference string " + serializedReference + " is not a valid serialized reference");
		}
	}

	private String getDecodedValue(Base64UrlEncodedIdentifier identifier) {
		return identifier == null ? null : identifier.getIdentifier();
	}

	private String getEncodedCursorFromCursorResult(CursorResult<?> cursorResult) {
		if (cursorResult == null || cursorResult.getCursor() == null) {
			return null;
		}

		return Base64UrlEncodedCursor.encodeCursor(cursorResult.getCursor());
	}

}
