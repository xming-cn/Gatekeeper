package com.xming.gatekeeper.api;

import java.util.Set;

public interface AuthUser {
    String username();
    Set<String> roles();
}