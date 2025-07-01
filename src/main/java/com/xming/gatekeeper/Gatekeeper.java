package com.xming.gatekeeper;

import com.xming.gatekeeper.api.ApiGateway;
import com.xming.gatekeeper.api.ApiResponse;
import com.xming.gatekeeper.jwt.JwtManager;
import com.xming.gatekeeper.web.ApiGatewayImpl;
import io.javalin.Javalin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Set;

public final class Gatekeeper extends JavaPlugin {
    private Javalin app;
    private ApiGatewayImpl gateway;

    @Override
    public void onEnable() {
        // Plugin startup logic
        app = Javalin.create().start(8080);
        JwtManager jwt = new JwtManager("secret-key");
        this.gateway = new ApiGatewayImpl(app, jwt);

        getServer().getServicesManager().register(ApiGateway.class, gateway, this, ServicePriority.High);

        app.post("/auth/login", ctx -> {
            Map<String, Object> data = ctx.bodyAsClass(Map.class);
            String username = (String) data.get("username");
            String password = (String) data.get("password");

            // 暂时硬编码账号密码
            if ("admin".equals(username) && "123456".equals(password)) {
                String token = jwt.issueToken("admin", Set.of("admin"));
                ctx.json(Map.of("token", token));
            } else {
                System.out.println("Login failed for user: " + username + ", password: " + password);
                ctx.status(401).json(Map.of("error", "Invalid credentials"));
            }
        });

        registerPluginRoutes();
    }

    private void registerPluginRoutes() {
        this.gateway.registerPluginRoutes(this, builder -> {
            builder.get(
                    "/ping",
                    request -> ApiResponse.json(200, Map.of("message", "pong"))
            );
            builder.post(
                    "/broadcast",
                    request -> {
                        Map<String, Object> data = request.getJsonBody();
                        String message = (String) data.get("message");
                        Bukkit.getServer().broadcastMessage(message);
                        return ApiResponse.json(200, Map.of("status", "success"));
                    }
            );
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        app.stop();
    }
}
