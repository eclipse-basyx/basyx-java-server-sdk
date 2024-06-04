package org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.aasregistry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.main.client.AuthorizedConnectedAasRegistry;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb.KafkaEventsMongoDbStorageIntegrationTest.RegistrationEventKafkaListener;
import org.eclipse.digitaltwin.basyx.aasregistry.service.tests.integration.BaseIntegrationTest;
import org.eclipse.digitaltwin.basyx.aasregistry.service.tests.integration.EventQueue;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.TokenManager;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.credential.ClientCredential;
import org.eclipse.digitaltwin.basyx.client.internal.authorization.grant.ClientCredentialGrant;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"spring.profiles.active=kafkaEvents,mongoDbStorage", "spring.kafka.bootstrap-servers=PLAINTEXT_HOST://localhost:9092", "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9096/realms/BaSyx", "basyx.feature.authorization.enabled=true", "basyx.feature.authorization.type=rbac", "basyx.feature.authorization.jwtBearerTokenProvider=keycloak", "basyx.feature.authorization.rbac.file=classpath:rbac_rules.json", "spring.data.mongodb.database=aasregistry", "spring.data.mongodb.uri=mongodb://mongoAdmin:mongoPassword@localhost:27017/"})
public class AuthorizedClientTest extends BaseIntegrationTest {
	
	@Autowired
	private RegistrationEventKafkaListener listener;
	
	@Value("${local.server.port}")
	private int port;
	
	@Before
	public void awaitAssignment() throws InterruptedException {
		listener.awaitTopicAssignment();
	}

	@Override
	public EventQueue queue() {
		return listener.getQueue();
	}
	
	@Before
	@Override
	public void initClient() throws ApiException {
		api = new AuthorizedConnectedAasRegistry("http://127.0.0.1:" + port, new TokenManager("http://localhost:9096/realms/BaSyx/protocol/openid-connect/token", new ClientCredentialGrant(new ClientCredential("workstation-1", "nY0mjyECF60DGzNmQUjL81XurSl8etom"))));

		api.deleteAllShellDescriptors();
		queue().assertNoAdditionalMessage();
	}
	
	@Test
	public void sendUnauthorizedRequest() throws IOException {
		TokenManager mockTokenManager = Mockito.mock(TokenManager.class);

		Mockito.when(mockTokenManager.getAccessToken()).thenReturn("mockedAccessToken");

		RegistryAndDiscoveryInterfaceApi registryApi = new AuthorizedConnectedAasRegistry("http://127.0.0.1:" + port, mockTokenManager);

		AssetAdministrationShellDescriptor descriptor = new AssetAdministrationShellDescriptor();
		descriptor.setIdShort("shortId");

		ApiException exception = assertThrows(ApiException.class, () -> {
			registryApi.postAssetAdministrationShellDescriptor(descriptor);
		});

		assertEquals(HttpStatus.UNAUTHORIZED.value(), exception.getCode());
	}
	
	@Test
	@Override
	public void whenPostSubmodelDescriptor_LocationIsReturned() throws ApiException, IOException {
		
	}
	
	@Test
	@Override
	public void whenPostShellDescriptor_LocationIsReturned() throws ApiException, IOException {
		
	}

}
