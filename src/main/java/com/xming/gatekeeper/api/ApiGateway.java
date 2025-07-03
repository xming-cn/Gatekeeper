package com.xming.gatekeeper.api;

import com.xming.gatekeeper.api.route.RouteRegistrar;
import com.xming.gatekeeper.api.ws.WebSocketRegistrar;
import com.xming.gatekeeper.api.ws.WsHub;
import org.bukkit.plugin.Plugin;

public interface ApiGateway {
    void registerPluginRoutes(Plugin plugin, RouteRegistrar registrar);
    void registerWebSocketRoutes(Plugin plugin, WebSocketRegistrar registrar);
    WsHub getWsHub();
}