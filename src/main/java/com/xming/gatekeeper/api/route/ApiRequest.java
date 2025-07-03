package com.xming.gatekeeper.api.route;

import com.xming.gatekeeper.api.AuthUser;

import java.util.Map;

public interface ApiRequest {
    String getPath();
    String getMethod();
    String getHeader(String name);
    String getBody();
    Map<String, Object> getJsonBody(); // JSON body 解析结果
    AuthUser getAuthenticatedUser();   // 从 JWT 解析出的用户
    String getPathParam(String name);
}