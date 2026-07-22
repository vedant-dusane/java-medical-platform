package com.project.backend.dto;

/**
 * Login DTO
 * Used to receive login credentials from the client in @RequestBody.
 * Not stored in the database.
 */
public class Login {

    private String identifier; // email for Doctor/Patient, username for Admin
    private String password;

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
