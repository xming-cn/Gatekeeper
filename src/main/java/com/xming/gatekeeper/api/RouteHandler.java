package com.xming.gatekeeper.api;

@FunctionalInterface
public interface RouteHandler {
    ApiResponse handle(ApiRequest request);
}