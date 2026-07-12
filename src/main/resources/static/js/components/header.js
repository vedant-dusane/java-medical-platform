/**
 * header.js
 * Renders a dynamic, role-aware header into the #header div on every page.
 * Roles: admin | doctor | patient | loggedPatient
 */

function renderHeader() {
  // ── 1. Clear auth data on the root/home page ──────────────────────
  if (window.location.pathname.endsWith("/") ||
      window.location.pathname.endsWith("/index.html")) {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
  }

  const headerDiv = document.getElementById("header");
  if (!headerDiv) return;

  // ── 2. Read role and token from localStorage ──────────────────────
  const role  = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  // ── 3. Guard: if privileged role has no token, session is invalid ─
  if (
    (role === "loggedPatient" || role === "admin" || role === "doctor") &&
    !token
  ) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  // ── 4. Build header HTML based on role ───────────────────────────
  let headerContent = `
    <div class="header">
      <div class="header-brand">
        <img src="/assets/images/logo/logo.png" alt="Smart Clinic Logo" class="logo-img"
             onerror="this.style.display='none'" />
        <span class="site-title">Smart Clinic</span>
      </div>
      <nav class="header-nav">
  `;

  if (role === "admin") {
    headerContent += `
        <button id="addDocBtn" class="add-btn adminBtn">Add Doctor</button>
        <a href="#" id="logoutBtn">Logout</a>
    `;
  } else if (role === "doctor") {
    headerContent += `
        <a href="/doctor/dashboard" id="homeBtn">Home</a>
        <a href="#" id="logoutBtn">Logout</a>
    `;
  } else if (role === "patient") {
    headerContent += `
        <button id="loginBtn" class="add-btn">Login</button>
        <button id="signupBtn" class="add-btn">Sign Up</button>
    `;
  } else if (role === "loggedPatient") {
    headerContent += `
        <a href="/pages/patientDashboard.html" id="homeBtn">Home</a>
        <a href="/pages/patientAppointments.html" id="appointmentsBtn">Appointments</a>
        <a href="#" id="logoutPatientBtn">Logout</a>
    `;
  }

  headerContent += `
      </nav>
    </div>
  `;

  // ── 5. Inject into the DOM ────────────────────────────────────────
  headerDiv.innerHTML = headerContent;

  // ── 6. Attach event listeners after injection ─────────────────────
  attachHeaderButtonListeners();
}

// ── Event listener attachment ────────────────────────────────────────
function attachHeaderButtonListeners() {
  // Add Doctor button (admin)
  const addDocBtn = document.getElementById("addDocBtn");
  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => {
      if (typeof openModal === "function") {
        openModal("addDoctor");
      }
    });
  }

  // Logout (admin / doctor)
  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", (e) => {
      e.preventDefault();
      logout();
    });
  }

  // Logout (patient)
  const logoutPatientBtn = document.getElementById("logoutPatientBtn");
  if (logoutPatientBtn) {
    logoutPatientBtn.addEventListener("click", (e) => {
      e.preventDefault();
      logoutPatient();
    });
  }

  // Login button (patient, not logged in)
  const loginBtn = document.getElementById("loginBtn");
  if (loginBtn) {
    loginBtn.addEventListener("click", () => {
      if (typeof openModal === "function") {
        openModal("patientLogin");
      }
    });
  }

  // Sign Up button (patient, not logged in)
  const signupBtn = document.getElementById("signupBtn");
  if (signupBtn) {
    signupBtn.addEventListener("click", () => {
      if (typeof openModal === "function") {
        openModal("patientSignup");
      }
    });
  }
}

// ── Logout: admin / doctor ───────────────────────────────────────────
function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole");
  window.location.href = "/";
}

// ── Logout: patient (keeps role as "patient" for login/signup header) ─
function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient");
  window.location.href = "/pages/patientDashboard.html";
}

// ── Auto-render when script loads ───────────────────────────────────
renderHeader();
