/**
 * adminDashboard.js
 * Manages the Admin Dashboard:
 *  - Load and display all doctor cards.
 *  - Search and filter doctors in real-time.
 *  - Open "Add Doctor" modal and handle form submission.
 *
 * Imports:
 *   openModal        from ../components/modals.js
 *   getDoctors,
 *   filterDoctors,
 *   saveDoctor       from ./services/doctorServices.js
 *   createDoctorCard from ../components/doctorCard.js   (used via dynamic import in card)
 */

import { openModal, closeModal } from "./components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

// ── Shared content container reference ───────────────────────────────
const contentDiv = document.getElementById("content");

// ─────────────────────────────────────────────────────────────────────
// Load all doctor cards on page load
// ─────────────────────────────────────────────────────────────────────
async function loadDoctorCards() {
  // Fetch all doctors from backend
  const doctors = await getDoctors();

  // Clear existing content
  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p class='noPatientRecord'>No doctors found.</p>";
    return;
  }

  // Render a card for each doctor
  renderDoctorCards(doctors);
}

// ─────────────────────────────────────────────────────────────────────
// Utility: render a list of doctors as cards into #content
// ─────────────────────────────────────────────────────────────────────
function renderDoctorCards(doctors) {
  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p class='noPatientRecord'>No doctors found.</p>";
    return;
  }

  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// ─────────────────────────────────────────────────────────────────────
// Search + filter: called on every input/change event
// ─────────────────────────────────────────────────────────────────────
async function filterDoctorsOnChange() {
  const name      = document.getElementById("searchBar")?.value?.trim()    || "";
  const time      = document.getElementById("filterTime")?.value           || "";
  const specialty = document.getElementById("filterSpecialty")?.value      || "";

  // Fetch filtered list from backend
  const doctors = await filterDoctors(name, time, specialty);

  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p class='noPatientRecord'>No doctors found.</p>";
    return;
  }

  renderDoctorCards(doctors);
}

// ─────────────────────────────────────────────────────────────────────
// Handle "Add Doctor" form submission (called by modal button onclick)
// ─────────────────────────────────────────────────────────────────────
window.addDoctorHandler = async function () {
  // Collect form values
  const name      = document.getElementById("docName")?.value?.trim();
  const specialty = document.getElementById("docSpecialty")?.value?.trim();
  const email     = document.getElementById("docEmail")?.value?.trim();
  const phone     = document.getElementById("docPhone")?.value?.trim();
  const password  = document.getElementById("docPassword")?.value?.trim();

  // Collect selected availability checkboxes
  const checkedBoxes = document.querySelectorAll(
    "#availableTimesGroup input[type='checkbox']:checked"
  );
  const availableTimes = Array.from(checkedBoxes).map((cb) => cb.value);

  // Validate required fields
  if (!name || !specialty || !email || !phone || !password) {
    alert("Please fill in all required fields.");
    return;
  }

  // Verify admin token exists
  const token = localStorage.getItem("token");
  if (!token) {
    alert("Session expired. Please log in again.");
    window.location.href = "/";
    return;
  }

  // Build doctor object
  const doctor = { name, specialty, email, phone, password, availableTimes };

  // POST to backend via saveDoctor()
  const result = await saveDoctor(doctor, token);

  if (result.success) {
    alert(result.message || "Doctor added successfully!");
    closeModal();
    // Refresh the doctor list
    await loadDoctorCards();
  } else {
    alert(result.message || "Failed to add doctor. Please try again.");
  }
};

// ─────────────────────────────────────────────────────────────────────
// Event listeners
// ─────────────────────────────────────────────────────────────────────
document.addEventListener("DOMContentLoaded", async () => {
  // Load all doctors on page load
  await loadDoctorCards();

  // Add Doctor button → open modal
  const addDocBtn = document.getElementById("addDocBtn");
  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => openModal("addDoctor"));
  }

  // Real-time search and filter
  document.getElementById("searchBar")
    ?.addEventListener("input", filterDoctorsOnChange);
  document.getElementById("filterTime")
    ?.addEventListener("change", filterDoctorsOnChange);
  document.getElementById("filterSpecialty")
    ?.addEventListener("change", filterDoctorsOnChange);
});
