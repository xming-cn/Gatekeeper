package com.xming.gatekeeper.web;

import com.xming.gatekeeper.api.route.ApiRequest;
import com.xming.gatekeeper.api.route.ApiResponse;
import com.xming.gatekeeper.api.route.RouteBuilder;
import com.xming.gatekeeper.api.route.RouteHandler;
import com.xming.gatekeeper.jwt.JwtManager;
import io.javalin.Javalin;
import com.xming.gatekeeper.api.*;

import java.util.Map;

public class RouteBuilderImpl implements RouteBuilder {

    private final Javalin app;
    private final String prefix;
    private final JwtManager jwtManager;

    public RouteBuilderImpl(Javalin app, String namespace, JwtManager jwtManager) {
        this.app = app;
        this.prefix = "/api/" + namespace;
        this.jwtManager = jwtManager;
    }

    private void register(String method, String path, RouteHandler handler) {
        String fullPath = prefix + path;

        app.addHttpHandler(io.javalin.http.HandlerType.valueOf(method.toUpperCase()), fullPath, ctx -> {
            AuthUser user;
            try {
                user = jwtManager.verifyTokenFromContext(ctx); // 验证 JWT
            } catch (Exception ex) {
                ctx.status(401).json(Map.of(
                        "error", "Unauthorized",
                        "message", ex.getMessage()
                ));
                return;
            }

            try {
                ApiRequest req = new JavalinApiRequest(ctx, user);
                ApiResponse res = handler.handle(req);

                ctx.status(res.statusCode());
                res.headers().forEach(ctx::header);
                ctx.result(res.body());
            } catch (Exception e) {
                ctx.status(500).json(Map.of(
                        "error", "Internal Server Error",
                        "message", e.getMessage()
                ));
            }
        });
    }


    @Override public RouteBuilder get(String path, RouteHandler handler) {
        register("GET", path, handler); return this;
    }

    @Override public RouteBuilder post(String path, RouteHandler handler) {
        register("POST", path, handler); return this;
    }

    @Override public RouteBuilder put(String path, RouteHandler handler) {
        register("PUT", path, handler); return this;
    }

    @Override public RouteBuilder delete(String path, RouteHandler handler) {
        register("DELETE", path, handler); return this;
    }
}