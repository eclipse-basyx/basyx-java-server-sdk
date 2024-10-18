import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonMapperFactory;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.SimpleAbstractTypeResolverFactory;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;

public class PostOperation {

	public OperationVariable[] invoke(OperationVariable[] in) {
		try {
			String uri = System.getenv("basyx.operation.proxy.uri");
			ObjectMapper mapper = getObjectMapper();
			HttpURLConnection connection = getPostURLConnection(uri);
			mapper.writeValue(connection.getOutputStream(), in);
			int code = connection.getResponseCode();
			if (code >= 400) {
				handleError(connection, code);
			}
			return mapper.readerForListOf(OperationVariable.class).readValue(connection.getInputStream());
		} catch (IOException | URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void handleError(HttpURLConnection connection, int code) throws IOException {
		try (InputStream in = connection.getErrorStream();
				InputStreamReader iReader = new InputStreamReader(in, StandardCharsets.UTF_8);
				BufferedReader bReader = new BufferedReader(iReader)) {
			StringBuilder builder = new StringBuilder();
			bReader.lines().forEach(builder::append);
			throw new RuntimeException("Error code " + code + ":" + builder.toString());
		}
	}

	private ObjectMapper getObjectMapper() {
		SimpleAbstractTypeResolver typeResolver = new SimpleAbstractTypeResolverFactory().create();
		JsonMapperFactory jsonMapperFactory = new JsonMapperFactory();
		return jsonMapperFactory.create(typeResolver);
	}

	private HttpURLConnection getPostURLConnection(String uri) throws IOException, URISyntaxException {
		URL url = new URI(uri).toURL();
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json; utf-8");
		connection.setRequestProperty("Accept", "application/json");
		connection.setDoOutput(true);
		return connection;
	}
}