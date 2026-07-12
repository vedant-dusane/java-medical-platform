package com.project.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.backend.services.TokenService;

import java.util.Map;

/**
 * DashboardController
 *
 * Serves as a gatekeeper to Thymeleaf dashboard views (adminDashboard and
 * doctorDashboard) by verifying access tokens for authenticated users.
 *
 * Returns a Thymeleaf view name if the token is valid,
 * or redirects to the root login page if it is not.
 */
@Controller
public class DashboardController {

    @Autowired
    private TokenService tokenService;

    /**
     * Renders the Admin Dashboard view after validating the token.
     *
     * @param token – JWT token passed as a path variable.
     * @return admin/adminDashboard view if valid, redirect to "/" otherwise.
     */
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        Map<String, Object> result = tokenService.validateToken(token, "admin");

        if (result.isEmpty()) {
            // Token is valid → serve the Thymeleaf template
            return "admin/adminDashboard";
        }

        // Invalid token → redirect to root login page
        return "redirect:/";
    }

    /**
     * Renders the Doctor Dashboard view after validating the token.
     *
     * @param token – JWT token passed as a path variable.
     * @return doctor/doctorDashboard view if valid, redirect to "/" otherwise.
     */
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        Map<String, Object> result = tokenService.validateToken(token, "doctor");

        if (result.isEmpty()) {
            // Token is valid → serve the Thymeleaf template
            return "doctor/doctorDashboard";
        }

        // Invalid token → redirect to root login page
        return "redirect:/";
    }
}
