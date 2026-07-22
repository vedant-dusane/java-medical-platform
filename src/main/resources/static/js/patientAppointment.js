/**
 * patientAppointment.js
 * Manages rendering and filtering a logged-in patient's appointments.
 */

import { API_BASE_URL } from "./config/config.js";
import { getPatientData, filterAppointments } from "./services/patientServices.js";

document.addEventListener("DOMContentLoaded", async () => {
  const token = localStorage.getItem("token");
  const tableBody = document.getElementById("appointmentsTableBody");

  if (!token) {
    alert("Session expired. Please log in.");
    window.location.href = "/";
    return;
  }

  // Fetch patient details to get patient ID
  const patientData = await getPatientData(token);
  if (!patientData || !patientData.id) {
    tableBody.innerHTML = `<tr><td colspan="5" class="noPatientRecord">Could not load profile.</td></tr>`;
    return;
  }

  const patientId = patientData.id;

  async function loadAppointments() {
    tableBody.innerHTML = "";
    const doctorName = document.getElementById("doctorSearchInput")?.value?.trim() || "null";
    const condition  = document.getElementById("conditionFilter")?.value || "null";

    const appointments = await filterAppointments(condition, doctorName, token);

    if (!appointments || appointments.length === 0) {
      tableBody.innerHTML = `<tr><td colspan="5" class="noPatientRecord">No appointments found.</td></tr>`;
      return;
    }

    appointments.forEach((appt) => {
      const row = document.createElement("tr");

      const idTd = document.createElement("td");
      idTd.textContent = appt.id;

      const docTd = document.createElement("td");
      docTd.textContent = appt.doctorName || "Dr. " + (appt.doctor?.name || "N/A");

      const timeTd = document.createElement("td");
      timeTd.textContent = appt.appointmentTime ? appt.appointmentTime.replace("T", " ") : "N/A";

      const statusTd = document.createElement("td");
      statusTd.textContent = appt.status === 1 ? "Completed" : "Scheduled";
      statusTd.style.color = appt.status === 1 ? "green" : "#d97706";

      const actionTd = document.createElement("td");
      if (appt.status === 0) {
        const cancelBtn = document.createElement("button");
        cancelBtn.textContent = "Cancel";
        cancelBtn.classList.add("prescription-btn");
        cancelBtn.style.background = "#dc2626";

        cancelBtn.addEventListener("click", async () => {
          if (!confirm("Are you sure you want to cancel this appointment?")) return;
          try {
            const res = await fetch(`${API_BASE_URL}/appointments/${appt.id}/${token}`, {
              method: "DELETE"
            });
            if (res.ok) {
              alert("Appointment cancelled.");
              loadAppointments();
            } else {
              alert("Failed to cancel appointment.");
            }
          } catch (e) {
            console.error("Cancel error:", e);
          }
        });
        actionTd.appendChild(cancelBtn);
      } else {
        actionTd.textContent = "—";
      }

      row.appendChild(idTd);
      row.appendChild(docTd);
      row.appendChild(timeTd);
      row.appendChild(statusTd);
      row.appendChild(actionTd);

      tableBody.appendChild(row);
    });
  }

  document.getElementById("doctorSearchInput")?.addEventListener("input", loadAppointments);
  document.getElementById("conditionFilter")?.addEventListener("change", loadAppointments);

  loadAppointments();
});
