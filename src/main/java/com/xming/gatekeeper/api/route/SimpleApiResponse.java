package com.xming.gatekeeper.api.route;

import java.util.Map;

public record SimpleApiResponse(int statusCode, Map<String, String> headers, String body) implements ApiResponse {

}
