# 🛡️ Gatekeeper

**Gatekeeper** is a framework-level plugin for Spigot servers that provides unified **REST API registration**, **JWT-based authentication**, and **namespace isolation** for other plugins.

> 📡 It acts as an API gateway, allowing other plugins to register routes in isolated namespaces with centralized authentication and no path conflicts.

---

## ✨ Features

* 🔐 JWT-based login authentication
* 🔌 Plugin-provided REST APIs via registration
* 📁 Namespaced routing for isolation and safety
* 🧩 Decoupled plugin integration via `ApiGateway` interface
* 🗣️ Built-in administrative API endpoints (e.g., `/broadcast`, `/health`)

---

## 🚀 Quick Start

### 1. Register API routes from another plugin

```java
public class PluginA extends JavaPlugin {
    @Override
    public void onEnable() {
        ApiGateway api = getServer().getServicesManager().load(ApiGateway.class);
        api.registerPluginRoutes(this, routes -> {
            routes.get("/ping", req -> ApiResponse.json(200, Map.of("pong", true)));
        });
    }
}
```

Registered routes will be exposed as:

```
GET /api/plugin-a/ping
```

---

## 🔐 Authentication

### Login

```
POST /auth/login
```

#### Request body (JSON or form):

```json
{
  "username": "admin",
  "password": "123456"
}
```

> ⚠️ For now, credentials are **hardcoded** for development/testing only:
>
> * Username: `admin`
> * Password: `123456`

#### Response:

```json
{
  "token": "your-jwt-token"
}
```

---

### Using JWT to call protected APIs

Add the following HTTP header to access `/api/...` routes:

```
Authorization: Bearer <your-token>
```

---

## 📢 Built-in Endpoints

### Broadcast a server-wide message

```
POST /api/gatekeeper/broadcast
```

#### Body:

```json
{
  "message": "&6[Notice] &eServer will restart in 10 minutes",
  "permission": "",
  "type": "chat" // "chat", "title", or "actionbar"
}
```

---

### Health check

```
GET /api/gatekeeper/health
```

Returns a simple status to verify that the plugin and HTTP gateway are running:

```json
{
  "status": "ok",
  "uptime": 12345,
  "onlinePlayers": 8
}
```

---

## 🔧 Requirements

* Minecraft Server: Spigot 1.16+
* Java: 17+
* Embedded Web Framework: [Javalin](https://javalin.io/)
* JWT: [auth0/java-jwt](https://github.com/auth0/java-jwt)

---

## 📌 Route Structure

| Path                          | Description                      |
| ----------------------------- | -------------------------------- |
| `/auth/login`                 | Login to receive a JWT token     |
| `/api/<plugin-namespace>/...` | Plugin-provided protected routes |
| `/api/gatekeeper/broadcast`   | Send a server-wide message       |
| `/api/gatekeeper/health`      | Health check endpoint            |

---

## 🧪 In Development

* [ ] Multi-user account system
* [ ] Bukkit permission integration
* [ ] Swagger/OpenAPI generation
* [ ] Plugin-to-plugin API auth hook

---

## 📄 License

MIT License — free to use, modify, and integrate.
