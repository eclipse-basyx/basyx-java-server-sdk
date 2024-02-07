package org.eclipse.digitaltwin.basyx.submodelrepository.feature.operation.delegation;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifier;
import org.eclipse.digitaltwin.basyx.core.exceptions.OperationDelegationException;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class HTTPOperationDelegation implements OperationDelegation {

	public static final String INVOCATION_DELEGATION_TYPE = "invocationDelegation";

	private WebClient webClient;

	public HTTPOperationDelegation(WebClient webClient) {
		this.webClient = webClient;
	}

	@Override
	public OperationVariable[] delegate(Qualifier qualifier, OperationVariable[] input) throws OperationDelegationException {

		String uri = qualifier.getValue();

		try {
			return webClient.post().uri(uri).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(input)).exchangeToMono(response -> {
				if (response.statusCode().isError()) {
					throw new OperationDelegationException(String.format("Unable to delegate the invocation operation on the URI: '%s' the response code is %s", uri, response.statusCode()));
				} else {
					return response.bodyToMono(OperationVariable[].class);
				}
			}).block();

		} catch (WebClientResponseException e) {
			throw new OperationDelegationException(String.format("Exception occurred while invocing operation on the URI: '%s' the error is %s", uri, e.getStackTrace()));
		}

	}

}
