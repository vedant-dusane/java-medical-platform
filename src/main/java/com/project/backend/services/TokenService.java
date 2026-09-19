package com.project.backend.services;

import com.project.backend.repositories.AdminRepository;
import com.project.backend.repositories.DoctorRepository;
import com.project.backend.repositories.PatientRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles everything JWT — generating tokens, pulling out the identifier,
 * and checking whether a token belongs to a valid admin, doctor, or patient.
 */
@Component
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    /** Generates a JWT token valid for 7 days with the given identifier as subject. */
    public String generateToken(String identifier) {
        return Jwts.builder()
                .subject(identifier)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    /** Extracts the identifier (email / username) from a JWT token. */
    public String extractIdentifier(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validates token for the given user type.
     * Returns empty Map if valid, Map with error if invalid.
     */
    public Map<String, Object> validateToken(String token, String user) {
        Map<String, Object> errors = new HashMap<>();
        try {
            String identifier = extractIdentifier(token);
            boolean valid = switch (user) {
                case "admin"   -> adminRepository.findByUsername(identifier) != null;
                case "doctor"  -> doctorRepository.findByEmail(identifier) != null;
                case "patient" -> patientRepository.findByEmail(identifier) != null;
                default        -> false;
            };
            if (!valid) errors.put("error", "User not found");
        } catch (Exception e) {
            errors.put("error", "Invalid or expired token");
        }
        return errors;
    }

    public SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
