/**
 * index.js – Role-Based Login Handling (Landing Page Service)
 *
 * Responsibilities:
 *  - Wire up Admin and Doctor login buttons to their respective modals.
 *  - Handle Admin login (POST /api/admin).
 *  - Handle Doctor login (POST /api/doctor/login).
 *  - Store JWT token in localStorage on success.
 *  - Call selectRole() from render.js to redirect to the correct dashboard.
 */

import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

// ── API endpoint constants ────────────────────────────────────────────
const ADMIN_API  = API_BASE_URL + "/admin";
const DOCTOR_API = API_BASE_URL + "/doctor/login";

// ── Wire up role selection buttons after page load ────────────────────
window.onload = function () {
  // Admin login button (on index.html role-selection screen)
  const adminBtn = document.getElementById("adminBtn");
  if (adminBtn) {
    adminBtn.addEventListener("click", () => {
      selectRole("admin");          // save role first
      openModal("adminLogin");      // then show login modal
    });
  }

  // Doctor login button
  const doctorBtn = document.getElementById("doctorBtn");
  if (doctorBtn) {
    doctorBtn.addEventListener("click", () => {
      selectRole("doctor");
      openModal("doctorLogin");
    });
  }

  // Patient button — no login modal, just navigate
  const patientBtn = document.getElementById("patientBtn");
  if (patientBtn) {
    patientBtn.addEventListener("click", () => {
      selectRole("patient");
    });
  }
};

// ── Admin Login Handler ───────────────────────────────────────────────
/**
 * Called by the inline onclick on the Admin login modal button.
 * Reads credentials, POSTs to /api/admin, stores token on success.
 */
window.adminLoginHandler = async function () {
  const username = document.getElementById("adminUsername")?.value?.trim();
  const password = document.getElementById("adminPassword")?.value?.trim();

  if (!username || !password) {
    alert("Please enter both username and password.");
    return;
  }

  const admin = { username, password };

  try {
    const response = await fetch(ADMIN_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(admin),
    });

    if (response.ok) {
      const data = await response.json();
      localStorage.setItem("token", data.token);
      selectRole("admin");
    } else {
      alert("Invalid credentials! Please try again.");
    }
  } catch (error) {
    console.error("Admin login error:", error);
    alert("An error occurred during login. Please check your connection.");
  }
};

// ── Doctor Login Handler ──────────────────────────────────────────────
/**
 * Called by the inline onclick on the Doctor login modal button.
 * Reads credentials, POSTs to /api/doctor/login, stores token on success.
 */
window.doctorLoginHandler = async function () {
  const email    = document.getElementById("doctorEmail")?.value?.trim();
  const password = document.getElementById("doctorPassword")?.value?.trim();

  if (!email || !password) {
    alert("Please enter both email and password.");
    return;
  }

  const doctor = { email, password };

  try {
    const response = await fetch(DOCTOR_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(doctor),
    });

    if (response.ok) {
      const data = await response.json();
      localStorage.setItem("token", data.token);
      selectRole("doctor");
    } else {
      alert("Invalid credentials! Please try again.");
    }
  } catch (error) {
    console.error("Doctor login error:", error);
    alert("An error occurred during login. Please check your connection.");
  }
};
