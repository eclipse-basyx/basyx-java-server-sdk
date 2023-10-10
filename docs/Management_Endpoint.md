# Configuring the Spring Management Endpoint
The Spring Management Endpoint (including the health endpoint) options can be configured under [application.properties](./basyx.aasrepository.component/src/main/resources/application.properties) with the following attributes: (As seen on https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints.cors)

* Allowed Origins:<br>
Comma-separated list of origin patterns to allow. Unlike allowed origins which only supports '*', origin patterns are more flexible (for example 'https://*.example.com') and can be used when credentials are allowed. When no allowed origin patterns or allowed origins are set, CORS support is disabled.
  ```
  management.endpoints.web.cors.allowed-origins=https://example.com
  ```
* Allowed Origin Patterns:<br>
Comma-separated list of origin patterns to allow. Unlike allowed origins which only supports '*', origin patterns are more flexible (for example 'https://*.example.com') and can be used when credentials are allowed. When no allowed origin patterns or allowed origins are set, CORS support is disabled.
  ```
  management.endpoints.web.cors.allowed-origin-patterns=https://*.example.com
  ```
* Allowed Methods:<br>
Comma-separated list of methods to allow. '*' allows all methods. When not set, defaults to GET.
  ```
  management.endpoints.web.cors.allowed-methods=
  ```
* Allowed Headers:<br>
Comma-separated list of headers to allow in a request. '*' allows all headers.
  ```
  management.endpoints.web.cors.allowed-headers=
  ```
* Exposed Headers:<br>
Comma-separated list of headers to include in a response.
  ```
  management.endpoints.web.cors.exposed-headers=
  ```

* Allow Credentials: <br>
Boolean, whether credentials are supported. When not set, credentials are not supported.
  ```
  management.endpoints.web.cors.allow-credentials=
  ```
* Max Age:<br>
Number, how long in seconds the response from a pre-flight request can be cached by clients.
  ```
  management.endpoints.web.cors.max-age=
  ```
