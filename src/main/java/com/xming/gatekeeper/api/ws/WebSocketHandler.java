package com.xming.gatekeeper.api.ws;

public interface WebSocketHandler {
    void onConnect(WsSession session);
    void onMessage(WsSession session, String message);
    void onClose(WsSession session, int status, String reason);
    void onError(WsSession session, Throwable error);
}