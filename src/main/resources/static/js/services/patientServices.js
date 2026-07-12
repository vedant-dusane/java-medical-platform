/**
 * patientServices.js
 * Centralizes all API interactions related to patient data.
 * Used by: Patient Dashboard, Doctor Dashboard, Appointment pages
 *
 * Functions:
 *   patientSignup(data)                        – register a new patient
 *   patientLogin(data)                         – authenticate a patient
 *   getPatientData(token)                      – fetch logged-in patient's profile
 *   getPatientAppointments(id, token, user)    – fetch appointments (patient or doctor view)
 *   filterAppointments(condition, name, token) – filter appointments by status/name
 *   showBookingOverlay(e, doctor, patientData) – display the booking UI overlay
 */

import { API_BASE_URL } from "../config/config.js";

// ── Base endpoint ─────────────────────────────────────────────────────
const PATIENT_API = API_BASE_URL + "/patient";

// ─────────────────────────────────────────────────────────────────────
// POST – Patient Signup
// ─────────────────────────────────────────────────────────────────────
/**
 * Registers a new patient account.
 * @param {Object} data – Patient details: name, email, phone, address, password.
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function patientSignup(data) {
  try {
    // POST patient registration data to the signup endpoint
    const response = await fetch(`${PATIENT_API}/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    // Extract message from response body
    const result = await response.json();

    return {
      success: response.ok,
      message: result.message || (response.ok ? "Signup successful!" : "Signup failed."),
    };
  } catch (error) {
    console.error("patientSignup() error:", error);
    return { success: false, message: "An error occurred during signup." };
  }
}

// ─────────────────────────────────────────────────────────────────────
// POST – Patient Login
// ─────────────────────────────────────────────────────────────────────
/**
 * Authenticates a patient with email and password.
 * Returns the raw fetch Response so the caller can read status and token.
 * @param {Object} data – { email, password }
 * @returns {Promise<Response>}
 */
export async function patientLogin(data) {
  // Log during development — remove before production
  console.log("patientLogin() called with:", data);

  // Return the raw response — caller decides what to do with status / body
  const response = await fetch(`${PATIENT_API}/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });

  return response;
}

// ─────────────────────────────────────────────────────────────────────
// GET – Logged-in Patient Profile
// ─────────────────────────────────────────────────────────────────────
/**
 * Fetches the currently logged-in patient's profile using their token.
 * @param {string} token – JWT token from localStorage.
 * @returns {Promise<Object|null>} Patient object, or null on error.
 */
export async function getPatientData(token) {
  try {
    // Pass token as a query parameter for authentication
    const response = await fetch(`${PATIENT_API}/data?token=${token}`, {
      method: "GET",
      headers: { "Content-Type": "application/json" },
    });

    if (!response.ok) return null;

    // Return the patient profile object
    const patient = await response.json();
    return patient;
  } catch (error) {
    console.error("getPatientData() error:", error);
    return null;
  }
}

// ─────────────────────────────────────────────────────────────────────
// GET – Patient Appointments (shared between patient & doctor views)
// ─────────────────────────────────────────────────────────────────────
/**
 * Fetches appointments for a given patient ID.
 * Works for both patient and doctor dashboards via the user param.
 * @param {number|string} id    – Patient's unique identifier.
 * @param {string}        token – JWT authentication token.
 * @param {string}        user  – "patient" or "doctor" — controls backend behaviour.
 * @returns {Promise<Array|null>} Appointments array, or null on error.
 */
export async function getPatientAppointments(id, token, user) {
  try {
    // Dynamically build URL to support both dashboard roles
    const url = `${PATIENT_API}/appointments/${id}?token=${token}&user=${user}`;

    const response = await fetch(url, {
      method: "GET",
      headers: { "Content-Type": "application/json" },
    });

    if (!response.ok) {
      console.error("getPatientAppointments() failed:", response.status);
      return null;
    }

    // Return the appointments list
    const appointments = await response.json();
    return appointments;
  } catch (error) {
    console.error("getPatientAppointments() error:", error);
    return null;
  }
}

// ─────────────────────────────────────────────────────────────────────
// GET – Filter Appointments
// ─────────────────────────────────────────────────────────────────────
/**
 * Filters a patient's appointments by status condition and/or name.
 * @param {string} condition – e.g. "0" (scheduled) or "1" (completed).
 * @param {string} name      – Patient name filter (or empty string).
 * @param {string} token     – JWT authentication token.
 * @returns {Promise<Array>} Filtered appointments array, or [] on error.
 */
export async function filterAppointments(condition, name, token) {
  try {
    // Construct the filter URL with all parameters
    const url = `${PATIENT_API}/appointments/filter/${condition}/${name || "null"}?token=${token}`;

    const response = await fetch(url, {
      method: "GET",
      headers: { "Content-Type": "application/json" },
    });

    if (!response.ok) {
      // Log the failure and return empty to avoid frontend crash
      console.error("filterAppointments() failed:", response.status);
      return [];
    }

    const data = await response.json();
    return data || [];
  } catch (error) {
    console.error("filterAppointments() error:", error);
    alert("An unexpected error occurred while filtering appointments.");
    return [];
  }
}

// ─────────────────────────────────────────────────────────────────────
// UI Helper – Show Booking Overlay
// ─────────────────────────────────────────────────────────────────────
/**
 * Displays the bottom slide-in booking overlay for a selected doctor.
 * Injects a form into .modalApp and activates it.
 * @param {Event}  e           – Click event (used for ripple origin).
 * @param {Object} doctor      – Doctor object to book with.
 * @param {Object} patientData – Logged-in patient's profile data.
 */
export function showBookingOverlay(e, doctor, patientData) {
  // ── Ripple effect ───────────────────────────────────────────────
  const ripple = document.createElement("div");
  ripple.classList.add("ripple-overlay");
  ripple.style.left = `${e.clientX - 5}px`;
  ripple.style.top  = `${e.clientY - 5}px`;
  document.body.appendChild(ripple);
  setTimeout(() => ripple.classList.add("active"), 10);
  setTimeout(() => ripple.remove(), 700);

  // ── Build or reuse the slide-in modal container ─────────────────
  let overlay = document.getElementById("bookingOverlay");
  let backdrop = document.getElementById("bookingBackdrop");

  if (!overlay) {
    backdrop = document.createElement("div");
    backdrop.id = "bookingBackdrop";
    backdrop.classList.add("modalApp-backdrop");
    document.body.appendChild(backdrop);

    overlay = document.createElement("div");
    overlay.id = "bookingOverlay";
    overlay.classList.add("modalApp");
    document.body.appendChild(overlay);
  }

  // ── Populate with doctor's available time slots ─────────────────
  const times = Array.isArray(doctor.availableTimes)
    ? doctor.availableTimes
    : [];

  const timeOptions = times
    .map((t) => `<option value="${t}">${t}</option>`)
    .join("");

  overlay.innerHTML = `
    <h3>Book Appointment with Dr. ${doctor.name}</h3>
    <p style="text-align:center;color:#666;font-size:0.88rem;margin-bottom:12px;">
      ${doctor.specialty || "General"}
    </p>
    <select id="bookingTime">
      <option value="">-- Select Time Slot --</option>
      ${timeOptions}
    </select>
    <button class="book-confirm-btn" id="confirmBookingBtn">Confirm Booking</button>
    <button
      style="display:block;width:90%;margin:8px auto 0;background:none;border:none;color:#888;cursor:pointer;"
      id="cancelBookingBtn">
      Cancel
    </button>
  `;

  // ── Show overlay and backdrop ───────────────────────────────────
  backdrop.classList.add("active");
  overlay.classList.add("active");

  // ── Confirm booking ─────────────────────────────────────────────
  document.getElementById("confirmBookingBtn").addEventListener("click", async () => {
    const selectedTime = document.getElementById("bookingTime").value;
    if (!selectedTime) {
      alert("Please select a time slot.");
      return;
    }

    const token = localStorage.getItem("token");
    const appointmentData = {
      doctor:    { id: doctor.id },
      patient:   { id: patientData.id },
      appointmentTime: selectedTime,
      status: 0,
    };

    try {
      const response = await fetch(`${API_BASE_URL}/appointment?token=${token}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(appointmentData),
      });

      if (response.ok) {
        alert("Appointment booked successfully!");
        closeOverlay();
      } else {
        const err = await response.json();
        alert(err.message || "Failed to book appointment.");
      }
    } catch (error) {
      console.error("Booking error:", error);
      alert("An error occurred while booking. Please try again.");
    }
  });

  // ── Cancel / close ──────────────────────────────────────────────
  document.getElementById("cancelBookingBtn").addEventListener("click", closeOverlay);
  backdrop.addEventListener("click", closeOverlay);

  function closeOverlay() {
    overlay.classList.remove("active");
    backdrop.classList.remove("active");
  }
}
