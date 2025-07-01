package com.xming.gatekeeper.api;

import org.bukkit.plugin.Plugin;

public interface ApiGateway {
    void registerPluginRoutes(Plugin plugin, RouteRegistrar registrar);
}