package com.project.backend.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * TokenService
 *
 * Handles token validation logic for Admin and Doctor dashboard access.
 *
 * validateToken(token, role):
 *   - Returns an empty Map  → token is valid (controller proceeds to the view).
 *   - Returns a non-empty Map → token is invalid (controller redirects to login).
 */
@Service
public class TokenService {

    /**
     * Validates a JWT token and checks that it belongs to the expected role.
     *
     * @param token – The JWT token string passed from the client.
     * @param role  – The expected role: "admin" or "doctor".
     * @return An empty Map if the token is valid for the given role;
     *         a Map containing an error entry otherwise.
     */
    public Map<String, Object> validateToken(String token, String role) {
        Map<String, Object> errors = new HashMap<>();

        // Basic null / empty check
        if (token == null || token.isBlank()) {
            errors.put("error", "Token is missing or empty");
            return errors;
        }

        // Full JWT signature verification and role claim extraction will be implemented
        // once the JWT utility class is available. For now, a non-blank token is
        // treated as structurally valid.

        return errors; // empty map → valid
    }
}
