package com.project.backend.services;

import com.project.backend.dto.AppointmentDTO;
import com.project.backend.models.Appointment;
import com.project.backend.repositories.AppointmentRepository;
import com.project.backend.repositories.DoctorRepository;
import com.project.backend.repositories.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    @SuppressWarnings("unused")
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              TokenService tokenService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
    }

    /** Books a new appointment. Returns 1 on success, 0 on error. */
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /** Updates an existing appointment. */
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Appointment> existing = appointmentRepository.findById(appointment.getId());
            if (existing.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            appointmentRepository.save(appointment);
            response.put("message", "Appointment updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /** Cancels an appointment by ID after verifying the patient's token. */
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Appointment> optional = appointmentRepository.findById(id);
            if (optional.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            Appointment appointment = optional.get();
            String email = tokenService.extractIdentifier(token);
            if (!appointment.getPatient().getEmail().equals(email)) {
                response.put("message", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            appointmentRepository.delete(appointment);
            response.put("message", "Appointment cancelled successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /** Retrieves appointments for a doctor on a specific date, optionally filtered by patient name. */
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String identifier = tokenService.extractIdentifier(token);
            var doctor = doctorRepository.findByEmail(identifier);
            if (doctor == null) {
                response.put("message", "Doctor not found");
                return response;
            }
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end   = date.atTime(23, 59, 59);

            List<Appointment> appointments;
            if (pname != null && !pname.isBlank() && !pname.equals("null")) {
                appointments = appointmentRepository
                        .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                                doctor.getId(), pname, start, end);
            } else {
                appointments = appointmentRepository
                        .findByDoctorIdAndAppointmentTimeBetween(doctor.getId(), start, end);
            }

            List<AppointmentDTO> dtos = appointments.stream().map(a -> new AppointmentDTO(
                    a.getId(),
                    a.getDoctor().getId(), a.getDoctor().getName(),
                    a.getPatient().getId(), a.getPatient().getName(),
                    a.getPatient().getEmail(), a.getPatient().getPhone(),
                    a.getPatient().getAddress(),
                    a.getAppointmentTime(), a.getStatus()
            )).collect(Collectors.toList());

            response.put("appointments", dtos);
        } catch (Exception e) {
            response.put("message", "Internal server error");
        }
        return response;
    }
}
