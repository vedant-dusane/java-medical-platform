package com.project.backend.services;

import com.project.backend.dto.AppointmentDTO;
import com.project.backend.models.Patient;
import com.project.backend.repositories.AppointmentRepository;
import com.project.backend.repositories.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /** Saves a new patient. Returns 1 on success, 0 on failure. */
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /** Retrieves appointments for a patient, verifying via token. */
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractIdentifier(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null || !patient.getId().equals(id)) {
                response.put("message", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            List<AppointmentDTO> dtos = appointmentRepository.findByPatientId(id)
                    .stream().map(a -> new AppointmentDTO(
                            a.getId(),
                            a.getDoctor().getId(), a.getDoctor().getName(),
                            a.getPatient().getId(), a.getPatient().getName(),
                            a.getPatient().getEmail(), a.getPatient().getPhone(),
                            a.getPatient().getAddress(),
                            a.getAppointmentTime(), a.getStatus()))
                    .collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /** Filters appointments by condition: "past" (status=1) or "future" (status=0). */
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            int status = condition.equalsIgnoreCase("past") ? 1 : 0;
            List<AppointmentDTO> dtos = appointmentRepository
                    .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status)
                    .stream().map(a -> new AppointmentDTO(
                            a.getId(),
                            a.getDoctor().getId(), a.getDoctor().getName(),
                            a.getPatient().getId(), a.getPatient().getName(),
                            a.getPatient().getEmail(), a.getPatient().getPhone(),
                            a.getPatient().getAddress(),
                            a.getAppointmentTime(), a.getStatus()))
                    .collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /** Filters appointments by doctor name for a patient. */
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<AppointmentDTO> dtos = appointmentRepository
                    .filterByDoctorNameAndPatientId(name, patientId)
                    .stream().map(a -> new AppointmentDTO(
                            a.getId(),
                            a.getDoctor().getId(), a.getDoctor().getName(),
                            a.getPatient().getId(), a.getPatient().getName(),
                            a.getPatient().getEmail(), a.getPatient().getPhone(),
                            a.getPatient().getAddress(),
                            a.getAppointmentTime(), a.getStatus()))
                    .collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /** Filters appointments by doctor name and condition. */
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition,
                                                                           String name,
                                                                           long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            int status = condition.equalsIgnoreCase("past") ? 1 : 0;
            List<AppointmentDTO> dtos = appointmentRepository
                    .filterByDoctorNameAndPatientIdAndStatus(name, patientId, status)
                    .stream().map(a -> new AppointmentDTO(
                            a.getId(),
                            a.getDoctor().getId(), a.getDoctor().getName(),
                            a.getPatient().getId(), a.getPatient().getName(),
                            a.getPatient().getEmail(), a.getPatient().getPhone(),
                            a.getPatient().getAddress(),
                            a.getAppointmentTime(), a.getStatus()))
                    .collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /** Returns patient details from JWT token. */
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractIdentifier(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                response.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            response.put("patient", patient);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
