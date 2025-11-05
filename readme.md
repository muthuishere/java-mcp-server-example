# Todo MCP Server - Spring Boot Implementation

A complete **Model Context Protocol (MCP) Server** implementation using Spring Boot that demonstrates OAuth2 authentication with Firebase through a proxy server. This server provides Todo management tools that can be used by MCP clients like VS Code, Claude Desktop, and other AI assistants.



## 🚀 Features

- **MCP Server Implementation**: Full OAuth2-protected MCP server using Spring AI MCP framework
- **Firebase Authentication**: Secure JWT-based authentication via Firebase Auth Server Proxy
- **Todo Management**: Complete CRUD operations for todo items with user isolation
- **Multiple Profiles**: Support for different MCP communication modes (SSE, Streamable, Stateless)
- **OAuth2 Discovery**: Full RFC-compliant OAuth2 metadata endpoints
- **User Context**: Automatic user isolation based on authenticated email
- **Security**: Comprehensive security configuration with proper error handling

## 🛠️ Tech Stack

- **Java 21** with Spring Boot 3.5.7
- **Spring Security** with OAuth2 Resource Server
- **Spring AI MCP Server** framework
- **Firebase Admin SDK** for JWT validation


## 📋 Prerequisites

Before running this MCP server, you need:

1. **Firebase Auth Server Proxy** running on port 9000
   - follow the instructions: https://github.com/muthuishere/mcp-firebase-auth-server
 

2. **Environment Variables** (see Configuration section)

3. **Java 17** or higher installed

## ⚙️ Configuration

### Environment Variables

Create a `.env` file or set these environment variables:

```bash
# Firebase Configuration (same as your auth proxy)
FIREBASE_PROJECT_ID=your-firebase-project-id
FIREBASE_API_KEY=your-firebase-api-key
FIREBASE_SERVICE_ACCOUNT_KEY={"type":"service_account",...} # Your service account JSON

# MCP Server Configuration
MCP_SERVER_BASE_URL=http://localhost:8080
MCP_AUTH_SERVER_URL=http://localhost:9000
```

### Application Profiles

The server supports multiple communication profiles:

- **`stdio`** (default): Standard input/output for MCP protocol
- **`sse`**: Server-Sent Events for real-time communication
- **`streamable`**: Streaming responses
- **`stateless`**: Stateless operation mode

## 🏃‍♂️ Running the Server

### 1. Standard Run 
```bash
task dev
```



## 🔧 MCP Tools Available

This server exposes the following MCP tools that AI assistants can use:

### `fetch-all-todos`
- **Description**: Gets all Todo items for the authenticated user
- **Parameters**: None
- **Returns**: List of Todo objects

### `fetch-todo-by-id`
- **Description**: Gets a specific Todo item by ID
- **Parameters**: `id` (Long) - ID of the todo item
- **Returns**: Optional Todo object

### `make-todo`
- **Description**: Creates a new Todo item
- **Parameters**:
  - `title` (String) - Title for the Todo
  - `description` (String) - Description for the Todo  
  - `completed` (boolean) - Is the Todo completed?
- **Returns**: TodoToolResponse with created todo and success message

### `change-todo`
- **Description**: Updates an existing Todo item
- **Parameters**:
  - `id` (Long) - ID of the todo to update
  - `title` (String) - New title
  - `description` (String) - New description
  - `completed` (boolean) - New completion status
- **Returns**: Optional updated Todo object

### `remove-todo`
- **Description**: Deletes a Todo item by ID
- **Parameters**: `id` (Long) - ID of the todo to delete
- **Returns**: boolean indicating success

## 🔐 Authentication Flow

### 1. OAuth2 Discovery Endpoints

The server exposes these discovery endpoints for MCP clients:

```http
GET /.well-known/oauth-protected-resource
GET /.well-known/oauth-authorization-server  
GET /.well-known/openid-configuration
GET /login/oauth
```

### 2. Token Validation Process

1. **Extract JWT**: From `Authorization: Bearer <token>` header
2. **Parse JWT**: Extract `firebase_token` claim from JWT payload
3. **Validate Firebase Token**: Using Firebase Admin SDK
4. **Set User Context**: Store user email in `MCPContextHolder`
5. **Access Control**: All todos are filtered by authenticated user email

### 3. Error Handling

Returns proper `WWW-Authenticate` headers for authentication failures:

```http
HTTP/1.1 401 Unauthorized
WWW-Authenticate: Bearer error="invalid_token", 
                  error_description="The access token provided is expired, revoked, malformed, or invalid",
                  resource_metadata="http://localhost:8080/.well-known/oauth-protected-resource/mcp/"
```

## 🗄️ Data Model

### Todo Entity
```java
@Entity
public class Todo {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private String email;        // User isolation
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

## 🧪 Testing

### 1. Run Tests
```bash
./gradlew test
```

### 2. Test OAuth Discovery
```bash
# Test resource metadata
curl http://localhost:8080/.well-known/oauth-protected-resource

# Test auth server metadata  
curl http://localhost:8080/.well-known/oauth-authorization-server

# Test protected endpoint (should return 401)
curl -v http://localhost:8080/mcp
```



## 🔗 Integration with MCP Clients

### VS Code Configuration
1. Install MCP extension
2. Configure server URL: `http://localhost:8080/mcp`
3. The OAuth flow will automatically redirect to Firebase authentication



## 📁 Project Structure

```
src/main/java/tools/muthuishere/todo/
├── TodoApplication.java              # Main Spring Boot application
├── config/
│   ├── FirebaseConfig.java          # Firebase configuration
│   ├── FirebaseJwtDecoder.java      # JWT token decoder
│   └── SecurityConfig.java          # Security configuration
├── controller/
│   └── ResourceMetadataController.java # OAuth2 discovery endpoints
├── security/
│   └── McpAuthenticationEntryPoint.java # Auth error handling
├── todo/
│   ├── TodoTools.java               # MCP tool implementations
│   ├── TodoService.java             # Business logic
│   ├── TodoRepository.java          # Data access
│   ├── MCPContextHolder.java        # User context management
│   └── model/
│       ├── Todo.java                # Todo entity
│       └── TodoToolResponse.java    # Tool response model
└── utils/
    └── Sampling.java                # Utility classes
```

## 🔄 Development Workflow

1. **Start Firebase Auth Proxy**: Ensure the auth server is running on port 9000
2. **Set Environment Variables**: Configure Firebase credentials
3. **Run MCP Server**: Use `./gradlew bootRun` or specific profile tasks
4. **Test Integration**: Use MCP Inspector or direct API calls
5. **Connect MCP Client**: Configure VS Code

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Note**: This is a reference implementation demonstrating MCP server development with OAuth2 authentication. Adapt the configuration and business logic for your specific use case.