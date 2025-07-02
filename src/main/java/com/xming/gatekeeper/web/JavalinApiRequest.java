package com.xming.gatekeeper.web;

import com.xming.gatekeeper.api.ApiRequest;
import com.xming.gatekeeper.api.AuthUser;
import io.javalin.http.Context;

import java.util.Map;

public class JavalinApiRequest implements ApiRequest {

    private final Context ctx;
    private final AuthUser user;

    public JavalinApiRequest(Context ctx, AuthUser user) {
        this.ctx = ctx;
        this.user = user;
    }

    @Override public String getPath() { return ctx.path(); }
    @Override public String getMethod() { return ctx.method().name(); }
    @Override public String getHeader(String name) { return ctx.header(name); }
    @Override public String getBody() { return ctx.body(); }
    @Override public Map<String, Object> getJsonBody() { return ctx.bodyAsClass(Map.class); }
    @Override public AuthUser getAuthenticatedUser() { return user; }
    @Override public String getPathParam(String name) {
        return ctx.pathParam(name);
    }
}
