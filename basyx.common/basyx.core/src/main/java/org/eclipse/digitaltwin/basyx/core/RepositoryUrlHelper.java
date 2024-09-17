package org.eclipse.digitaltwin.basyx.core;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class RepositoryUrlHelper {

	public static String createRepositoryUrl(String baseUrl, String additionalPath) {
		try {
            URI baseUri = new URI(baseUrl);

            String[] basePathSegments = baseUri.getPath().replaceAll("^/|/$", "").split("/");
            String[] additionalPathSegments = additionalPath != null ? additionalPath.replaceAll("^/|/$", "").split("/") : new String[0];

            StringBuilder fullPath = new StringBuilder();
            for (String segment : basePathSegments) {
                if (!segment.isEmpty()) {
                    fullPath.append("/").append(segment);
                }
            }
            for (String segment : additionalPathSegments) {
                if (!segment.isEmpty()) {
                    fullPath.append("/").append(segment);
                }
            }

            URI finalUri = new URI(baseUri.getScheme(), null, baseUri.getHost(), baseUri.getPort(), fullPath.toString(), null, null);

            return finalUri.toURL().toString();
		} catch (URISyntaxException | MalformedURLException e) {
			throw new RuntimeException("The Base URL or additional path is malformed.\n" + e.getMessage(), e); 
		}
	}

}
