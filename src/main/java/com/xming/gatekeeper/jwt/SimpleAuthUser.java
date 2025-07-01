package com.xming.gatekeeper.jwt;

import com.xming.gatekeeper.api.AuthUser;

import java.util.Set;

public record SimpleAuthUser(String username, Set<String> roles) implements AuthUser {

}
