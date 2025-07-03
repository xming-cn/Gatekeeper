package com.xming.gatekeeper.web;

import com.xming.gatekeeper.api.route.RouteRegistrar;
import com.xming.gatekeeper.api.ws.WebSocketRegistrar;
import com.xming.gatekeeper.api.ws.WsHub;
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

    private final WsHub wsHub = new WsHubImpl();

    public ApiGatewayImpl(Javalin app, JwtManager jwtManager) {
        this.app = app;
        this.jwtManager = jwtManager;
    }

    @Override
    public void registerPluginRoutes(Plugin plugin, RouteRegistrar registrar) {
        String namespace = plugin.getName().toLowerCase();
        this.registerPluginRoutes(namespace, registrar);
    }

    public void registerPluginRoutes(String namespace, RouteRegistrar registrar) {
        if (routeBuilders.containsKey(namespace)) {
            throw new IllegalArgumentException("Namespace already registered: " + namespace);
        }

        RouteBuilderImpl builder = new RouteBuilderImpl(app, namespace, jwtManager);
        registrar.register(builder);
        routeBuilders.put(namespace, builder);
    }

    @Override
    public void registerWebSocketRoutes(Plugin plugin, WebSocketRegistrar registrar) {
        WebSocketRouteBuilderImpl builder = new WebSocketRouteBuilderImpl(app, plugin.getName().toLowerCase(), jwtManager, wsHub);
        registrar.register(builder);
    }

    @Override
    public WsHub getWsHub() {
        return wsHub;
    }
}