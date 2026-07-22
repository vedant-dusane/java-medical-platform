/**
 * patientRows.js
 * Creates a <tr> row element for each patient appointment in the doctor dashboard.
 * Supports both nested patient objects and flat AppointmentDTO fields.
 */

export function createPatientRow(appointment) {
  const row = document.createElement("tr");

  // Read fields with fallback for both nested and flat DTO properties
  const patientId    = appointment.patientId    || appointment.patient?.id    || "—";
  const patientName  = appointment.patientName  || appointment.patient?.name  || "—";
  const patientPhone = appointment.patientPhone || appointment.patient?.phone || "—";
  const patientEmail = appointment.patientEmail || appointment.patient?.email || "—";

  // Patient ID
  const idCell = document.createElement("td");
  idCell.textContent = patientId;

  // Patient Name
  const nameCell = document.createElement("td");
  nameCell.textContent = patientName;

  // Phone
  const phoneCell = document.createElement("td");
  phoneCell.textContent = patientPhone;

  // Email
  const emailCell = document.createElement("td");
  emailCell.textContent = patientEmail;

  // Prescription button
  const prescCell = document.createElement("td");
  const prescBtn  = document.createElement("button");
  prescBtn.textContent = "Add Prescription";
  prescBtn.classList.add("prescription-btn");
  prescBtn.setAttribute("data-appointment-id", appointment.id);

  // Navigate to Add Prescription page with context stored in localStorage
  prescBtn.addEventListener("click", () => {
    localStorage.setItem("selectedAppointmentId", appointment.id);
    localStorage.setItem("selectedPatientId", patientId);
    localStorage.setItem("selectedPatientName", patientName);
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
