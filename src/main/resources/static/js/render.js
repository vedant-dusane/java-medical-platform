/**
 * render.js
 * Provides selectRole() and renderContent() helpers used across pages.
 *
 * selectRole(role) – saves the chosen role to localStorage and
 *                    redirects the user to the correct dashboard (with token).
 *
 * renderContent()  – called on page load to kick off role-aware data rendering.
 */

function selectRole(role) {
  localStorage.setItem("userRole", role);
  const token = localStorage.getItem("token");

  switch (role) {
    case "admin":
      // Redirect only after login — token carries auth to DashboardController
      if (token) {
        window.location.href = "/adminDashboard/" + token;
      }
      break;
    case "doctor":
      if (token) {
        window.location.href = "/doctorDashboard/" + token;
      }
      break;
    case "patient":
      window.location.href = "/pages/patientDashboard.html";
      break;
    case "loggedPatient":
      window.location.href = "/pages/loggedPatientDashboard.html";
      break;
    default:
      window.location.href = "/";
  }
}

window.selectRole = selectRole;

function renderContent() {
  console.log("renderContent() called – page:", window.location.pathname);
}

window.renderContent = renderContent;
