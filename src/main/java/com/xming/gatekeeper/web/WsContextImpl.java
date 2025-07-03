package com.xming.gatekeeper.web;

import com.xming.gatekeeper.api.ws.WsContext;

public class WsContextImpl implements WsContext {
    io.javalin.websocket.WsContext ctx;

    public WsContextImpl(io.javalin.websocket.WsContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public String getSessionId() {
        return ctx.sessionId();
    }
}
