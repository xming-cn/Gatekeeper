package com.xming.gatekeeper.api.route;

@FunctionalInterface
public interface RouteRegistrar {
    void register(RouteBuilder builder);
}