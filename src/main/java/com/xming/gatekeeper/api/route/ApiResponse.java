package com.xming.gatekeeper.api.route;

import com.xming.gatekeeper.api.JsonUtil;

import java.util.Map;

public interface ApiResponse {
    int statusCode();
    Map<String, String> headers();
    String body();

    static ApiResponse json(int statusCode, Object obj) {
        return new SimpleApiResponse(statusCode, Map.of("Content-Type", "application/json"), JsonUtil.toJson(obj));
    }
}