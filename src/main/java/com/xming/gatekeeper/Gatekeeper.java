package com.xming.gatekeeper;

import com.xming.gatekeeper.api.ApiGateway;
import com.xming.gatekeeper.jwt.JwtManager;
import com.xming.gatekeeper.web.ApiGatewayImpl;
import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class Gatekeeper extends JavaPlugin {
    static Gatekeeper plugin;

    private Javalin app;
    private ApiGatewayImpl gateway;

    private long startTime;

    private FileConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.startTime = System.currentTimeMillis();

        plugin = this;

        this.saveDefaultConfig();
        this.config = this.getConfig();

        String secretKey = config.getString("jwt.secret-key");
        Integer tokenExpiration = config.getInt("token-expiration", 3600);
        String adminPassword = config.getString("admin-password", "123456");
        int port = config.getInt("port", 8080);

        app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> cors.addRule(CorsPluginConfig.CorsRule::anyHost));
        }).start("0.0.0.0", port);
        JwtManager jwt = new JwtManager(secretKey);
        this.gateway = new ApiGatewayImpl(app, jwt);

        getServer().getServicesManager().register(ApiGateway.class, gateway, this, ServicePriority.High);

        app.post("/auth/login", ctx -> {
            Map<String, Object> data = ctx.bodyAsClass(Map.class);
            String username = (String) data.get("username");
            String password = (String) data.get("password");

            // 暂时硬编码账号密码
            if ("admin".equals(username) && adminPassword.equals(password)) {
                String token = jwt.issueToken("admin", Set.of("admin"), tokenExpiration);
                ctx.json(Map.of("token", token));
            } else {
                ctx.status(401).json(Map.of("error", "Invalid credentials"));
            }
        });

        app.get("/health", ctx -> {
            int onlinePlayers = Bukkit.getOnlinePlayers().size();
            int uptime = (int) ((System.currentTimeMillis() - startTime) / 1000);
            ctx.json(Map.of(
                    "status", "ok",
                    "onlinePlayers", onlinePlayers,
                    "uptime", uptime
            ));
        });

        GatekeeperRoutes.registerPluginRoutes(this, this.gateway);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        gateway.getWsHub().clear();
        app.stop();
        GatekeeperRoutes.unregisterPluginRoutes(this, this.gateway);
    }

    static public Gatekeeper getPlugin() {
        return plugin;
    }

    public Javalin getApp() {
        return app;
    }

    public ApiGatewayImpl getGateway() {
        return gateway;
    }

    public long getStartTime() {
        return startTime;
    }
}
