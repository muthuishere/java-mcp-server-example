package tools.muthuishere.todo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ResourceMetadataController {

    @Value("${mcp.authorization.server.url}")
    private String authServerUrl;

    @Value("${mcp.authorization.server.authorize-url}")
    private String authorizeUrl;

    @Value("${mcp.authorization.server.token-url}")
    private String tokenUrl;

    @Value("${mcp.resource.server.base-path}")
    private String resourceBasePath;

    /**
     * OAuth 2.0 Protected Resource Metadata endpoint
     * As per RFC 8707: https://tools.ietf.org/html/rfc8707
     */
    @GetMapping("/.well-known/oauth-protected-resource/mcp/")
    public Map<String, Object> getResourceMetadata() {
        Map<String, Object> metadata = new java.util.HashMap<>();
        metadata.put("resource_name", "Todo MCP Server");
        metadata.put("resource", resourceBasePath);
        metadata.put("authorization_servers", new String[]{authServerUrl});
        metadata.put("bearer_methods_supported", new String[]{"header"});
        metadata.put("scopes_supported", new String[]{"read:email"});
        return metadata;
    }

    /**
     * Fallback OAuth 2.0 Protected Resource Metadata endpoint (without /mcp/)
     * Required for MCP Inspector discovery
     */
    @GetMapping(value = "/.well-known/oauth-protected-resource", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getGenericResourceMetadata() {
        // Return the same metadata as the MCP-specific endpoint
        return ResponseEntity.ok(getResourceMetadata());
    }

    /**
     * OAuth 2.0 Authorization Server Metadata endpoint
     * As per RFC 8414: https://tools.ietf.org/html/rfc8414
     */
    @GetMapping(value = "/.well-known/oauth-authorization-server", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> authorizationServerMetadata() {
        Map<String, Object> metadata = new java.util.HashMap<>();
        metadata.put("issuer", authServerUrl);
        metadata.put("authorization_endpoint", authServerUrl + "/oauth2/authorize");
        metadata.put("token_endpoint", authServerUrl + "/oauth2/token");
        metadata.put("response_types_supported", new String[]{"code"});
        metadata.put("grant_types_supported", new String[]{"authorization_code"});
        metadata.put("scopes_supported", new String[]{"read:email"});
        metadata.put("token_endpoint_auth_methods_supported", new String[]{"client_secret_basic", "client_secret_post", "none"});
        metadata.put("registration_endpoint", authServerUrl + "/oauth2/register");
        
        return ResponseEntity.ok(metadata);
    }

    /**
     * OpenID Connect Discovery endpoint
     * As per OpenID Connect Discovery spec
     */
    @GetMapping(value = "/.well-known/openid-configuration", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> openidConfiguration() {
        Map<String, Object> config = new java.util.HashMap<>();
        config.put("issuer", authServerUrl);
        config.put("authorization_endpoint", authServerUrl + "/oauth2/authorize");
        config.put("token_endpoint", authServerUrl + "/oauth2/token");
        config.put("response_types_supported", new String[]{"code"});
        config.put("subject_types_supported", new String[]{"public"});
        config.put("id_token_signing_alg_values_supported", new String[]{"RS256"});
        config.put("scopes_supported", new String[]{"openid", "email", "profile", "read:email"});
        config.put("claims_supported", new String[]{"sub", "email", "email_verified"});
        config.put("registration_endpoint", authServerUrl + "/oauth2/register");
        
        return ResponseEntity.ok(config);
    }

    /**
     * OAuth login redirect endpoint for MCP clients and vs code
     */
    @GetMapping(value = "/login/oauth", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> loginOauth() {
        return ResponseEntity.status(302)
            .header("Location", authServerUrl + "/login/oauth")
            .body(Map.of(
                "authorization_url", authServerUrl + "/oauth2/authorize",
                "token_url", authServerUrl + "/oauth2/token",
                "redirect_url", authServerUrl + "/login/oauth"
            ));
    }
}