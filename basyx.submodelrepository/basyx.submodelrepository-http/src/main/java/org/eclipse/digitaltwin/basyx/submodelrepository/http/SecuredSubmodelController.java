/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.submodelrepository.http;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.jwt.HackedJwtValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Secured endpoints demonstrating JWT signature validation.
 * This controller shows how to use the HackedJwtValidator in practice.
 * 
 * @author GitHub Copilot
 */
@RestController
@RequestMapping("/api/v3.0/submodel-repository/secured")
public class SecuredSubmodelController {

    private static final Logger logger = LoggerFactory.getLogger(SecuredSubmodelController.class);
    
    private final SubmodelRepository repository;
    private final HackedJwtValidator jwtValidator;
    private final HttpServletRequest request;

    @Autowired
    public SecuredSubmodelController(SubmodelRepository repository, 
                                   HackedJwtValidator jwtValidator, 
                                   HttpServletRequest request) {
        this.repository = repository;
        this.jwtValidator = jwtValidator;
        this.request = request;
    }

    /**
     * Secured endpoint to get a submodel by ID with JWT validation.
     * This demonstrates the usage pattern with header size handling.
     * 
     * Supports multiple token sources:
     * 1. Authorization: Bearer <token>
     * 2. Cookie: jwt-token=<token>
     * 3. Query parameter: ?token=<token> (if enabled)
     * 
     * Example usage:
     * GET /api/v3.0/submodel-repository/secured/submodels/{submodelIdentifier}
     * Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
     * 
     * Or for large tokens:
     * GET /api/v3.0/submodel-repository/secured/submodels/{submodelIdentifier}
     * Cookie: jwt-token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
     */
    @GetMapping("/submodels/{submodelIdentifier}")
    public ResponseEntity<?> getSecuredSubmodel(
            @PathVariable("submodelIdentifier") Base64UrlEncodedIdentifier submodelIdentifier) {
        
        // 1. Extract JWT token from various sources (handles large tokens)
        String token = jwtValidator.extractTokenFromRequest(request);
        
        if (token == null) {
            logger.warn("No JWT token provided in any supported location");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "JWT token required", 
                                "message", "Provide token via Authorization header, Cookie, or query parameter",
                                "supportedMethods", getSupportedTokenMethods()));
        }
        
        // 2. Validate JWT signature
        HackedJwtValidator.JwtValidationResult validation = jwtValidator.validateJwtSignature(token);
        
        if (!validation.isValid()) {
            logger.warn("JWT validation failed: {}", validation.getErrorMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid JWT token", 
                                "message", validation.getErrorMessage()));
        }
        
        // 3. JWT is valid, proceed with business logic
        logger.info("JWT validated successfully for user: {} from issuer: {}", 
                   validation.getSubject(), validation.getIssuer());
        
        try {
            Submodel submodel = repository.getSubmodel(submodelIdentifier.getIdentifier());
            
            // Optional: Add JWT info to response headers for debugging
            return ResponseEntity.ok()
                    .header("X-JWT-Subject", validation.getSubject())
                    .header("X-JWT-Issuer", validation.getIssuer())
                    .body(submodel);
                    
        } catch (Exception e) {
            logger.error("Error retrieving submodel: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Submodel not found", 
                                "message", e.getMessage()));
        }
    }
    
    // Helper methods
    
    private HackedJwtValidator.JwtValidationResult validateJwtFromRequest() {
        String authHeader = request.getHeader("Authorization");
        String token = jwtValidator.extractTokenFromHeader(authHeader);
        
        if (token == null) {
            return HackedJwtValidator.JwtValidationResult.invalid("No JWT token provided");
        }
        
        return jwtValidator.validateJwtSignature(token);
    }
    
    private ResponseEntity<?> createUnauthorizedResponse(String message) {
        logger.warn("Unauthorized access attempt: {}", message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Authentication failed", 
                            "message", message));
    }
    
    private boolean hasWritePermission(String subject) {
        // Simplified permission check - in reality you'd check against 
        // a permission system, database, or JWT roles
        return subject != null && !subject.isEmpty();
    }
    
    private boolean hasAdminRole(String subject) {
        // Simplified admin check - in reality you'd check JWT roles/claims
        // or external permission system
        return subject != null && subject.contains("admin");
    }
    
    private Map<String, Object> getSupportedTokenMethods() {
        Map<String, Object> methods = new HashMap<>();
        methods.put("authorizationHeader", true);
        methods.put("cookie", jwtValidator != null); // Simplified check
        methods.put("queryParameter", false); // Set based on configuration
        methods.put("cookieName", "jwt-token");
        methods.put("queryParamName", "token");
        methods.put("maxTokenSize", 8192);
        return methods;
    }
    
    /**
     * Endpoint to test large token handling and provide troubleshooting info.
     * GET /api/v3.0/submodel-repository/secured/token-size-test
     */
    @GetMapping("/token-size-test")
    public ResponseEntity<?> tokenSizeTest() {
        Map<String, Object> response = new HashMap<>();
        
        // Check Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            response.put("authHeaderLength", authHeader.length());
            response.put("authHeaderPreview", authHeader.length() > 50 ? 
                authHeader.substring(0, 50) + "..." : authHeader);
        } else {
            response.put("authHeader", "not present");
        }
        
        // Check for token via alternative methods
        String token = jwtValidator.extractTokenFromRequest(request);
        if (token != null) {
            response.put("tokenFound", true);
            response.put("tokenLength", token.length());
            response.put("tokenSource", getTokenSource(request));
            
            // Basic token structure check
            String[] parts = token.split("\\.");
            response.put("tokenParts", parts.length);
            if (parts.length == 3) {
                response.put("headerLength", parts[0].length());
                response.put("payloadLength", parts[1].length());
                response.put("signatureLength", parts[2].length());
            }
        } else {
            response.put("tokenFound", false);
        }
        
        response.put("supportedMethods", getSupportedTokenMethods());
        response.put("maxHeaderSize", "32KB (configured)");
        response.put("recommendedTokenSize", "< 8KB");
        
        return ResponseEntity.ok(response);
    }
    
    private String getTokenSource(jakarta.servlet.http.HttpServletRequest request) {
        if (jwtValidator.extractTokenFromHeader(request.getHeader("Authorization")) != null) {
            return "Authorization header";
        }
        // Add other checks for cookie and query param
        return "unknown";
    }
}
