# Lösung für "Request Header too large" bei JWT Tokens

## 🚨 Problem
JWT Tokens können sehr groß werden (> 8KB), besonders bei:
- Vielen Rollen/Claims
- Verschachtelten Gruppenstrukturen  
- Zusätzlichen Custom Claims
- Keycloak mit vielen Realm-Rollen

Standard HTTP Header Limit: **8KB**

## ✅ Lösungsansätze

### 1. Server-Konfiguration erweitern

#### **application.properties:**
```properties
# Tomcat HTTP Header Size (Standard)
server.tomcat.max-http-header-size=32KB
server.max-http-header-size=32KB

# Jetty (Alternative)
server.jetty.max-http-header-size=32KB

# Undertow (Alternative)
server.undertow.max-header-size=32KB
server.undertow.max-headers=200

# JWT spezifische Limits
basyx.jwt.max-token-size=8192
basyx.jwt.allow-cookie-auth=true
basyx.jwt.allow-query-param-auth=false
```

### 2. Alternative Token-Übertragung

#### **A) Cookie-basiert (empfohlen für große Tokens):**
```javascript
// Frontend: Token in Cookie setzen
document.cookie = `jwt-token=${jwtToken}; Secure; HttpOnly; SameSite=Strict`;

// Oder per JavaScript fetch
fetch('/api/v3.0/submodel-repository/secured/submodels/123', {
    credentials: 'include',  // Cookies mitschicken
    headers: {
        'Content-Type': 'application/json'
    }
});
```

#### **B) Query Parameter (nur für Development):**
```bash
# Nur wenn aktiviert: basyx.jwt.allow-query-param-auth=true
curl "http://localhost:8090/api/v3.0/submodel-repository/secured/submodels/123?token=eyJ..."
```

### 3. JWT Optimierung

#### **Keycloak Token optimieren:**
```json
// Keycloak Client Settings
{
  "mappers": [
    {
      "name": "remove-unnecessary-claims",
      "protocol": "openid-connect",
      "protocolMapper": "oidc-hardcoded-claim-mapper",
      "config": {
        "claim.value": "",
        "userinfo.token.claim": "false",
        "id.token.claim": "false",
        "access.token.claim": "true"
      }
    }
  ]
}
```

#### **Minimale Claims verwenden:**
- Nur notwendige Rollen einbeziehen
- Custom Claims reduzieren
- Audience (`aud`) spezifisch setzen
- Kurze Issuer URLs verwenden

## 🔧 Praktische Implementierung

### **1. Multi-Source Token Extraktion:**
```java
// Automatische Fallback-Mechanismen
String token = jwtValidator.extractTokenFromRequest(request);
// Versucht: Header → Cookie → Query Parameter
```

### **2. Debug Endpoint:**
```bash
# Token-Größe testen
curl -X GET "http://localhost:8090/api/v3.0/submodel-repository/secured/token-size-test" \
  -H "Authorization: Bearer $TOKEN"
```

Response:
```json
{
  "tokenFound": true,
  "tokenLength": 2847,
  "tokenSource": "Authorization header",
  "authHeaderLength": 2854,
  "tokenParts": 3,
  "headerLength": 156,
  "payloadLength": 1234,
  "signatureLength": 342,
  "maxHeaderSize": "32KB (configured)",
  "recommendedTokenSize": "< 8KB"
}
```

### **3. Client-seitige Lösung:**
```javascript
// Automatische Token-Größen-Behandlung
function makeAuthenticatedRequest(url, token) {
    const tokenSize = token.length;
    
    if (tokenSize > 6000) {  // Nahe Header-Limit
        // Cookie verwenden
        document.cookie = `jwt-token=${token}; Secure; SameSite=Strict`;
        return fetch(url, { credentials: 'include' });
    } else {
        // Standard Authorization Header
        return fetch(url, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
    }
}
```

## 🛠️ Troubleshooting

### **Problem diagnostizieren:**
```bash
# 1. Token-Größe prüfen
echo $TOKEN | wc -c

# 2. Header-Größe testen
curl -v -H "Authorization: Bearer $TOKEN" http://localhost:8090/health

# 3. Debug-Endpoint nutzen
curl "http://localhost:8090/api/v3.0/submodel-repository/secured/token-size-test" \
  -H "Authorization: Bearer $TOKEN"
```

### **Häufige Fehler:**
- **414 URI Too Long**: Query Parameter zu groß
- **431 Request Header Fields Too Large**: Authorization Header zu groß  
- **413 Payload Too Large**: Body zu groß (nicht JWT-related)

### **Server Logs:**
```properties
# Detaillierte HTTP Logs
logging.level.org.springframework.web=DEBUG
logging.level.org.apache.tomcat.util.http=DEBUG
logging.level.org.eclipse.digitaltwin.basyx.jwt=DEBUG
```

## 🎯 Best Practices

1. **Token-Größe begrenzen**: < 8KB für Header-Kompatibilität
2. **Cookie für große Tokens**: Automatischer Fallback
3. **Claims minimieren**: Nur notwendige Informationen
4. **Header-Limits erhöhen**: Server-seitig konfigurieren
5. **Monitoring**: Token-Größen überwachen
6. **Caching**: Public Keys cachen für Performance

## 🔐 Sicherheitshinweise

- **Cookies**: `Secure`, `HttpOnly`, `SameSite=Strict` verwenden
- **Query Parameters**: Nur in Development, niemals in Production
- **HTTPS**: Immer verschlüsselte Verbindungen
- **Token Rotation**: Kurze Lebensdauer für große Tokens
- **Logging**: Keine Tokens in Logs schreiben
