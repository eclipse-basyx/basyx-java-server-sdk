/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/


package org.eclipse.digitaltwin.basyx.submodelrepository.tck;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.BaSyxSubmodelHttpTestUtils;
import org.springframework.http.HttpStatus;

import com.google.gson.Gson;

/**
 * 
 * @author schnicke
 *
 */
public class SubmodelTCKHelper {
	private static JsonSerializer serializer = new JsonSerializer();
	private static JsonDeserializer deserializer = new JsonDeserializer();
	private static Gson gson = new Gson();

	public static void createSubmodelOnRepository(String url, Submodel submodel) {
		try {
			String submodelJSON = serializer.write(submodel);
			CloseableHttpResponse response = BaSyxSubmodelHttpTestUtils.createSubmodel(url, submodelJSON);

			if (response.getCode() != HttpStatus.CREATED.value()) {
				fail();
			}
		} catch (SerializationException | IOException e) {
			fail(e.toString());
		}
	}

	public static void deleteAllSubmodelsOnRepository(String url) {
		getAllSubmodelIds(url).forEach(s -> deleteSubmodel(url, s));
	}

	private static void deleteSubmodel(String repoUrl, String id) {
		try {
			String url = BaSyxSubmodelHttpTestUtils.getSpecificSubmodelAccessPath(repoUrl, id);
			CloseableHttpResponse deleteResponse = BaSyxHttpTestUtils.executeDeleteOnURL(url);

			System.out.println("Deleting submodel with id " + id + ", ResponseCode is " + deleteResponse.getCode());
		} catch (IOException e) {
			fail(e.toString());
		}
	}

	private static List<String> getAllSubmodelIds(String url) {
		try {
			String jsonResponse = getJsonResponse(url);

			List<Submodel> submodels = deserializer.readList(jsonResponse, Submodel.class);
			return submodels.stream().map(Submodel::getId).collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String getJsonResponse(String url) throws IOException, ParseException {
		String rawResponse = BaSyxSubmodelHttpTestUtils.requestAllSubmodels(url);

		Object obj = gson.fromJson(rawResponse, Object.class);

		if (obj instanceof List)
			return rawResponse;

		return handlePaginationResultWrapper(obj);
	}

	@SuppressWarnings("unchecked")
	private static String handlePaginationResultWrapper(Object obj) {
		Map<String, ?> jsonMap = (Map<String, ?>) obj;
		
		return gson.toJson(jsonMap.get("result"));
	}
}
