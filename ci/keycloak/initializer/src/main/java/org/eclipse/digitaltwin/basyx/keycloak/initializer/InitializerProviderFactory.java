package org.eclipse.digitaltwin.basyx.keycloak.initializer;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;

public interface InitializerProviderFactory extends ProviderFactory<Provider>, Provider {
	
	@Override
	default Provider create(KeycloakSession session) {
		return null;
	}

	@Override
	default void init(Config.Scope config) {
	}

	@Override
	default void close() {
	}
}
