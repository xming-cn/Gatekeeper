package com.xming.gatekeeper.web;

import io.javalin.Javalin;
import com.xming.gatekeeper.api.*;
import com.xming.gatekeeper.jwt.JwtManager;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class ApiGatewayImpl implements ApiGateway {

    private final Javalin app;
    private final JwtManager jwtManager;
    private final Map<String, RouteBuilderImpl> routeBuilders = new HashMap<>();

    public ApiGatewayImpl(Javalin app, JwtManager jwtManager) {
        this.app = app;
        this.jwtManager = jwtManager;
    }

    @Override
    public void registerPluginRoutes(Plugin plugin, RouteRegistrar registrar) {
        String namespace = plugin.getName().toLowerCase();
        if (routeBuilders.containsKey(namespace)) {
            throw new IllegalArgumentException("Namespace already registered: " + namespace);
        }

        RouteBuilderImpl builder = new RouteBuilderImpl(app, namespace, jwtManager);
        registrar.register(builder);
        routeBuilders.put(namespace, builder);
    }
}