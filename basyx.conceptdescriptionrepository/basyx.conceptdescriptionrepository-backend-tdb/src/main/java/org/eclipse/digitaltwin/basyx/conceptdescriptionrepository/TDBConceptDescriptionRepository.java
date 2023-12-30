/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository;

import java.awt.*;
import java.io.IOException;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import okhttp3.*;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * 
 * TDB implementation of the ConceptDescriptionRepository
 *
 * @author mhrimaz
 *
 */
public class TDBConceptDescriptionRepository implements ConceptDescriptionRepository {

	private String collectionName;
	private String cdRepositoryName;

	public TDBConceptDescriptionRepository(String collectionName) {
		this.collectionName = collectionName;
	}

	public TDBConceptDescriptionRepository(String collectionName, String cdRepositoryName) {
		this(collectionName);
		this.cdRepositoryName = cdRepositoryName;
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptions(PaginationInfo pInfo) {
		throw new RuntimeException();
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByIdShort(String idShort, PaginationInfo pInfo) {
		throw new RuntimeException();
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByIsCaseOf(Reference reference, PaginationInfo pInfo) {
		throw new RuntimeException();
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByDataSpecificationReference(Reference reference, PaginationInfo pInfo) {
		throw new RuntimeException();
	}

	@Override
	public ConceptDescription getConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		throw new RuntimeException();
	}

	@Override
	public void updateConceptDescription(String conceptDescriptionId, ConceptDescription conceptDescription) throws ElementDoesNotExistException {

		throw new RuntimeException();
	}

	private static String conceptDescriptionAsRDF(final ConceptDescription conceptDescription) throws SerializationException {
		// TODO: this should use AAS4j method in the future
		JsonSerializer jsonSerializer = new JsonSerializer();
		String conceptDescriptionAsJson = jsonSerializer.write(conceptDescription);
		OkHttpClient client = new OkHttpClient().newBuilder()
				.build();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, conceptDescriptionAsJson);
		Request request = new Request.Builder()
				.url("http://127.0.0.1:9192/concept-description:jsontordf")
				.method("POST", body)
				.addHeader("Content-Type", "application/json")
				.build();
		try {
			Response response = client.newCall(request).execute();
			String asRDF = response.body().string();
			String asRDFWithoutPrefixes = asRDF.replaceAll("(?m)^@.*$", "");
			return asRDFWithoutPrefixes;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void createConceptDescription(ConceptDescription conceptDescription) throws CollidingIdentifierException, MissingIdentifierException {
		String conceptDescriptionAsRDF = null;
		try {
			conceptDescriptionAsRDF = conceptDescriptionAsRDF(conceptDescription);
		} catch (SerializationException e) {
			throw new RuntimeException(e);
		}
		OkHttpClient client = new OkHttpClient().newBuilder()
				.build();
		MediaType mediaType = MediaType.parse("application/sparql-update");
		RequestBody body = RequestBody.create(mediaType, "prefix aas: <https://admin-shell.io/aas/3/0/>\r\nprefix xsd: <http://www.w3.org/2001/XMLSchema#>  \r\nINSERT DATA \r\n{"+conceptDescriptionAsRDF+"}");
		Request request = new Request.Builder()
				.url("http://127.0.0.1:3030/test/update")
				.method("POST", body)
				.addHeader("Content-Type", "application/sparql-update")
				.build();
		try {
			Response response = client.newCall(request).execute();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		throw new RuntimeException();
	}

	@Override
	public void deleteConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		throw new RuntimeException();
	}

	@Override
	public String getName() {
		return cdRepositoryName == null ? ConceptDescriptionRepository.super.getName() : cdRepositoryName;
	}

}
