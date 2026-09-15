package com.project.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Login DTO
 * Used to receive login credentials from the client in @RequestBody.
 * Not stored in the database.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Login {

    @JsonAlias({"email", "username", "identifier"})
    private String identifier; // email for Doctor/Patient, username for Admin
    private String password;

    public Login() {
    }

    public Login(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Convenience alias accessors to ensure compatibility with payloads specifying email or username directly
    public String getEmail() {
        return identifier;
    }

    public void setEmail(String email) {
        this.identifier = email;
    }

    public String getUsername() {
        return identifier;
    }

    public void setUsername(String username) {
        this.identifier = username;
    }
}
