package com.xming.gatekeeper.api;

@FunctionalInterface
public interface RouteRegistrar {
    void register(RouteBuilder builder);
}