/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
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
package org.eclipse.digitaltwin.basyx.aasservice.client.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.processing.Generated;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonMapperFactory;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.SimpleAbstractTypeResolverFactory;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.client.internal.ApiClient;
import org.eclipse.digitaltwin.basyx.client.internal.ApiException;
import org.eclipse.digitaltwin.basyx.client.internal.ApiResponse;
import org.eclipse.digitaltwin.basyx.client.internal.Pair;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.core.exceptions.AccessTokenRetrievalException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.http.description.ServiceDescription;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursorResult;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-02-08T19:43:45.125130100+01:00[Europe/Berlin]")
public class AssetAdministrationShellServiceApi {
  private final HttpClient memberVarHttpClient;
  private final ObjectMapper memberVarObjectMapper;
  private final String memberVarBaseUri;
  private final Consumer<HttpRequest.Builder> memberVarInterceptor;
  private final Duration memberVarReadTimeout;
  private final Consumer<HttpResponse<InputStream>> memberVarResponseInterceptor;
  private TokenManager tokenManager;

  public AssetAdministrationShellServiceApi() {
    this(new ApiClient());
  }
  
  public AssetAdministrationShellServiceApi(TokenManager tokenManager) {
	    this(new ApiClient());
	    this.tokenManager = tokenManager;
	  }

  public AssetAdministrationShellServiceApi(ObjectMapper mapper, String baseUri) {
    this(new ApiClient(HttpClient.newBuilder(), mapper, baseUri));
  }
  
  public AssetAdministrationShellServiceApi(ObjectMapper mapper, String baseUri, TokenManager tokenManager) {
	    this(new ApiClient(HttpClient.newBuilder(), mapper, baseUri));
	    this.tokenManager = tokenManager;
  }
  
  public AssetAdministrationShellServiceApi(String baseUri) {
		this(new ApiClient(HttpClient.newBuilder(), new JsonMapperFactory().create(new SimpleAbstractTypeResolverFactory().create()), baseUri));
  }
  
  public AssetAdministrationShellServiceApi(String baseUri, TokenManager tokenManager) {
		this(new ApiClient(HttpClient.newBuilder(), new JsonMapperFactory().create(new SimpleAbstractTypeResolverFactory().create()), baseUri));
		this.tokenManager = tokenManager;
  }


  public AssetAdministrationShellServiceApi(ApiClient apiClient) {
    memberVarHttpClient = apiClient.getHttpClient();
    memberVarObjectMapper = apiClient.getObjectMapper();
    memberVarBaseUri = apiClient.getBaseUri();
    memberVarInterceptor = apiClient.getRequestInterceptor();
    memberVarReadTimeout = apiClient.getReadTimeout();
    memberVarResponseInterceptor = apiClient.getResponseInterceptor();
  }

  protected ApiException getApiException(String operationId, HttpResponse<InputStream> response) throws IOException {
    String body = response.body() == null ? null : new String(response.body().readAllBytes());
    String message = formatExceptionMessage(operationId, response.statusCode(), body);
    return new ApiException(response.statusCode(), message, response.headers(), body);
  }

  private String formatExceptionMessage(String operationId, int statusCode, String body) {
    if (body == null || body.isEmpty()) {
      body = "[no body]";
    }
    return operationId + " call failed with: " + statusCode + " - " + body;
  }

  	/**
	 * Deletes the submodel reference from the Asset Administration Shell. Does not
	 * delete the submodel itself!
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public void deleteSubmodelReferenceById(String submodelIdentifier) throws ApiException {

		deleteSubmodelReferenceByIdWithHttpInfo(submodelIdentifier);
	}

	/**
	 * Deletes the submodel reference from the Asset Administration Shell. Does not
	 * delete the submodel itself!
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> deleteSubmodelReferenceByIdWithHttpInfo(String submodelIdentifier) throws ApiException {
		String submodelIdentifierAsBytes = ApiClient.base64UrlEncode(submodelIdentifier);
		return deleteSubmodelReferenceByIdWithHttpInfoNoUrlEncoding(submodelIdentifierAsBytes);

	}

	/**
	 * Deletes the submodel reference from the Asset Administration Shell. Does not
	 * delete the submodel itself!
	 * 
	 * @param submodelIdentifier
	 *            The Submodel’s unique id (UTF8-BASE64-URL-encoded) (required)
	 * @return ApiResponse&lt;Void&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<Void> deleteSubmodelReferenceByIdWithHttpInfoNoUrlEncoding(String submodelIdentifier) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = deleteSubmodelReferenceByIdRequestBuilder(submodelIdentifier);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("deleteSubmodelReferenceById", localVarResponse);
				}
				return new ApiResponse<Void>(localVarResponse.statusCode(), localVarResponse.headers().map(), null);
			} finally {
				// Drain the InputStream
				while (localVarResponse.body().read() != -1) {
					// Ignore
				}
				localVarResponse.body().close();
			}
		} catch (IOException e) {
			throw new ApiException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ApiException(e);
		}
	}

	private HttpRequest.Builder deleteSubmodelReferenceByIdRequestBuilder(String submodelIdentifier) throws ApiException {
		// verify the required parameter 'submodelIdentifier' is set
		if (submodelIdentifier == null) {
			throw new ApiException(400, "Missing the required parameter 'submodelIdentifier' when calling deleteSubmodelReferenceById");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/submodel-refs/{submodelIdentifier}".replace("{submodelIdentifier}", ApiClient.urlEncode(submodelIdentifier.toString()));

		localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

		localVarRequestBuilder.header("Accept", "application/json");
		
		addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);

		localVarRequestBuilder.method("DELETE", HttpRequest.BodyPublishers.noBody());
		if (memberVarReadTimeout != null) {
			localVarRequestBuilder.timeout(memberVarReadTimeout);
		}
		if (memberVarInterceptor != null) {
			memberVarInterceptor.accept(localVarRequestBuilder);
		}
		return localVarRequestBuilder;
	}

	/**
	 * 
	 * 
	 * @throws ApiException
	 *             if fails to make API call
	 */
  public void deleteThumbnail() throws ApiException {

    deleteThumbnailWithHttpInfo();
  }

  /**
   * 
   * 
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> deleteThumbnailWithHttpInfo() throws ApiException {
  	return deleteThumbnailWithHttpInfoNoUrlEncoding();
 	
 }


  /**
   * 
   * 
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> deleteThumbnailWithHttpInfoNoUrlEncoding() throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = deleteThumbnailRequestBuilder();
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("deleteThumbnail", localVarResponse);
        }
        return new ApiResponse<Void>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          null
        );
      } finally {
        // Drain the InputStream
        while (localVarResponse.body().read() != -1) {
            // Ignore
        }
        localVarResponse.body().close();
      }
    } catch (IOException e) {
      throw new ApiException(e);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ApiException(e);
    }
  }

  private HttpRequest.Builder deleteThumbnailRequestBuilder() throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

	String localVarPath = "/asset-information/thumbnail";

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Accept", "application/json");
    
    addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);

    localVarRequestBuilder.method("DELETE", HttpRequest.BodyPublishers.noBody());
    if (memberVarReadTimeout != null) {
      localVarRequestBuilder.timeout(memberVarReadTimeout);
    }
    if (memberVarInterceptor != null) {
      memberVarInterceptor.accept(localVarRequestBuilder);
    }
    return localVarRequestBuilder;
  }

	/**
	 * Returns all submodel references
	 * 
	 * @param limit
	 *            The maximum number of elements in the response array (optional)
	 * @param cursor
	 *            A server-generated identifier retrieved from pagingMetadata that
	 *            specifies from which position the result listing should continue
	 *            (optional)
	 * @return CursorResult&lt;List&lt;Reference&gt;&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public CursorResult<List<Reference>> getAllSubmodelReferences(Integer limit, String cursor) throws ApiException {
		ApiResponse<Base64UrlEncodedCursorResult<List<Reference>>> localVarResponse = getAllSubmodelReferencesApiResponse(limit, cursor);
		return localVarResponse.getData();
	}

	private ApiResponse<Base64UrlEncodedCursorResult<List<Reference>>> getAllSubmodelReferencesApiResponse(Integer limit, String cursor) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getAllSubmodelReferencesRequestBuilder(limit, cursor);
    try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
      }
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("getAllSubmodelReferences", localVarResponse);
				}
		return new ApiResponse<Base64UrlEncodedCursorResult<List<Reference>>>(
						localVarResponse.statusCode(), localVarResponse.headers().map(),
				localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<Base64UrlEncodedCursorResult<List<Reference>>>() {
				}) // closes the InputStream
				);
			} finally {
			}
		} catch (IOException e) {
			throw new ApiException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ApiException(e);
    }
  }

  private HttpRequest.Builder getAllSubmodelReferencesRequestBuilder(Integer limit, String cursor) throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel-refs";

    List<Pair> localVarQueryParams = new ArrayList<>();
    StringJoiner localVarQueryStringJoiner = new StringJoiner("&");
    localVarQueryParams.addAll(ApiClient.parameterToPairs("limit", limit));
    localVarQueryParams.addAll(ApiClient.parameterToPairs("cursor", cursor));

    if (!localVarQueryParams.isEmpty() || localVarQueryStringJoiner.length() != 0) {
      StringJoiner queryJoiner = new StringJoiner("&");
      localVarQueryParams.forEach(p -> queryJoiner.add(p.getName() + '=' + p.getValue()));
      if (localVarQueryStringJoiner.length() != 0) {
        queryJoiner.add(localVarQueryStringJoiner.toString());
      }
      localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath + '?' + queryJoiner.toString()));
    } else {
      localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));
    }

    localVarRequestBuilder.header("Accept", "application/json");
    
    addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);

    localVarRequestBuilder.method("GET", HttpRequest.BodyPublishers.noBody());
    if (memberVarReadTimeout != null) {
      localVarRequestBuilder.timeout(memberVarReadTimeout);
    }
    if (memberVarInterceptor != null) {
      memberVarInterceptor.accept(localVarRequestBuilder);
    }
    return localVarRequestBuilder;
  }
  /**
   * Returns a specific Asset Administration Shell
   * 
   * @return AssetAdministrationShell
   * @throws ApiException if fails to make API call
   */
  public AssetAdministrationShell getAssetAdministrationShell() throws ApiException {

    ApiResponse<AssetAdministrationShell> localVarResponse = getAssetAdministrationShellWithHttpInfo();
    return localVarResponse.getData();
  }

  /**
   * Returns a specific Asset Administration Shell
   * 
   * @return ApiResponse&lt;AssetAdministrationShell&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<AssetAdministrationShell> getAssetAdministrationShellWithHttpInfo() throws ApiException {
  	return getAssetAdministrationShellWithHttpInfoNoUrlEncoding();
 	
 }


  /**
   * Returns a specific Asset Administration Shell
   * 
   * @return ApiResponse&lt;AssetAdministrationShell&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<AssetAdministrationShell> getAssetAdministrationShellWithHttpInfoNoUrlEncoding() throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAssetAdministrationShellRequestBuilder();
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAssetAdministrationShell", localVarResponse);
        }
        return new ApiResponse<AssetAdministrationShell>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<AssetAdministrationShell>() {}) // closes the InputStream
        );
      } finally {
      }
    } catch (IOException e) {
      throw new ApiException(e);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ApiException(e);
    }
  }

  private HttpRequest.Builder getAssetAdministrationShellRequestBuilder() throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

	localVarRequestBuilder.uri(URI.create(memberVarBaseUri));

    localVarRequestBuilder.header("Accept", "application/json");
    
    addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);

    localVarRequestBuilder.method("GET", HttpRequest.BodyPublishers.noBody());
    if (memberVarReadTimeout != null) {
      localVarRequestBuilder.timeout(memberVarReadTimeout);
    }
    if (memberVarInterceptor != null) {
      memberVarInterceptor.accept(localVarRequestBuilder);
    }
    return localVarRequestBuilder;
  }
  /**
   * Returns a specific Asset Administration Shell as a Reference
   * 
   * @return Reference
   * @throws ApiException if fails to make API call
   */
  public Reference getAssetAdministrationShellReference() throws ApiException {

    ApiResponse<Reference> localVarResponse = getAssetAdministrationShellReferenceWithHttpInfo();
    return localVarResponse.getData();
  }

  /**
   * Returns a specific Asset Administration Shell as a Reference
   * 
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Reference> getAssetAdministrationShellReferenceWithHttpInfo() throws ApiException {
  	return getAssetAdministrationShellReferenceWithHttpInfoNoUrlEncoding();
 	
 }


  /**
   * Returns a specific Asset Administration Shell as a Reference
   * 
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Reference> getAssetAdministrationShellReferenceWithHttpInfoNoUrlEncoding() throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAssetAdministrationShellReferenceRequestBuilder();
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAssetAdministrationShellReference", localVarResponse);
        }
        return new ApiResponse<Reference>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<Reference>() {}) // closes the InputStream
        );
      } finally {
      }
    } catch (IOException e) {
      throw new ApiException(e);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ApiException(e);
    }
  }

  private HttpRequest.Builder getAssetAdministrationShellReferenceRequestBuilder() throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/$reference";

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Accept", "application/json");
    
    addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);

    localVarRequestBuilder.method("GET", HttpRequest.BodyPublishers.noBody());
    if (memberVarReadTimeout != null) {
      localVarRequestBuilder.timeout(memberVarReadTimeout);
    }
    if (memberVarInterceptor != null) {
      memberVarInterceptor.accept(localVarRequestBuilder);
    }
    return localVarRequestBuilder;
  }
  /**
   * Returns the Asset Information
   * 
   * @return AssetInformation
   * @throws ApiException if fails to make API call
   */
  public AssetInformation getAssetInformation() throws ApiException {

    ApiResponse<AssetInformation> localVarResponse = getAssetInformationWithHttpInfo();
    return localVarResponse.getData();
  }

  /**
   * Returns the Asset Information
   * 
   * @return ApiResponse&lt;AssetInformation&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<AssetInformation> getAssetInformationWithHttpInfo() throws ApiException {
  	return getAssetInformationWithHttpInfoNoUrlEncoding();
 	
 }


  /**
   * Returns the Asset Information
   * 
   * @return ApiResponse&lt;AssetInformation&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<AssetInformation> getAssetInformationWithHttpInfoNoUrlEncoding() throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getAssetInformationRequestBuilder();
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getAssetInformation", localVarResponse);
        }
        return new ApiResponse<AssetInformation>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<AssetInformation>() {}) // closes the InputStream
        );
      } finally {
      }
    } catch (IOException e) {
      throw new ApiException(e);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ApiException(e);
    }
  }

  private HttpRequest.Builder getAssetInformationRequestBuilder() throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

	String localVarPath = "/asset-information";

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Accept", "application/json");
    
    addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);

    localVarRequestBuilder.method("GET", HttpRequest.BodyPublishers.noBody());
    if (memberVarReadTimeout != null) {
      localVarRequestBuilder.timeout(memberVarReadTimeout);
    }
    if (memberVarInterceptor != null) {
      memberVarInterceptor.accept(localVarRequestBuilder);
    }
    return localVarRequestBuilder;
  }
  /**
   * Returns the self-describing information of a network resource (ServiceDescription)
   * 
   * @return ServiceDescription
   * @throws ApiException if fails to make API call
   */
  public ServiceDescription getDescription() throws ApiException {

    ApiResponse<ServiceDescription> localVarResponse = getDescriptionWithHttpInfo();
    return localVarResponse.getData();
  }

  /**
   * Returns the self-describing information of a network resource (ServiceDescription)
   * 
   * @return ApiResponse&lt;ServiceDescription&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<ServiceDescription> getDescriptionWithHttpInfo() throws ApiException {
  	return getDescriptionWithHttpInfoNoUrlEncoding();
 	
 }


  /**
   * Returns the self-describing information of a network resource (ServiceDescription)
   * 
   * @return ApiResponse&lt;ServiceDescription&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<ServiceDescription> getDescriptionWithHttpInfoNoUrlEncoding() throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = getDescriptionRequestBuilder();
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("getDescription", localVarResponse);
        }
        return new ApiResponse<ServiceDescription>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<ServiceDescription>() {}) // closes the InputStream
        );
      } finally {
      }
    } catch (IOException e) {
      throw new ApiException(e);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ApiException(e);
    }
  }

  private HttpRequest.Builder getDescriptionRequestBuilder() throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/description";

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Accept", "application/json");
    
    addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);

    localVarRequestBuilder.method("GET", HttpRequest.BodyPublishers.noBody());
    if (memberVarReadTimeout != null) {
      localVarRequestBuilder.timeout(memberVarReadTimeout);
    }
    if (memberVarInterceptor != null) {
      memberVarInterceptor.accept(localVarRequestBuilder);
    }
    return localVarRequestBuilder;
  }
  /**
   * 
   * 
   * @return File
   * @throws ApiException if fails to make API call
   */
  public File getThumbnail() throws ApiException {

    ApiResponse<File> localVarResponse = getThumbnailWithHttpInfo();
    return localVarResponse.getData();
  }

	/**
	 * 
	 * 
	 * @return ApiResponse&lt;File&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<File> getThumbnailWithHttpInfo() throws ApiException {
		return getThumbnailWithHttpInfoNoUrlEncoding();
	}

	/**
	 * @return ApiResponse&lt;File&gt;
	 * @throws ApiException
	 *             if fails to make API call
	 */
	public ApiResponse<File> getThumbnailWithHttpInfoNoUrlEncoding() throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = getThumbnailRequestBuilder();
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			if (localVarResponse.statusCode() / 100 != 2) {
				throw getApiException("getThumbnail", localVarResponse);
			}
			if (localVarResponse.body() == null) {
				return new ApiResponse<File>(localVarResponse.statusCode(), localVarResponse.headers().map(), null);
			} else {
				File tempFile = File.createTempFile(buildUniqueFilename(), extractFileName(localVarResponse.headers()));
				try (FileOutputStream out = new FileOutputStream(tempFile)) {
					localVarResponse.body().transferTo(out);
					return new ApiResponse<File>(localVarResponse.statusCode(), localVarResponse.headers().map(), tempFile);
				} finally {
					localVarResponse.body().close();
				}
			}
		} catch (IOException e) {
			throw new ApiException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ApiException(e);
		}
	}

  private HttpRequest.Builder getThumbnailRequestBuilder() throws ApiException {

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

	String localVarPath = "/asset-information/thumbnail";

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Accept", "application/octet-stream, application/json");
    
    addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);

    localVarRequestBuilder.method("GET", HttpRequest.BodyPublishers.noBody());
    if (memberVarReadTimeout != null) {
      localVarRequestBuilder.timeout(memberVarReadTimeout);
    }
    if (memberVarInterceptor != null) {
      memberVarInterceptor.accept(localVarRequestBuilder);
    }
    return localVarRequestBuilder;
  }
  /**
   * Creates a submodel reference at the Asset Administration Shell
   * 
   * @param reference Reference to the Submodel (required)
   * @return Reference
   * @throws ApiException if fails to make API call
   */
  public Reference postSubmodelReference(Reference reference) throws ApiException {

    ApiResponse<Reference> localVarResponse = postSubmodelReferenceWithHttpInfo(reference);
    return localVarResponse.getData();
  }

  /**
   * Creates a submodel reference at the Asset Administration Shell
   * 
   * @param reference Reference to the Submodel (required)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Reference> postSubmodelReferenceWithHttpInfo(Reference reference) throws ApiException {
  	return postSubmodelReferenceWithHttpInfoNoUrlEncoding(reference);
 	
 }


  /**
   * Creates a submodel reference at the Asset Administration Shell
   * 
   * @param reference Reference to the Submodel (required)
   * @return ApiResponse&lt;Reference&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Reference> postSubmodelReferenceWithHttpInfoNoUrlEncoding(Reference reference) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = postSubmodelReferenceRequestBuilder(reference);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("postSubmodelReference", localVarResponse);
        }
        return new ApiResponse<Reference>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          localVarResponse.body() == null ? null : memberVarObjectMapper.readValue(localVarResponse.body(), new TypeReference<Reference>() {}) // closes the InputStream
        );
      } finally {
      }
    } catch (IOException e) {
      throw new ApiException(e);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ApiException(e);
    }
  }

  private HttpRequest.Builder postSubmodelReferenceRequestBuilder(Reference reference) throws ApiException {
    // verify the required parameter 'reference' is set
    if (reference == null) {
      throw new ApiException(400, "Missing the required parameter 'reference' when calling postSubmodelReference");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/submodel-refs";

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");
    
    addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(reference);
      localVarRequestBuilder.method("POST", HttpRequest.BodyPublishers.ofByteArray(localVarPostBody));
    } catch (IOException e) {
      throw new ApiException(e);
    }
    if (memberVarReadTimeout != null) {
      localVarRequestBuilder.timeout(memberVarReadTimeout);
    }
    if (memberVarInterceptor != null) {
      memberVarInterceptor.accept(localVarRequestBuilder);
    }
    return localVarRequestBuilder;
  }
  /**
   * Updates an existing Asset Administration Shell
   * 
   * @param assetAdministrationShell Asset Administration Shell object (required)
   * @throws ApiException if fails to make API call
   */
  public void putAssetAdministrationShell(AssetAdministrationShell assetAdministrationShell) throws ApiException {

    putAssetAdministrationShellWithHttpInfo(assetAdministrationShell);
  }

  /**
   * Updates an existing Asset Administration Shell
   * 
   * @param assetAdministrationShell Asset Administration Shell object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> putAssetAdministrationShellWithHttpInfo(AssetAdministrationShell assetAdministrationShell) throws ApiException {
  	return putAssetAdministrationShellWithHttpInfoNoUrlEncoding(assetAdministrationShell);
 	
 }


  /**
   * Updates an existing Asset Administration Shell
   * 
   * @param assetAdministrationShell Asset Administration Shell object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putAssetAdministrationShellWithHttpInfoNoUrlEncoding(AssetAdministrationShell assetAdministrationShell) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putAssetAdministrationShellRequestBuilder(assetAdministrationShell);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("putAssetAdministrationShell", localVarResponse);
        }
        return new ApiResponse<Void>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          null
        );
      } finally {
        // Drain the InputStream
        while (localVarResponse.body().read() != -1) {
            // Ignore
        }
        localVarResponse.body().close();
      }
    } catch (IOException e) {
      throw new ApiException(e);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ApiException(e);
    }
  }

  private HttpRequest.Builder putAssetAdministrationShellRequestBuilder(AssetAdministrationShell assetAdministrationShell) throws ApiException {
    // verify the required parameter 'assetAdministrationShell' is set
    if (assetAdministrationShell == null) {
      throw new ApiException(400, "Missing the required parameter 'assetAdministrationShell' when calling putAssetAdministrationShell");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

    String localVarPath = "/";

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");
    
    addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(assetAdministrationShell);
      localVarRequestBuilder.method("PUT", HttpRequest.BodyPublishers.ofByteArray(localVarPostBody));
    } catch (IOException e) {
      throw new ApiException(e);
    }
    if (memberVarReadTimeout != null) {
      localVarRequestBuilder.timeout(memberVarReadTimeout);
    }
    if (memberVarInterceptor != null) {
      memberVarInterceptor.accept(localVarRequestBuilder);
    }
    return localVarRequestBuilder;
  }
  /**
   * Updates the Asset Information
   * 
   * @param assetInformation Asset Information object (required)
   * @throws ApiException if fails to make API call
   */
  public void putAssetInformation(AssetInformation assetInformation) throws ApiException {

    putAssetInformationWithHttpInfo(assetInformation);
  }

  /**
   * Updates the Asset Information
   * 
   * @param assetInformation Asset Information object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
 public ApiResponse<Void> putAssetInformationWithHttpInfo(AssetInformation assetInformation) throws ApiException {
  	return putAssetInformationWithHttpInfoNoUrlEncoding(assetInformation);
 	
 }


  /**
   * Updates the Asset Information
   * 
   * @param assetInformation Asset Information object (required)
   * @return ApiResponse&lt;Void&gt;
   * @throws ApiException if fails to make API call
   */
  public ApiResponse<Void> putAssetInformationWithHttpInfoNoUrlEncoding(AssetInformation assetInformation) throws ApiException {
    HttpRequest.Builder localVarRequestBuilder = putAssetInformationRequestBuilder(assetInformation);
    try {
      HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(
          localVarRequestBuilder.build(),
          HttpResponse.BodyHandlers.ofInputStream());
      if (memberVarResponseInterceptor != null) {
        memberVarResponseInterceptor.accept(localVarResponse);
      }
      try {
        if (localVarResponse.statusCode()/ 100 != 2) {
          throw getApiException("putAssetInformation", localVarResponse);
        }
        return new ApiResponse<Void>(
          localVarResponse.statusCode(),
          localVarResponse.headers().map(),
          null
        );
      } finally {
        // Drain the InputStream
        while (localVarResponse.body().read() != -1) {
            // Ignore
        }
        localVarResponse.body().close();
      }
    } catch (IOException e) {
      throw new ApiException(e);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ApiException(e);
    }
  }

  private HttpRequest.Builder putAssetInformationRequestBuilder(AssetInformation assetInformation) throws ApiException {
    // verify the required parameter 'assetInformation' is set
    if (assetInformation == null) {
      throw new ApiException(400, "Missing the required parameter 'assetInformation' when calling putAssetInformation");
    }

    HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

	String localVarPath = "/asset-information";

    localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));

    localVarRequestBuilder.header("Content-Type", "application/json");
    localVarRequestBuilder.header("Accept", "application/json");
    
    addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);

    try {
      byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(assetInformation);
      localVarRequestBuilder.method("PUT", HttpRequest.BodyPublishers.ofByteArray(localVarPostBody));
    } catch (IOException e) {
      throw new ApiException(e);
    }
    if (memberVarReadTimeout != null) {
      localVarRequestBuilder.timeout(memberVarReadTimeout);
    }
    if (memberVarInterceptor != null) {
      memberVarInterceptor.accept(localVarRequestBuilder);
    }
    return localVarRequestBuilder;
  }
  
	/**
	 * 
	 * @param fileName
	 * @param contentTypeStr
	 * @param inputStream
	 * @throws ApiException
	 */
	public void putThumbnail(String fileName, String contentTypeStr, InputStream inputStream) throws ApiException {
		ContentType contentType = ContentType.DEFAULT_BINARY;
		try {
			contentType = ContentType.parse(contentTypeStr);
		} catch (Exception e) {
			System.err.println("Error parsing content type: " + e.getMessage());
		}

		putThumbnailWithHttpInfoNoUrlEncoding(fileName, contentType, inputStream);
	}

	private ApiResponse<Void> putThumbnailWithHttpInfoNoUrlEncoding(String fileName, ContentType contentType, InputStream inputStream) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = putThumbnailRequestBuilder(fileName, contentType, inputStream);
		try {
			HttpResponse<InputStream> localVarResponse = memberVarHttpClient.send(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
			if (memberVarResponseInterceptor != null) {
				memberVarResponseInterceptor.accept(localVarResponse);
			}
			try {
				if (localVarResponse.statusCode() / 100 != 2) {
					throw getApiException("putThumbnail", localVarResponse);
				}
				return new ApiResponse<Void>(localVarResponse.statusCode(), localVarResponse.headers().map(), null);
			} finally {
				localVarResponse.body().close();
			}
		} catch (IOException e) {
			throw new ApiException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ApiException(e);
		}
	}

	private HttpRequest.Builder putThumbnailRequestBuilder(String fileName, ContentType contentType, InputStream inputStream) throws ApiException {
		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();
		String localVarPath = "/asset-information/thumbnail";
		localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));
		localVarRequestBuilder.header("Accept", "application/json");

		// Building multipart/form-data
		var multiPartBuilder = MultipartEntityBuilder.create();
		multiPartBuilder.addTextBody("fileName", fileName);
		multiPartBuilder.addBinaryBody("file", inputStream, contentType, fileName);
		var entity = multiPartBuilder.build();

		Pipe pipe;
		try {
			pipe = Pipe.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		new Thread(() -> {
			try (OutputStream outputStream = Channels.newOutputStream(pipe.sink())) {
				entity.writeTo(outputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();

		HttpRequest.BodyPublisher formDataPublisher = HttpRequest.BodyPublishers.ofInputStream(() -> Channels.newInputStream(pipe.source()));

		localVarRequestBuilder.header("Content-Type", entity.getContentType().getValue()).method("PUT", formDataPublisher);
		
		addAuthorizationHeaderIfAuthIsEnabled(localVarRequestBuilder);

		if (memberVarReadTimeout != null) {
			localVarRequestBuilder.timeout(memberVarReadTimeout);
		}
		if (memberVarInterceptor != null) {
			memberVarInterceptor.accept(localVarRequestBuilder);
		}
		return localVarRequestBuilder;
	}

	private static String extractFileName(HttpHeaders headers) {
		Optional<String> contentType = headers.firstValue("Content-Type");
		try {
			return "." + MimeTypeUtils.parseMimeType(contentType.get()).getSubtype();
		} catch (Exception e) {
			return ".tmp";
		}
	}

	private static String buildUniqueFilename() {
		return UUID.randomUUID().toString();
	}
	
	private void addAuthorizationHeaderIfAuthIsEnabled(HttpRequest.Builder localVarRequestBuilder) {
		if (tokenManager != null) {
	    	try {
	    		localVarRequestBuilder.header("Authorization", "Bearer " + tokenManager.getAccessToken());
			} catch (IOException e) {
				e.printStackTrace();
				throw new AccessTokenRetrievalException("Unable to request access token");
			}
	    }
	}

}