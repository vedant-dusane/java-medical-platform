package com.project.backend.services;

import com.project.backend.dto.Login;
import com.project.backend.models.Admin;
import com.project.backend.models.Appointment;
import com.project.backend.models.Patient;
import com.project.backend.repositories.AdminRepository;
import com.project.backend.repositories.DoctorRepository;
import com.project.backend.repositories.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central service that glues together auth, token validation, and coordination
 * between doctors, patients, and appointments. Most controllers delegate here.
 */
@Service
public class ServiceLayer {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public ServiceLayer(TokenService tokenService,
                        AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository,
                        DoctorService doctorService,
                        PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    /** Validates token for a given user type. Returns error response if invalid. */
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        Map<String, Object> result = tokenService.validateToken(token, user);
        if (!result.isEmpty()) {
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return null; // null means valid — caller proceeds
    }

    /** Validates admin credentials and returns a JWT token if correct. */
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
            if (admin == null || !admin.getPassword().equals(receivedAdmin.getPassword())) {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            response.put("token", tokenService.generateToken(admin.getUsername()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Filters doctors by name, specialty, and available time.
     * Falls back gracefully when parameters are empty/null.
     */
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        boolean hasName      = name      != null && !name.isBlank()      && !name.equals("null");
        boolean hasSpecialty = specialty != null && !specialty.isBlank() && !specialty.equals("null");
        boolean hasTime      = time      != null && !time.isBlank()      && !time.equals("null");

        if (hasName && hasSpecialty && hasTime)
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        if (hasName && hasTime)
            return doctorService.filterDoctorByNameAndTime(name, time);
        if (hasName && hasSpecialty)
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        if (hasSpecialty && hasTime)
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        if (hasSpecialty)
            return doctorService.filterDoctorBySpecility(specialty);
        if (hasTime)
            return doctorService.filterDoctorsByTime(time);
        if (hasName)
            return doctorService.findDoctorByName(name);

        return Map.of("doctors", doctorService.getDoctors());
    }

    /**
     * Validates appointment availability.
     * Returns 1 valid, 0 time unavailable, -1 doctor not found.
     */
    public int validateAppointment(Appointment appointment) {
        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null) return -1;
        var optional = doctorRepository.findById(appointment.getDoctor().getId());
        if (optional.isEmpty()) return -1;

        LocalDate date = appointment.getAppointmentTime().toLocalDate();
        List<String> available = doctorService.getDoctorAvailability(appointment.getDoctor().getId(), date);
        String requestedTime = appointment.getAppointmentTime().toLocalTime().toString();
        boolean matches = available.stream().anyMatch(slot ->
                slot.equalsIgnoreCase(requestedTime)
                || slot.startsWith(requestedTime)
                || requestedTime.startsWith(slot)
                || (slot.length() >= 5 && requestedTime.length() >= 5 && slot.substring(0, 5).equals(requestedTime.substring(0, 5)))
        );
        return matches ? 1 : 0;
    }

    /** Checks if a patient already exists. Returns true if new, false if duplicate. */
    public boolean validatePatient(Patient patient) {
        return patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()) == null;
    }

    /** Validates patient login and returns JWT token on success. */
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(login.getIdentifier());
            if (patient == null || !patient.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            response.put("token", tokenService.generateToken(patient.getEmail()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /** Filters patient appointments by condition and/or doctor name. */
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        String email = tokenService.extractIdentifier(token);
        Patient patient = patientRepository.findByEmail(email);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized"));
        }
        Long patientId = patient.getId();
        boolean hasCondition = condition != null && !condition.isBlank() && !condition.equals("null");
        boolean hasName      = name      != null && !name.isBlank()      && !name.equals("null");

        if (hasCondition && hasName)
            return patientService.filterByDoctorAndCondition(condition, name, patientId);
        if (hasCondition)
            return patientService.filterByCondition(condition, patientId);
        if (hasName)
            return patientService.filterByDoctor(name, patientId);

        return patientService.getPatientAppointment(patientId, token);
    }
}
