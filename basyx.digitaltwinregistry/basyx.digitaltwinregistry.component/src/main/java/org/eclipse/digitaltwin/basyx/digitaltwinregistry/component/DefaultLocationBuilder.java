package org.eclipse.digitaltwin.basyx.digitaltwinregistry.component;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.eclipse.digitaltwin.basyx.aasregistry.service.api.LocationBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class DefaultLocationBuilder implements LocationBuilder {
	
	private static final String SM_ID_PARAM = "/{sm-id}";
	private static final String SUBMODEL_DESCRIPTORS = "/submodel-descriptors";
	private static final String SHELL_ID_PARAM = "/{shell-id}";
	private static final String SHELL_DESCRIPTORS = "/shell-descriptors";

	@Override
	public URI getAasLocation(String aasId) {
		String encodedAasId = encode(aasId);
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(SHELL_DESCRIPTORS).path(SHELL_ID_PARAM).buildAndExpand(encodedAasId).toUri();
	}

	@Override
	public URI getSubmodelLocation(String aasId, String submodelId) {
		String encodedShellId = encode(aasId);
		String encodedSmId = encode(submodelId);
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(SHELL_DESCRIPTORS).path(SHELL_ID_PARAM).path(SUBMODEL_DESCRIPTORS).path(SM_ID_PARAM).buildAndExpand(encodedShellId, encodedSmId).toUri();
	}

	private String encode(String value) {
		if (value == null) {
			return null;
		}
		return new String(java.util.Base64.getUrlEncoder().encode(value.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
	}

}
