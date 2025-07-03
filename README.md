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
        api.registerPluginRoutes(this, builder -> {
            builder.get("/ping", request -> ApiResponse.json(200, Map.of("message", "pong")));
        });
    }
}
```

Registered routes will be exposed as:

```
GET /api/plugina/ping
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

## Websocket

Register WebSocket endpoint in your plugin:

```java
public class PluginA extends JavaPlugin {
    @Override
    public void onEnable() {
        gateway.registerWebSocketRoutes(plugin, builder -> {
            builder.on("/message", new WebSocketHandler() {
                @Override
                public void onConnect(WsSession session) {

                }

                @Override
                public void onMessage(WsSession session, String message) {

                }

                @Override
                public void onClose(WsSession session, int status, String reason) {

                }

                @Override
                public void onError(WsSession session, Throwable error) {

                }
            });
        });
    }
}
```

Registered WebSocket routes will be exposed as:

```
ws /ws/plugina/message
```

And Gatekeeper will managing sessions for you

```java
public class SomeClass {
    @Override
    public void anyFunction() {
        WsHub hub = gateway.getWsHub();
        hub.broadcast("plguina", "/message", message);
    }
}
```

Gatekeeper will also handle authentication for WebSocket connections. 
Here is an example of how to authenticate in python:

```python
import json
import asyncio
import requests
import websockets

def get_token():
    url = 'http://127.0.0.1:8080/auth/login'
    data = {
        'username': 'admin',
        'password': 'yourpasswordhere'
    }
    response = requests.post(url, json=data)
    if response.status_code == 200:
        return response.json().get('token')
    else:
        raise Exception("Failed to get token")

async def hello():
    token = get_token()
    uri = "ws://127.0.0.1:8080/ws/gatekeeper/logger"
    async with websockets.connect(uri) as websocket:
        auth_data = {
            "type": "auth",
            "token": token
        }
        await websocket.send(json.dumps(auth_data))
        print("Authentication sent")
        # {"type": "auth_ok"}
        while True:
            response = await websocket.recv()
            print(response, end = '')

asyncio.run(hello())
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
  "message": "§6[Notice] §eServer will restart in 10 minutes"
}
```

---

### Health check

```
GET /health
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

### Console Log Websocket
```http
ws /ws/gatekeeper/logger
```

Real time access to the server console logs via WebSocket.

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
* [ ] Swagger/OpenAPI generation
* [ ] Plugin-to-plugin API auth hook

---

## 📄 License

MIT License — free to use, modify, and integrate.
