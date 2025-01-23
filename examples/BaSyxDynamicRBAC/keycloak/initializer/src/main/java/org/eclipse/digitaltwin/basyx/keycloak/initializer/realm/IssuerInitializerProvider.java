package org.eclipse.digitaltwin.basyx.keycloak.initializer.realm;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.digitaltwin.basyx.keycloak.initializer.InitializerProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.services.Urls;
import org.keycloak.services.validation.Validation;

import com.google.auto.service.AutoService;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;

@AutoService(InitializerProviderFactory.class)
public class IssuerInitializerProvider implements InitializerProviderFactory {

	public static final String PROVIDER_ID = "issuer";

	private static final String CONFIG_ATTR_BASE_URI = "base-uri";

	private static String issuerBaseUri;

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(IssuerInitializerProvider.class);

	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public void init(Config.Scope config) {
		issuerBaseUri = config.get(CONFIG_ATTR_BASE_URI);
		if (!Validation.isBlank(issuerBaseUri)) {
			log.info("Issuer BaseURI fixed value: {}", issuerBaseUri);
		} else {
			log.info("Issuer BaseURI is blank");
		}
	}

	@Override
	public void postInit(KeycloakSessionFactory factory) {
		ByteBuddyAgent.install();
		new ByteBuddy()
			.redefine(Urls.class)
			.method(named("realmIssuer").and(isDeclaredBy(Urls.class).and(returns(String.class))))
			.intercept(MethodDelegation.to(this.getClass()))
			.make()
			.load(Urls.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
	}

	public static String realmIssuer(URI baseUri, String realmName) {
		try {
			baseUri = new URI(issuerBaseUri);
		} catch (URISyntaxException | NullPointerException ignored) {
		}
		return Urls.realmBase(baseUri).path("{realm}").build(realmName).toString();
	}

	@Override
	public List<ProviderConfigProperty> getConfigMetadata() {
		return ProviderConfigurationBuilder.create()
			.property()
			.name(CONFIG_ATTR_BASE_URI)
			.type(ProviderConfigProperty.STRING_TYPE)
			.helpText("The baseUri to use for the issuer of this server. Keep empty, if the regular hostname settings should be used.")
			.add()
			.build();
	}
}


