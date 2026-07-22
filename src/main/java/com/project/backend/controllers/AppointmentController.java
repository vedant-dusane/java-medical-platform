package com.project.backend.controllers;

import com.project.backend.models.Appointment;
import com.project.backend.services.AppointmentService;
import com.project.backend.services.ServiceLayer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final ServiceLayer service;

    public AppointmentController(AppointmentService appointmentService, ServiceLayer service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String patientName,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (validation != null) {
            Map<String, Object> err = new HashMap<>(validation.getBody());
            return ResponseEntity.status(validation.getStatusCode()).body(err);
        }
        return ResponseEntity.ok(appointmentService.getAppointment(patientName, date, token));
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation != null) return validation;

        int valid = service.validateAppointment(appointment);
        if (valid == -1) {
            response.put("message", "Doctor not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if (valid == 0) {
            response.put("message", "Time slot not available");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        int result = appointmentService.bookAppointment(appointment);
        if (result == 1) {
            response.put("message", "Appointment booked successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        response.put("message", "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation != null) return validation;
        return appointmentService.updateAppointment(appointment);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable long id,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation != null) return validation;
        return appointmentService.cancelAppointment(id, token);
    }
}
