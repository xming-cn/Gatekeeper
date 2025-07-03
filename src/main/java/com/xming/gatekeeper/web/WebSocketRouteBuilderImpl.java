package com.xming.gatekeeper.web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xming.gatekeeper.api.AuthUser;
import com.xming.gatekeeper.api.ws.*;
import com.xming.gatekeeper.jwt.JwtManager;
import io.javalin.Javalin;

public class WebSocketRouteBuilderImpl implements WebSocketRouteBuilder {

    private final Javalin app;
    private final String namespace;
    private final String prefix;
    private final JwtManager jwtManager;
    private final WsHub wsHub;

    public WebSocketRouteBuilderImpl(Javalin app, String namespace, JwtManager jwtManager, WsHub wsHub) {
        this.app = app;
        this.namespace = namespace.toLowerCase();
        this.prefix = "/ws/" + namespace;
        this.jwtManager = jwtManager;
        this.wsHub = wsHub;
    }

    @Override
    public WebSocketRouteBuilder on(String path, WebSocketHandler handler) {
        String fullPath = prefix + path;
        app.ws(fullPath, ws -> {
            ws.onConnect(ctx -> {
                WsSessionImpl session = new WsSessionImpl(ctx);
                wsHub.register(this.namespace, path, session);
                handler.onConnect(session);
            });

            ws.onMessage(ctx -> {
                WsContext context = new WsContextImpl(ctx);
                WsSessionImpl session = (WsSessionImpl) wsHub.getSession(context);
                String msg = ctx.message();

                if (!session.isAuthenticated()) {
                    try {
                        // 假设客户端发送的是 JSON：{ "type": "auth", "token": "..." }
                        JsonObject json = JsonParser.parseString(msg).getAsJsonObject();
                        if (!"auth".equals(json.get("type").getAsString())) {
                            ctx.session.close(4401, "unauthenticated");
                            return;
                        }
                        String token = json.get("token").getAsString();
                        AuthUser user = jwtManager.verifyToken(token);
                        session.authenticate(user);
                        // 可选回复确认
                        session.send("{\"type\": \"auth_ok\"}");
                    } catch (Exception e) {
                        ctx.session.close(4401, "invalid token");
                    }
                    return;
                }

                handler.onMessage(session, msg);
            });

            ws.onClose(ctx -> {
                try {
                    WsContext context = new WsContextImpl(ctx);
                    WsSessionImpl session = (WsSessionImpl) wsHub.getSession(context);
                    wsHub.unregister(this.namespace, path, session);
                    handler.onClose(session, ctx.status(), ctx.reason());
                } catch (RuntimeException ignored) {

                }
            });

            ws.onError(ctx -> {
                try {
                    WsContext context = new WsContextImpl(ctx);
                    WsSessionImpl session = (WsSessionImpl) wsHub.getSession(context);
                    handler.onError(session, ctx.error());
                } catch (RuntimeException ignored) {

                }
            });
        });

        return this;
    }
}
