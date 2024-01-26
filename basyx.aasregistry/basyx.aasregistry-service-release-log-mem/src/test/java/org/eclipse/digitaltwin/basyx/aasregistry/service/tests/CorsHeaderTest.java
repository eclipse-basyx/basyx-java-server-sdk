package org.eclipse.digitaltwin.basyx.aasregistry.service.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@TestPropertySource(properties = { "spring.profiles.active=logEvents,inMemoryStorage", "basyx.cors.allowed-origins=*", "basyx.cors.allowed-methods=GET,POST,PATCH,DELETE,PUT,OPTIONS,HEAD" })
public class CorsHeaderTest {

	@Value("${local.server.port}")
	private String port;
	
	@Test
	public void testCorsHeader() throws IOException {
		URL url = new URL("http://localhost:" + port + "/shell-descriptors");
		URLConnection connection = url.openConnection();
		List<String> varyHeaders = connection.getHeaderFields().get("Vary");
		assertThat(varyHeaders.contains("Access-Control-Request-Headers"));
		assertThat(varyHeaders.contains("Access-Control-Request-Method"));	
	}
	
}
