/**
 * loggedPatientDashboard.js
 * Manages the Logged-In Patient Dashboard:
 *  - Load and display all available doctor cards.
 *  - Real-time search and specialty/time filtering.
 *  - Doctor cards show the real "Book Now" flow via showBookingOverlay().
 *
 * localStorage.userRole must be "loggedPatient" when this page loads.
 */

import { createDoctorCard }          from "./components/doctorCard.js";
import { getDoctors, filterDoctors } from "./services/doctorServices.js";

// ─────────────────────────────────────────────────────────────────────
// Shared content container
// ─────────────────────────────────────────────────────────────────────
const contentDiv = document.getElementById("content");

// ─────────────────────────────────────────────────────────────────────
// Load all doctor cards
// ─────────────────────────────────────────────────────────────────────
async function loadDoctorCards() {
  contentDiv.innerHTML = "<p class='noPatientRecord'>Loading doctors…</p>";
  const doctors = await getDoctors();
  renderDoctorCards(doctors);
}

// ─────────────────────────────────────────────────────────────────────
// Render helpers
// ─────────────────────────────────────────────────────────────────────
function renderDoctorCards(doctors) {
  contentDiv.innerHTML = "";
  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p class='noPatientRecord'>No doctors available.</p>";
    return;
  }
  doctors.forEach((doctor) => {
    contentDiv.appendChild(createDoctorCard(doctor));
  });
}

// ─────────────────────────────────────────────────────────────────────
// Filter handler – called on search/dropdown change
// ─────────────────────────────────────────────────────────────────────
async function filterDoctorsOnChange() {
  const name      = document.getElementById("searchBar")?.value?.trim()    || "";
  const time      = document.getElementById("filterTime")?.value           || "";
  const specialty = document.getElementById("filterSpecialty")?.value      || "";

  const doctors = await filterDoctors(name, time, specialty);
  renderDoctorCards(doctors);
}

// ─────────────────────────────────────────────────────────────────────
// DOMContentLoaded – wire up events
// ─────────────────────────────────────────────────────────────────────
document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();

  document.getElementById("searchBar")
    ?.addEventListener("input", filterDoctorsOnChange);
  document.getElementById("filterTime")
    ?.addEventListener("change", filterDoctorsOnChange);
  document.getElementById("filterSpecialty")
    ?.addEventListener("change", filterDoctorsOnChange);
});
