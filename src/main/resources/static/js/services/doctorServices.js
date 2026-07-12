/**
 * doctorServices.js
 * Centralizes all API interactions related to doctor data.
 * Used by: Admin Dashboard, Patient Dashboard
 *
 * Functions:
 *   getDoctors()                          – fetch all doctors
 *   deleteDoctor(id, token)               – delete a doctor by ID (admin only)
 *   saveDoctor(doctor, token)             – add a new doctor (admin only)
 *   filterDoctors(name, time, specialty)  – filter doctors by criteria
 */

import { API_BASE_URL } from "../config/config.js";

// ── Base endpoint ─────────────────────────────────────────────────────
const DOCTOR_API = API_BASE_URL + "/doctor";

// ─────────────────────────────────────────────────────────────────────
// GET all doctors
// ─────────────────────────────────────────────────────────────────────
/**
 * Fetches the full list of doctors from the backend.
 * @returns {Promise<Array>} Array of doctor objects, or [] on error.
 */
export async function getDoctors() {
  try {
    // Send GET request to the doctor endpoint
    const response = await fetch(DOCTOR_API, {
      method: "GET",
      headers: { "Content-Type": "application/json" },
    });

    // Extract and return the list from the response JSON
    const data = await response.json();
    return data || [];
  } catch (error) {
    console.error("getDoctors() error:", error);
    // Return empty list to avoid breaking the frontend
    return [];
  }
}

// ─────────────────────────────────────────────────────────────────────
// DELETE a doctor
// ─────────────────────────────────────────────────────────────────────
/**
 * Deletes a doctor by ID. Requires an admin JWT token.
 * @param {number|string} id    – Doctor's unique identifier.
 * @param {string}        token – Admin authentication token.
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function deleteDoctor(id, token) {
  try {
    // Construct the full URL with id and token as query param
    const url = `${DOCTOR_API}/${id}?token=${token}`;

    const response = await fetch(url, {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
    });

    const data = await response.json();

    // Return structured success/message for the caller
    return {
      success: response.ok,
      message: data.message || (response.ok ? "Doctor deleted." : "Delete failed."),
    };
  } catch (error) {
    console.error("deleteDoctor() error:", error);
    return { success: false, message: "An error occurred while deleting the doctor." };
  }
}

// ─────────────────────────────────────────────────────────────────────
// POST – save (add) a new doctor
// ─────────────────────────────────────────────────────────────────────
/**
 * Saves a new doctor record. Requires an admin JWT token.
 * @param {Object} doctor – Doctor details (name, email, specialty, etc.).
 * @param {string} token  – Admin authentication token.
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function saveDoctor(doctor, token) {
  try {
    // POST with JSON body; pass token as query param for auth
    const response = await fetch(`${DOCTOR_API}?token=${token}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(doctor),
    });

    const data = await response.json();

    return {
      success: response.ok,
      message: data.message || (response.ok ? "Doctor added successfully." : "Failed to add doctor."),
    };
  } catch (error) {
    console.error("saveDoctor() error:", error);
    return { success: false, message: "An error occurred while saving the doctor." };
  }
}

// ─────────────────────────────────────────────────────────────────────
// GET – filter doctors
// ─────────────────────────────────────────────────────────────────────
/**
 * Filters doctors by name, available time, and/or specialty.
 * @param {string} name      – Doctor name (or empty string).
 * @param {string} time      – Time slot filter e.g. "AM" / "PM" (or empty).
 * @param {string} specialty – Medical specialty (or empty string).
 * @returns {Promise<Array>} Filtered doctor list, or [] on error.
 */
export async function filterDoctors(name, time, specialty) {
  try {
    // Build the filter URL with route / query parameters
    const url = `${DOCTOR_API}/filter/${name || "null"}/${time || "null"}/${specialty || "null"}`;

    const response = await fetch(url, {
      method: "GET",
      headers: { "Content-Type": "application/json" },
    });

    if (!response.ok) {
      alert("No doctors found matching the selected filters.");
      return [];
    }

    const data = await response.json();
    return data || [];
  } catch (error) {
    console.error("filterDoctors() error:", error);
    alert("An error occurred while filtering doctors.");
    return [];
  }
}
