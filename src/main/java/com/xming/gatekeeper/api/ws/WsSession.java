package com.xming.gatekeeper.api.ws;

import com.xming.gatekeeper.api.AuthUser;

public interface WsSession {
    String getId();
    void send(String text);
    void close(int status, String reason);

    void authenticate(AuthUser user);
    boolean isAuthenticated();
    AuthUser getUser();

    WsContext getContext();
}