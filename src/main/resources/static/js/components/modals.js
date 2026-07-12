/**
 * modals.js
 * Manages the shared modal dialog used across all pages.
 *
 * openModal(type)  – renders the correct form inside #modal-body
 *                    and displays the modal overlay.
 * closeModal()     – hides the modal and clears its content.
 *
 * Supported modal types:
 *   "adminLogin"    – Admin username/password form
 *   "doctorLogin"   – Doctor email/password form
 *   "patientLogin"  – Patient email/password form
 *   "patientSignup" – Patient registration form
 *   "addDoctor"     – Add new doctor form (admin only)
 */

// ── Modal element references ──────────────────────────────────────────
const modal     = document.getElementById("modal");
const modalBody = document.getElementById("modal-body");
const closeBtn  = document.getElementById("closeModal");

// Close button listener
if (closeBtn) {
  closeBtn.addEventListener("click", closeModal);
}

// Close when clicking the dark backdrop (outside modal-content)
if (modal) {
  modal.addEventListener("click", (e) => {
    if (e.target === modal) closeModal();
  });
}

// ── Public: open a modal by type ──────────────────────────────────────
export function openModal(type) {
  if (!modal || !modalBody) return;

  modalBody.innerHTML = getModalContent(type);
  modal.classList.add("active");
  modal.style.display = "flex";
}

// ── Public: close the modal ───────────────────────────────────────────
export function closeModal() {
  if (!modal) return;
  modal.classList.remove("active");
  modal.style.display = "none";
  if (modalBody) modalBody.innerHTML = "";
}

// ── Build modal HTML by type ──────────────────────────────────────────
function getModalContent(type) {
  switch (type) {

    case "adminLogin":
      return `
        <h2 style="margin-bottom:20px;color:#014d4e;">Admin Login</h2>
        <div class="form-group">
          <label for="adminUsername">Username</label>
          <input type="text" id="adminUsername" class="input-field" placeholder="Enter username" />
        </div>
        <div class="form-group">
          <label for="adminPassword">Password</label>
          <input type="password" id="adminPassword" class="input-field" placeholder="Enter password" />
        </div>
        <button
          class="dashboard-btn"
          style="width:100%;margin-top:12px;"
          onclick="adminLoginHandler()">
          Login
        </button>`;

    case "doctorLogin":
      return `
        <h2 style="margin-bottom:20px;color:#014d4e;">Doctor Login</h2>
        <div class="form-group">
          <label for="doctorEmail">Email</label>
          <input type="email" id="doctorEmail" class="input-field" placeholder="Enter email" />
        </div>
        <div class="form-group">
          <label for="doctorPassword">Password</label>
          <input type="password" id="doctorPassword" class="input-field" placeholder="Enter password" />
        </div>
        <button
          class="dashboard-btn"
          style="width:100%;margin-top:12px;"
          onclick="doctorLoginHandler()">
          Login
        </button>`;

    case "patientLogin":
      return `
        <h2 style="margin-bottom:20px;color:#014d4e;">Patient Login</h2>
        <div class="form-group">
          <label for="patientEmail">Email</label>
          <input type="email" id="patientEmail" class="input-field" placeholder="Enter email" />
        </div>
        <div class="form-group">
          <label for="patientPassword">Password</label>
          <input type="password" id="patientPassword" class="input-field" placeholder="Enter password" />
        </div>
        <button
          class="dashboard-btn"
          style="width:100%;margin-top:12px;"
          onclick="patientLoginHandler()">
          Login
        </button>`;

    case "patientSignup":
      return `
        <h2 style="margin-bottom:20px;color:#014d4e;">Patient Sign Up</h2>
        <div class="form-group">
          <label for="signupName">Full Name</label>
          <input type="text" id="signupName" class="input-field" placeholder="Enter full name" />
        </div>
        <div class="form-group">
          <label for="signupEmail">Email</label>
          <input type="email" id="signupEmail" class="input-field" placeholder="Enter email" />
        </div>
        <div class="form-group">
          <label for="signupPhone">Phone</label>
          <input type="text" id="signupPhone" class="input-field" placeholder="10-digit phone number" />
        </div>
        <div class="form-group">
          <label for="signupAddress">Address</label>
          <input type="text" id="signupAddress" class="input-field" placeholder="Enter address" />
        </div>
        <div class="form-group">
          <label for="signupPassword">Password</label>
          <input type="password" id="signupPassword" class="input-field" placeholder="Min 6 characters" />
        </div>
        <button
          class="dashboard-btn"
          style="width:100%;margin-top:12px;"
          onclick="patientSignupHandler()">
          Sign Up
        </button>`;

    case "addDoctor":
      return `
        <h2 style="margin-bottom:20px;color:#014d4e;">Add New Doctor</h2>
        <div class="form-group">
          <label for="docName">Full Name</label>
          <input type="text" id="docName" class="input-field" placeholder="Doctor's full name" />
        </div>
        <div class="form-group">
          <label for="docSpecialty">Specialty</label>
          <input type="text" id="docSpecialty" class="input-field" placeholder="e.g. Cardiology" />
        </div>
        <div class="form-group">
          <label for="docEmail">Email</label>
          <input type="email" id="docEmail" class="input-field" placeholder="Doctor's email" />
        </div>
        <div class="form-group">
          <label for="docPhone">Phone</label>
          <input type="text" id="docPhone" class="input-field" placeholder="10-digit phone number" />
        </div>
        <div class="form-group">
          <label for="docPassword">Password</label>
          <input type="password" id="docPassword" class="input-field" placeholder="Min 6 characters" />
        </div>
        <div class="form-group">
          <label>Available Times</label>
          <div class="checkbox-group" id="availableTimesGroup">
            <label><input type="checkbox" value="09:00-10:00" /> 09:00 – 10:00</label>
            <label><input type="checkbox" value="10:00-11:00" /> 10:00 – 11:00</label>
            <label><input type="checkbox" value="11:00-12:00" /> 11:00 – 12:00</label>
            <label><input type="checkbox" value="14:00-15:00" /> 14:00 – 15:00</label>
            <label><input type="checkbox" value="15:00-16:00" /> 15:00 – 16:00</label>
            <label><input type="checkbox" value="16:00-17:00" /> 16:00 – 17:00</label>
          </div>
        </div>
        <button
          class="dashboard-btn"
          style="width:100%;margin-top:12px;"
          onclick="addDoctorHandler()">
          Add Doctor
        </button>`;

    default:
      return `<p>Unknown modal type: ${type}</p>`;
  }
}
