package com.xming.gatekeeper.web;

import com.xming.gatekeeper.api.AuthUser;
import com.xming.gatekeeper.api.ws.WsSession;
import io.javalin.websocket.WsContext;

public class WsSessionImpl implements WsSession {
    private final WsContext ctx;
    private AuthUser user;

    public WsSessionImpl(WsContext ctx) {
        this.ctx = ctx;
    }

    public void authenticate(AuthUser user) {
        this.user = user;
    }

    public boolean isAuthenticated() {
        return user != null;
    }

    @Override
    public String getId() {
        return ctx.sessionId();
    }

    @Override
    public void send(String text) {
        ctx.send(text);
    }

    @Override
    public void close(int status, String reason) {
        ctx.closeSession(status, reason);
    }

    @Override
    public AuthUser getUser() {
        return user;
    }

    @Override
    public com.xming.gatekeeper.api.ws.WsContext getContext() {
        return new WsContextImpl(ctx);
    }
}