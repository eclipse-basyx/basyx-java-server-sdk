package org.eclipse.digitaltwin.basyx.core;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.web.util.UriComponentsBuilder;


public class Helper {

	public static String createRepositoryUrl(String baseUrl, String additionalPath) {
        try {
            // Create a URI from the base URL
            URI baseUri = new URI(baseUrl);
    
            // Use UriComponentsBuilder to construct the new URL
            URI finalUri = UriComponentsBuilder.newInstance()
                    .scheme(baseUri.getScheme())
                    .host(baseUri.getHost())
                    .port(baseUri.getPort())
                    .pathSegment(baseUri.getPath().replaceAll("^/|/$", "").split("/"))
                    .pathSegment(additionalPath.replaceAll("^/|/$", "").split("/"))
                    .build()
                    .toUri();
    
            // Convert the URI to a URL and return its string representation
            return finalUri.toURL().toString();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException("The Base URL or additional path is malformed.\n" + e.getMessage(), e);
        }
	}
	
}
