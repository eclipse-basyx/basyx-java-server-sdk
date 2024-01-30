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

package org.eclipse.digitaltwin.basyx.authorization;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for retrieving access token
 * 
 * @author danish
 */
public class AccessTokenProvider {

	private String authenticationServerAddress;
	private String clientId;

	public AccessTokenProvider(String authenticationServerAddress, String clientId) {
		this.authenticationServerAddress = authenticationServerAddress;
		this.clientId = clientId;
	}

	public String getAccessToken(String username, String password) {
		try {
			URIBuilder uriBuilder = new URIBuilder(authenticationServerAddress);
			HttpPost httpPost = createHttpPost(username, password, uriBuilder);

			CloseableHttpClient httpClient = HttpClients.createDefault();

			@SuppressWarnings("deprecation")
			CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

			return getAccessTokenFromResponse(httpResponse);
		} catch (Exception e) {
			throw new RuntimeException("Error while requesting token", e);
		}
	}

	private HttpPost createHttpPost(String username, String password, URIBuilder uriBuilder) throws URISyntaxException {
		HttpPost httpPost = new HttpPost(uriBuilder.build());

		List<NameValuePair> formParams = new ArrayList<>();
		
		formParams.add(new BasicNameValuePair("grant_type", "password"));
		formParams.add(new BasicNameValuePair("client_id", clientId));
		formParams.add(new BasicNameValuePair("username", username));
		formParams.add(new BasicNameValuePair("password", password));
		httpPost.setEntity(new UrlEncodedFormEntity(formParams));
		
		return httpPost;
	}

	private String getAccessTokenFromResponse(final CloseableHttpResponse httpResponse) throws ParseException, IOException {
		final String responseString = getResponseAsString(httpResponse);

		final ObjectMapper objectMapper = new ObjectMapper();
		final JsonNode responseNode = objectMapper.readTree(responseString);

		return responseNode.get("access_token").asText();
	}
	
	private String getResponseAsString(CloseableHttpResponse retrievalResponse) throws IOException, ParseException {
		return EntityUtils.toString(retrievalResponse.getEntity(), "UTF-8");
	}

}
