/**
 * doctorDashboard.js
 * Manages the Doctor Dashboard:
 *  - Load patient appointment rows for today or a selected date.
 *  - Search appointments by patient name.
 *  - Navigate to Add Prescription page.
 *
 * Imports:
 *   getAllAppointments from ./services/appointmentRecordService.js
 *   createPatientRow  from ./components/patientRows.js
 */

import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow }   from "./components/patientRows.js";

// ─────────────────────────────────────────────────────────────────────
// Global variables
// ─────────────────────────────────────────────────────────────────────

// Reference to the table body where rows will be rendered
const tableBody = document.getElementById("patientTableBody");

// Default selected date = today (YYYY-MM-DD)
let selectedDate = new Date().toISOString().split("T")[0];

// Auth token from localStorage for API calls
const token = localStorage.getItem("token");

// Patient name filter — null means "show all"
let patientName = "null";

// ─────────────────────────────────────────────────────────────────────
// Load and render appointment rows
// ─────────────────────────────────────────────────────────────────────
async function loadAppointments() {
  // Clear the table body before loading new rows
  tableBody.innerHTML = "";

  try {
    // Fetch appointments for selectedDate + patientName filter
    const appointments = await getAllAppointments(selectedDate, patientName, token);

    if (!appointments || appointments.length === 0) {
      // Show a friendly "no results" message row
      tableBody.innerHTML = `
        <tr>
          <td colspan="5" class="noPatientRecord">
            No appointments found for today.
          </td>
        </tr>`;
      return;
    }

    // Render a row for each appointment
    appointments.forEach((appointment) => {
      const row = createPatientRow(appointment);
      tableBody.appendChild(row);
    });

  } catch (error) {
    console.error("loadAppointments() error:", error);
    // Fallback error row
    tableBody.innerHTML = `
      <tr>
        <td colspan="5" class="noPatientRecord">
          An error occurred while loading appointments.
        </td>
      </tr>`;
  }
}

// ─────────────────────────────────────────────────────────────────────
// Event listeners
// ─────────────────────────────────────────────────────────────────────
document.addEventListener("DOMContentLoaded", () => {
  // Initialise the date picker to today
  const datePicker = document.getElementById("dateFilter");
  if (datePicker) {
    datePicker.value = selectedDate;

    // Update selectedDate when doctor picks a date
    datePicker.addEventListener("change", () => {
      selectedDate = datePicker.value;
      loadAppointments();
    });
  }

  // "Today's Appointments" button — reset to today
  const todayBtn = document.getElementById("todayBtn");
  if (todayBtn) {
    todayBtn.addEventListener("click", () => {
      selectedDate = new Date().toISOString().split("T")[0];
      if (datePicker) datePicker.value = selectedDate;
      loadAppointments();
    });
  }

  // Search bar — filter by patient name
  const searchBar = document.getElementById("searchBar");
  if (searchBar) {
    searchBar.addEventListener("input", () => {
      const val = searchBar.value.trim();
      // Default to "null" when empty so the API shows all
      patientName = val.length > 0 ? val : "null";
      loadAppointments();
    });
  }

  // Trigger renderContent() hook (defined in render.js)
  if (typeof renderContent === "function") renderContent();

  // Initial load — today's appointments
  loadAppointments();
});
