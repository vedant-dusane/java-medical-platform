/**
 * render.js
 * Provides selectRole() and renderContent() helpers used across pages.
 *
 * selectRole(role) – saves the chosen role to localStorage and
 *                    redirects the user to the correct dashboard.
 *
 * renderContent()  – called on page load (e.g. body onload="renderContent()")
 *                    to kick off role-aware data rendering.
 */

/**
 * Saves the selected role in localStorage and navigates to the
 * appropriate dashboard page.
 *
 * @param {string} role – "admin" | "doctor" | "patient" | "loggedPatient"
 */
function selectRole(role) {
  localStorage.setItem("userRole", role);

  switch (role) {
    case "admin":
      window.location.href = "/admin/dashboard";
      break;
    case "doctor":
      window.location.href = "/doctor/dashboard";
      break;
    case "patient":
      window.location.href = "/pages/patientDashboard.html";
      break;
    case "loggedPatient":
      window.location.href = "/pages/patientDashboard.html";
      break;
    default:
      window.location.href = "/";
  }
}

/**
 * Called via body onload="renderContent()" on dashboard pages.
 * Triggers any page-specific initialisation that requires the DOM
 * to be fully ready (e.g. loading doctor cards for patients).
 */
function renderContent() {
  // Each dashboard's own module handles its data loading.
  // This function acts as the entry-point hook so HTML pages can
  // call a single, consistent function name on load.
  console.log("renderContent() called – page:", window.location.pathname);
}
