package com.project.backend.controllers;

import com.project.backend.models.Prescription;
import com.project.backend.services.PrescriptionService;
import com.project.backend.services.ServiceLayer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final ServiceLayer service;

    public PrescriptionController(PrescriptionService prescriptionService, ServiceLayer service) {
        this.prescriptionService = prescriptionService;
        this.service = service;
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @PathVariable String token,
            @RequestBody Prescription prescription) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (validation != null) return validation;
        return prescriptionService.savePrescription(prescription);
    }

    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        Map<String, Object> errMap = new HashMap<>();
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (validation != null) {
            errMap.putAll(validation.getBody());
            return ResponseEntity.status(validation.getStatusCode()).body(errMap);
        }
        return prescriptionService.getPrescription(appointmentId);
    }
}
