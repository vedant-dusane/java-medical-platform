/**
 * patientRows.js
 * Creates a <tr> row element for each patient appointment in the doctor dashboard.
 * Used by: doctorDashboard.js
 */

/**
 * Builds and returns a <tr> element representing one patient appointment row.
 *
 * @param {Object} appointment – Appointment object from the API.
 * @returns {HTMLTableRowElement}
 */
export function createPatientRow(appointment) {
  const patient = appointment.patient || {};

  const row = document.createElement("tr");

  // Patient ID
  const idCell = document.createElement("td");
  idCell.textContent = patient.id || "—";

  // Patient Name
  const nameCell = document.createElement("td");
  nameCell.textContent = patient.name || "—";

  // Phone
  const phoneCell = document.createElement("td");
  phoneCell.textContent = patient.phone || "—";

  // Email
  const emailCell = document.createElement("td");
  emailCell.textContent = patient.email || "—";

  // Prescription button
  const prescCell = document.createElement("td");
  const prescBtn  = document.createElement("button");
  prescBtn.textContent = "Add Prescription";
  prescBtn.classList.add("prescription-btn");
  prescBtn.setAttribute("data-appointment-id", appointment.id);
  prescBtn.setAttribute("data-patient-id", patient.id);

  // Navigate to Add Prescription page with context
  prescBtn.addEventListener("click", () => {
    localStorage.setItem("selectedAppointmentId", appointment.id);
    localStorage.setItem("selectedPatientId", patient.id);
    localStorage.setItem("selectedPatientName", patient.name);
    window.location.href = "/pages/addPrescription.html";
  });

  prescCell.appendChild(prescBtn);

  row.appendChild(idCell);
  row.appendChild(nameCell);
  row.appendChild(phoneCell);
  row.appendChild(emailCell);
  row.appendChild(prescCell);

  return row;
}
