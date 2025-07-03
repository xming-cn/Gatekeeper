package com.xming.gatekeeper.api.ws;

import java.util.Set;

public interface WsHub {
    void register(String namespace, String path, WsSession session);
    void unregister(String namespace, String path, WsSession session);

    Set<WsSession> getAuthenticatedSession(String namespace, String path);

    void broadcast(String namespace, String path, String message);
    void sendTo(String namespace, String path, String sessionId, String message);

    int count(String namespace, String path);

    void clear();

    WsSession getSession(WsContext ctx);
}
