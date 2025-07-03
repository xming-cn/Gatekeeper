package com.xming.gatekeeper.api.route;

public interface RouteBuilder {
    RouteBuilder get(String path, RouteHandler handler);
    RouteBuilder post(String path, RouteHandler handler);
    RouteBuilder put(String path, RouteHandler handler);
    RouteBuilder delete(String path, RouteHandler handler);
}