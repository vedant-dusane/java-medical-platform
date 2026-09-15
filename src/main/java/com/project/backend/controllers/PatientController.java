package com.project.backend.controllers;

import com.project.backend.dto.Login;
import com.project.backend.models.Patient;
import com.project.backend.services.PatientService;
import com.project.backend.services.ServiceLayer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({ "${api.path:}" + "patient", "/patient", "/api/patient" })
public class PatientController {

    private final PatientService patientService;
    private final ServiceLayer service;

    public PatientController(PatientService patientService, ServiceLayer service) {
        this.patientService = patientService;
        this.service = service;
    }

    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatientDetails(@PathVariable String token) {
        Map<String, Object> errMap = new HashMap<>();
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation != null) {
            errMap.putAll(validation.getBody());
            return ResponseEntity.status(validation.getStatusCode()).body(errMap);
        }
        return patientService.getPatientDetails(token);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {
        Map<String, String> response = new HashMap<>();
        if (!service.validatePatient(patient)) {
            response.put("message", "Patient with email id or phone no already exist");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        int result = patientService.createPatient(patient);
        if (result == 1) {
            response.put("message", "Signup successful");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        response.put("message", "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> patientLogin(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    @GetMapping({ "/{id}/{token}", "/{id}/patient/{token}" })
    public ResponseEntity<Map<String, Object>> getPatientAppointments(
            @PathVariable Long id,
            @PathVariable String token) {

        Map<String, Object> errMap = new HashMap<>();
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation != null) {
            errMap.putAll(validation.getBody());
            return ResponseEntity.status(validation.getStatusCode()).body(errMap);
        }
        return patientService.getPatientAppointment(id, token);
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterAppointments(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {

        Map<String, Object> errMap = new HashMap<>();
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation != null) {
            errMap.putAll(validation.getBody());
            return ResponseEntity.status(validation.getStatusCode()).body(errMap);
        }
        return service.filterPatient(condition, name, token);
    }
}
