/**
 * patientDashboard.js
 * Manages the Patient Dashboard:
 *  - Load and display all available doctor cards.
 *  - Real-time search and specialty/time filtering.
 *  - Open Login / Signup modals.
 *  - Handle patient signup and login form submissions.
 *
 * Imports:
 *   createDoctorCard           from ./components/doctorCard.js
 *   openModal                  from ./components/modals.js
 *   getDoctors, filterDoctors  from ./services/doctorServices.js
 *   patientLogin, patientSignup from ./services/patientServices.js
 */

import { createDoctorCard }           from "./components/doctorCard.js";
import { openModal }                  from "./components/modals.js";
import { getDoctors, filterDoctors }  from "./services/doctorServices.js";
import { patientLogin, patientSignup } from "./services/patientServices.js";

// ─────────────────────────────────────────────────────────────────────
// Shared content container
// ─────────────────────────────────────────────────────────────────────
const contentDiv = document.getElementById("content");

// ─────────────────────────────────────────────────────────────────────
// Load all doctor cards
// ─────────────────────────────────────────────────────────────────────
async function loadDoctorCards() {
  // Fetch full doctor list from backend
  const doctors = await getDoctors();

  // Clear existing content
  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p class='noPatientRecord'>No doctors available.</p>";
    return;
  }

  // Render each doctor card
  renderDoctorCards(doctors);
}

// ─────────────────────────────────────────────────────────────────────
// Utility: render a list of doctor objects as cards
// ─────────────────────────────────────────────────────────────────────
export function renderDoctorCards(doctors) {
  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p class='noPatientRecord'>No doctors found with the given filters.</p>";
    return;
  }

  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// ─────────────────────────────────────────────────────────────────────
// Search + filter: triggered on input / dropdown change
// ─────────────────────────────────────────────────────────────────────
async function filterDoctorsOnChange() {
  const name      = document.getElementById("searchBar")?.value?.trim()    || "";
  const time      = document.getElementById("filterTime")?.value           || "";
  const specialty = document.getElementById("filterSpecialty")?.value      || "";

  const doctors = await filterDoctors(name, time, specialty);

  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p class='noPatientRecord'>No doctors found with the given filters.</p>";
    return;
  }

  renderDoctorCards(doctors);
}

// ─────────────────────────────────────────────────────────────────────
// Patient Signup handler (called by modal button onclick)
// ─────────────────────────────────────────────────────────────────────
window.signupPatient = async function () {
  const name     = document.getElementById("signupName")?.value?.trim();
  const email    = document.getElementById("signupEmail")?.value?.trim();
  const phone    = document.getElementById("signupPhone")?.value?.trim();
  const address  = document.getElementById("signupAddress")?.value?.trim();
  const password = document.getElementById("signupPassword")?.value?.trim();

  if (!name || !email || !phone || !address || !password) {
    alert("Please fill in all required fields.");
    return;
  }

  const data = { name, email, phone, address, password };

  // Call patientSignup service
  const result = await patientSignup(data);

  if (result.success) {
    alert(result.message || "Registration successful! Please log in.");
    // Close modal and reload page
    const modal = document.getElementById("modal");
    if (modal) {
      modal.classList.remove("active");
      modal.style.display = "none";
    }
    window.location.reload();
  } else {
    alert(result.message || "Signup failed. Please try again.");
  }
};

// ─────────────────────────────────────────────────────────────────────
// Patient Login handler (called by modal button onclick)
// ─────────────────────────────────────────────────────────────────────
window.loginPatient = async function () {
  const email    = document.getElementById("patientEmail")?.value?.trim();
  const password = document.getElementById("patientPassword")?.value?.trim();

  if (!email || !password) {
    alert("Please enter your email and password.");
    return;
  }

  const data = { email, password };

  try {
    // Call patientLogin service — returns raw Response
    const response = await patientLogin(data);

    if (response.ok) {
      const result = await response.json();
      // Store JWT token and switch role to loggedPatient
      localStorage.setItem("token", result.token);
      localStorage.setItem("userRole", "loggedPatient");
      // Redirect to logged-in patient dashboard
      window.location.href = "/pages/loggedPatientDashboard.html";
    } else {
      const err = await response.json();
      alert(err.message || "Invalid credentials! Please try again.");
    }
  } catch (error) {
    console.error("loginPatient() error:", error);
    alert("An error occurred during login. Please try again.");
  }
};

// ─────────────────────────────────────────────────────────────────────
// DOMContentLoaded – wire up everything
// ─────────────────────────────────────────────────────────────────────
document.addEventListener("DOMContentLoaded", () => {
  // Load doctor cards on page load
  loadDoctorCards();

  // Signup button
  const signupBtn = document.getElementById("patientSignup");
  if (signupBtn) {
    signupBtn.addEventListener("click", () => openModal("patientSignup"));
  }

  // Login button
  const loginBtn = document.getElementById("patientLogin");
  if (loginBtn) {
    loginBtn.addEventListener("click", () => openModal("patientLogin"));
  }

  // Real-time search and filter listeners
  document.getElementById("searchBar")
    ?.addEventListener("input", filterDoctorsOnChange);
  document.getElementById("filterTime")
    ?.addEventListener("change", filterDoctorsOnChange);
  document.getElementById("filterSpecialty")
    ?.addEventListener("change", filterDoctorsOnChange);
});
