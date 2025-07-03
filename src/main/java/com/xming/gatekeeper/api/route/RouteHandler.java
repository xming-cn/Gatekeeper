package com.xming.gatekeeper.api.route;

@FunctionalInterface
public interface RouteHandler {
    ApiResponse handle(ApiRequest request);
}