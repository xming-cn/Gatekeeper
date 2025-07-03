package com.xming.gatekeeper.api.ws;

@FunctionalInterface
public interface WebSocketRegistrar {
    void register(WebSocketRouteBuilder builder);
}