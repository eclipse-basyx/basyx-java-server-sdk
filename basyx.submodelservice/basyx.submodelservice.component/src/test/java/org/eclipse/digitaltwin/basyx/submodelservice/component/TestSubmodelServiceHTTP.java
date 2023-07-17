package org.eclipse.digitaltwin.basyx.submodelservice.component;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class TestSubmodelServiceHTTP {
	private static ConfigurableApplicationContext appContext;

	@BeforeAll
	public static void startAASRepo() throws Exception {
		appContext = new SpringApplication(DummySubmodelServiceComponent.class).run(new String[] {});
	}

	@Test
	public void rightSubmodelIsReturnedOnGetRequest() throws IOException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(getURL());
		String actualSubmodel = new BasicHttpClientResponseHandler().handleResponse(response);
		String expectedSubmodel = BaSyxHttpTestUtils.readJSONStringFromClasspath("TechnicalSubmodel.json");
		assertEquals(expectedSubmodel, actualSubmodel);
	}

	@AfterAll
	public static void shutdownAASRepo() {
		appContext.close();
	}

	protected String getURL() {
		return "http://localhost:8080/submodel";
	}

}