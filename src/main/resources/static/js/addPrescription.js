/**
 * addPrescription.js
 * Handles adding a prescription for a patient appointment.
 * POSTs to /api/prescription/{token}
 */

import { API_BASE_URL } from "./config/config.js";

document.addEventListener("DOMContentLoaded", () => {
  const appointmentId = localStorage.getItem("selectedAppointmentId");
  const patientName   = localStorage.getItem("selectedPatientName") || "Patient";
  const token         = localStorage.getItem("token");

  const banner = document.getElementById("patientInfoBanner");
  if (banner) {
    banner.textContent = `Patient: ${patientName} (Appointment #${appointmentId || 'N/A'})`;
  }

  // Cancel button → back to doctor dashboard
  const cancelBtn = document.getElementById("cancelBtn");
  if (cancelBtn) {
    cancelBtn.addEventListener("click", () => {
      window.location.href = `/doctorDashboard/${token}`;
    });
  }

  // Form submission
  const form = document.getElementById("prescriptionForm");
  if (form) {
    form.addEventListener("submit", async (e) => {
      e.preventDefault();

      const medication  = document.getElementById("medication")?.value?.trim();
      const dosage      = document.getElementById("dosage")?.value?.trim();
      const doctorNotes = document.getElementById("doctorNotes")?.value?.trim();

      if (!medication || !dosage) {
        alert("Please provide both medication and dosage.");
        return;
      }

      if (!token) {
        alert("Session expired. Please log in again.");
        window.location.href = "/";
        return;
      }

      const prescriptionData = {
        patientName: patientName,
        appointmentId: parseInt(appointmentId, 10),
        medication: medication,
        dosage: dosage,
        doctorNotes: doctorNotes || ""
      };

      try {
        const response = await fetch(`${API_BASE_URL}/prescription/${token}`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(prescriptionData)
        });

        if (response.ok) {
          alert("Prescription added successfully!");
          window.location.href = `/doctorDashboard/${token}`;
        } else {
          const err = await response.json();
          alert(err.message || "Failed to add prescription.");
        }
      } catch (error) {
        console.error("Prescription save error:", error);
        alert("An error occurred while saving the prescription.");
      }
    });
  }
});
