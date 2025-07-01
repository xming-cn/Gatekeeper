package com.xming.gatekeeper.api;

import java.util.Map;

public record SimpleApiResponse(int statusCode, Map<String, String> headers, String body) implements ApiResponse {

}
