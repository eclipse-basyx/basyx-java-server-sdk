# JWT Validator - Nutzungsanleitung

## 🚀 Schnellstart

### 1. Konfiguration

Füge in deine `application.properties` hinzu:

```properties
# Keycloak (Standard BaSyx Setup)
basyx.jwt.wellknown.url=http://localhost:8080/realms/BaSyx/.well-known/openid_configuration
basyx.jwt.issuer=http://localhost:8080/realms/BaSyx

# Oder für Auth0
# basyx.jwt.wellknown.url=https://YOUR_DOMAIN.auth0.com/.well-known/openid_configuration

# Oder für Google
# basyx.jwt.wellknown.url=https://accounts.google.com/.well-known/openid_configuration
```

### 2. Grundlegende Verwendung im Controller

```java
@RestController
public class MeinController {
    
    @Autowired
    private HackedJwtValidator jwtValidator;
    
    @Autowired
    private HttpServletRequest request;
    
    @GetMapping("/secured-endpoint")
    public ResponseEntity<?> securedEndpoint() {
        
        // 1. JWT Token aus Header extrahieren
        String authHeader = request.getHeader("Authorization");
        String token = jwtValidator.extractTokenFromHeader(authHeader);
        
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("JWT token required");
        }
        
        // 2. JWT Signatur validieren
        HackedJwtValidator.JwtValidationResult validation = 
            jwtValidator.validateJwtSignature(token);
        
        if (!validation.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid JWT: " + validation.getErrorMessage());
        }
        
        // 3. Geschäftslogik ausführen
        String user = validation.getSubject();
        String issuer = validation.getIssuer();
        
        return ResponseEntity.ok("Hello " + user + " from " + issuer);
    }
}
```

## 📡 API Endpunkte Testen

### Mit curl:

```bash
# 1. JWT Token von Keycloak holen
TOKEN=$(curl -s -X POST "http://localhost:8080/realms/BaSyx/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin" \
  -d "password=admin" \
  -d "grant_type=password" \
  -d "client_id=basyx-client" \
  | jq -r '.access_token')

# 2. Geschützten Endpoint aufrufen
curl -X GET "http://localhost:8090/api/v3.0/submodel-repository/secured/submodels/aHR0cHM6Ly9leGFtcGxlLmNvbS9pZHMvc20=" \
  -H "Authorization: Bearer $TOKEN"

# 3. JWT Info anzeigen (Debug)
curl -X GET "http://localhost:8090/api/v3.0/submodel-repository/secured/jwt-info" \
  -H "Authorization: Bearer $TOKEN"
```

### Mit Postman:

1. **Authorization Tab**: 
   - Type: "Bearer Token"
   - Token: Dein JWT Token

2. **Headers**:
   ```
   Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

## 🛠️ Erweiterte Verwendung

### Hilfsmethode für wiederverwendbare Validierung:

```java
@Component
public class JwtSecurityHelper {
    
    @Autowired
    private HackedJwtValidator jwtValidator;
    
    @Autowired
    private HttpServletRequest request;
    
    public JwtValidationResult validateCurrentRequest() {
        String authHeader = request.getHeader("Authorization");
        String token = jwtValidator.extractTokenFromHeader(authHeader);
        
        if (token == null) {
            return JwtValidationResult.invalid("No JWT token provided");
        }
        
        return jwtValidator.validateJwtSignature(token);
    }
    
    public String getCurrentUser() {
        JwtValidationResult validation = validateCurrentRequest();
        return validation.isValid() ? validation.getSubject() : null;
    }
}
```

### Verwendung in Service-Klassen:

```java
@Service
public class SecuredSubmodelService {
    
    @Autowired
    private JwtSecurityHelper securityHelper;
    
    @Autowired
    private SubmodelRepository repository;
    
    public Submodel getSubmodelSecurely(String id) {
        JwtValidationResult validation = securityHelper.validateCurrentRequest();
        
        if (!validation.isValid()) {
            throw new SecurityException("Invalid JWT: " + validation.getErrorMessage());
        }
        
        // Log who accessed what
        logger.info("User {} accessing submodel {}", validation.getSubject(), id);
        
        return repository.getSubmodel(id);
    }
}
```

## 🎯 Praktische Beispiele

### 1. Einfacher GET Endpoint:

```java
@GetMapping("/my-submodels")
public ResponseEntity<?> getMySubmodels() {
    // JWT validieren
    JwtValidationResult validation = validateJwtFromRequest();
    if (!validation.isValid()) {
        return unauthorized(validation.getErrorMessage());
    }
    
    // Nur Submodels des aktuellen Users zurückgeben
    String userId = validation.getSubject();
    List<Submodel> userSubmodels = repository.getSubmodelsByOwner(userId);
    
    return ResponseEntity.ok(userSubmodels);
}
```

### 2. POST mit Berechtigungsprüfung:

```java
@PostMapping("/submodels")
public ResponseEntity<?> createSubmodel(@RequestBody Submodel submodel) {
    // JWT validieren
    JwtValidationResult validation = validateJwtFromRequest();
    if (!validation.isValid()) {
        return unauthorized(validation.getErrorMessage());
    }
    
    // Prüfen ob User Schreibrechte hat
    if (!hasCreatePermission(validation.getSubject())) {
        return forbidden("Insufficient permissions");
    }
    
    // Owner setzen
    submodel.setCreatedBy(validation.getSubject());
    repository.createSubmodel(submodel);
    
    return ResponseEntity.status(HttpStatus.CREATED).body(submodel);
}
```

### 3. Admin-only Endpoint:

```java
@DeleteMapping("/admin/submodels/{id}")
public ResponseEntity<?> adminDeleteSubmodel(@PathVariable String id) {
    JwtValidationResult validation = validateJwtFromRequest();
    if (!validation.isValid()) {
        return unauthorized(validation.getErrorMessage());
    }
    
    // Admin-Rolle prüfen (vereinfacht)
    if (!validation.getSubject().contains("admin")) {
        return forbidden("Admin access required");
    }
    
    repository.deleteSubmodel(id);
    return ResponseEntity.noContent().build();
}
```

## 🔍 Debugging & Troubleshooting

### JWT Info Endpoint nutzen:

```bash
curl -X GET "http://localhost:8090/api/v3.0/submodel-repository/secured/jwt-info" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

Response:
```json
{
  "hasToken": true,
  "valid": true,
  "subject": "admin",
  "issuer": "http://localhost:8080/realms/BaSyx",
  "expiration": 1671234567
}
```

### Häufige Probleme:

1. **"Token is missing"**: Authorization Header fehlt oder falsch formatiert
2. **"Invalid signature"**: Falscher Public Key oder Token manipuliert
3. **"Token expired"**: Token ist abgelaufen
4. **"Invalid issuer"**: Issuer im Token stimmt nicht mit Konfiguration überein

### Logs aktivieren:

```properties
logging.level.org.eclipse.digitaltwin.basyx.submodelrepository.http.jwt=DEBUG
```

## 🚨 Sicherheitshinweise

1. **HTTPS verwenden**: Niemals JWT über unverschlüsselte Verbindungen
2. **Token Lebensdauer begrenzen**: Kurze Expiration Times verwenden
3. **Proper Error Handling**: Keine sensitive Informationen in Fehlermeldungen
4. **Rate Limiting**: Implementiere Rate Limiting für Auth-Endpunkte
5. **Input Validation**: Alle Eingaben validieren, auch bei gültigen JWTs

## 🎪 Vollständiges Beispiel

Siehe `SecuredSubmodelController.java` für ein komplettes Beispiel mit verschiedenen Endpunkt-Typen und Sicherheitsmustern.
