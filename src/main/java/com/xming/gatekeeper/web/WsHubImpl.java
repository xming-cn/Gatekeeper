package com.xming.gatekeeper.web;

import com.xming.gatekeeper.api.ws.WsContext;
import com.xming.gatekeeper.api.ws.WsHub;
import com.xming.gatekeeper.api.ws.WsSession;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WsHubImpl implements WsHub {
    private final Map<String, Map<String, WsSession>> routes = new ConcurrentHashMap<>();
    private final Map<String, WsSession> ctxSessionMap = new ConcurrentHashMap<>();

    private String key(String namespace, String path) {
        return namespace + ":" + path;
    }

    @Override
    public void register(String namespace, String path, WsSession session) {
        routes.computeIfAbsent(key(namespace, path), k -> new ConcurrentHashMap<>())
                .put(session.getId(), session);
        ctxSessionMap.put(session.getContext().getSessionId(), session);
    }

    @Override
    public void unregister(String namespace, String path, WsSession session) {
        Map<String, WsSession> map = routes.get(key(namespace, path));
        if (map != null) {
            map.remove(session.getId());
            if (map.isEmpty()) {
                routes.remove(key(namespace, path));
            }
        }
    }

    @Override
    public Set<WsSession> getAuthenticatedSession(String namespace, String path) {
        Collection<WsSession> sessions = routes.getOrDefault(key(namespace, path), Map.of()).values();
        sessions = sessions.stream().filter(WsSession::isAuthenticated).toList();
        return new HashSet<>(sessions);
    }

    @Override
    public void broadcast(String namespace, String path, String message) {
        getAuthenticatedSession(namespace, path).forEach(s -> {
            try {
                s.send(message);
            } catch (Exception e) {
                e.printStackTrace(); // Or remove invalid session
            }
        });
    }

    @Override
    public void sendTo(String namespace, String path, String sessionId, String message) {
        Map<String, WsSession> map = routes.getOrDefault(key(namespace, path), Map.of());
        WsSession session = map.get(sessionId);
        if (session != null) {
            session.send(message);
        }
    }

    @Override
    public int count(String namespace, String path) {
        return routes.getOrDefault(key(namespace, path), Map.of()).size();
    }

    @Override
    public void clear() {
        for (String key : this.routes.keySet()) {
            Map<String, WsSession> sessions = this.routes.get(key);
            if (sessions != null) {
                for (WsSession session : sessions.values()) {
                    try {
                        session.close(1001, "Server shutdown");
                    } catch (Exception e) {
                        e.printStackTrace(); // Handle close exceptions
                    }
                }
            }
            this.routes.remove(key);
        }
    }

    @Override
    public WsSession getSession(com.xming.gatekeeper.api.ws.WsContext ctx) {
        return ctxSessionMap.get(ctx.getSessionId());
    }
}
