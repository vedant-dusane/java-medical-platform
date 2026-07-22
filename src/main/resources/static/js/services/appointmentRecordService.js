/**
 * appointmentRecordService.js
 * Fetches doctor appointment records from the backend.
 * Used by: doctorDashboard.js
 */

import { API_BASE_URL } from "../config/config.js";

const APPOINTMENT_API = API_BASE_URL + "/appointments";

/**
 * Fetches all appointments for the logged-in doctor,
 * optionally filtered by date and patient name.
 *
 * @param {string} date        – Date string "YYYY-MM-DD".
 * @param {string} patientName – Patient name filter, or "null".
 * @param {string} token       – Doctor JWT token.
 * @returns {Promise<Array>}   Array of appointment objects, or [].
 */
export async function getAllAppointments(date, patientName, token) {
  try {
    const url = `${APPOINTMENT_API}/${date}/${patientName || "null"}/${token}`;

    const response = await fetch(url, {
      method: "GET",
      headers: { "Content-Type": "application/json" },
    });

    if (!response.ok) {
      console.error("getAllAppointments() failed:", response.status);
      return [];
    }

    const data = await response.json();
    return data.appointments || [];
  } catch (error) {
    console.error("getAllAppointments() error:", error);
    return [];
  }
}
