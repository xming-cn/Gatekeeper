package com.xming.gatekeeper.api.ws;

public interface WebSocketRouteBuilder {
    WebSocketRouteBuilder on(String path, WebSocketHandler handler);
}