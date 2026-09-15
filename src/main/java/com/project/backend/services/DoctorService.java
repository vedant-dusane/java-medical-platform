package com.project.backend.services;

import com.project.backend.dto.Login;
import com.project.backend.models.Doctor;
import com.project.backend.repositories.AppointmentRepository;
import com.project.backend.repositories.DoctorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /** Returns available time slots for a doctor on a given date. */
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> opt = doctorRepository.findById(doctorId);
        if (opt.isEmpty()) return new ArrayList<>();

        Doctor doctor = opt.get();
        List<String> allSlots = new ArrayList<>(doctor.getAvailableTimes() != null
                ? doctor.getAvailableTimes() : new ArrayList<>());

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.atTime(23, 59, 59);
        var booked = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        List<String> bookedTimes = booked.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toList());

        allSlots.removeIf(slot -> bookedTimes.stream().anyMatch(bt ->
                slot.equalsIgnoreCase(bt)
                || slot.startsWith(bt)
                || bt.startsWith(slot)
                || (slot.length() >= 5 && bt.length() >= 5 && slot.substring(0, 5).equals(bt.substring(0, 5)))
        ));
        return allSlots;
    }

    /** Saves a new doctor. Returns 1 success, -1 duplicate, 0 error. */
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) return -1;
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /** Updates an existing doctor. Returns 1 success, -1 not found, 0 error. */
    public int updateDoctor(Doctor doctor) {
        try {
            if (!doctorRepository.existsById(doctor.getId())) return -1;
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /** Returns all doctors. */
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    /** Deletes a doctor and their appointments. Returns 1 success, -1 not found, 0 error. */
    public int deleteDoctor(long id) {
        try {
            if (!doctorRepository.existsById(id)) return -1;
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /** Validates doctor login credentials; returns token on success. */
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());
            if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            response.put("token", tokenService.generateToken(doctor.getEmail()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /** Finds doctors by partial name. */
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorRepository.findByNameLike(name));
        return response;
    }

    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        return Map.of("doctors", filterDoctorByTime(doctors, amOrPm));
    }

    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        return Map.of("doctors", filterDoctorByTime(doctors, amOrPm));
    }

    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        return Map.of("doctors",
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty));
    }

    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        return Map.of("doctors", filterDoctorByTime(doctors, amOrPm));
    }

    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        return Map.of("doctors", doctorRepository.findBySpecialtyIgnoreCase(specialty));
    }

    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        return Map.of("doctors", filterDoctorByTime(doctorRepository.findAll(), amOrPm));
    }

    /** Filters a list of doctors by AM or PM availability or specific time slot. */
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (amOrPm == null || amOrPm.isBlank() || amOrPm.equalsIgnoreCase("null")) {
            return doctors;
        }
        String cleanParam = amOrPm.trim();
        return doctors.stream().filter(d -> {
            if (d.getAvailableTimes() == null || d.getAvailableTimes().isEmpty()) return false;
            return d.getAvailableTimes().stream().anyMatch(t -> {
                if (cleanParam.equalsIgnoreCase("AM")) {
                    return t.toUpperCase().contains("AM") || (t.matches("^\\d{2}:.*") && t.compareTo("12:00") < 0);
                } else if (cleanParam.equalsIgnoreCase("PM")) {
                    return t.toUpperCase().contains("PM") || (t.matches("^\\d{2}:.*") && t.compareTo("12:00") >= 0);
                } else {
                    String cleanT = t.trim();
                    return cleanT.equalsIgnoreCase(cleanParam)
                            || cleanT.contains(cleanParam)
                            || cleanParam.contains(cleanT)
                            || (cleanT.length() >= 5 && cleanParam.length() >= 5 && cleanT.substring(0, 5).equals(cleanParam.substring(0, 5)));
                }
            });
        }).collect(Collectors.toList());
    }
}
