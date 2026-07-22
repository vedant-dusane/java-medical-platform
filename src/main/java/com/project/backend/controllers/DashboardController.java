package com.project.backend.controllers;

import com.project.backend.services.ServiceLayer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DashboardController {

    private final ServiceLayer service;

    public DashboardController(ServiceLayer service) {
        this.service = service;
    }

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        if (service.validateToken(token, "admin") == null) {
            return "admin/adminDashboard";
        }
        return "redirect:/";
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        if (service.validateToken(token, "doctor") == null) {
            return "doctor/doctorDashboard";
        }
        return "redirect:/";
    }
}
