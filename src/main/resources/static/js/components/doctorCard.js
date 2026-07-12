/**
 * doctorCard.js
 * Exports a reusable createDoctorCard(doctor) function that builds a doctor
 * card DOM element with role-based action buttons (Delete / Book Now).
 *
 * Dependencies (resolved at runtime from service files):
 *   deleteDoctor()      – /js/services/doctorServices.js
 *   getPatientData()    – /js/services/patientServices.js
 *   showBookingOverlay()– /js/services/patientServices.js  (or modals.js)
 */

import { deleteDoctor } from "/js/services/doctorServices.js";
import { getPatientData, showBookingOverlay } from "/js/services/patientServices.js";

/**
 * Creates and returns a doctor card HTMLElement.
 * @param {Object} doctor - Doctor data object from the API.
 * @returns {HTMLElement}
 */
export function createDoctorCard(doctor) {
  // ── 1. Main card container ─────────────────────────────────────
  const card = document.createElement("div");
  card.classList.add("doctor-card");
  card.setAttribute("data-id", doctor.id);

  // ── 2. Fetch current user role ─────────────────────────────────
  const role = localStorage.getItem("userRole");

  // ── 3. Doctor info section ─────────────────────────────────────
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  // Header strip (gradient background)
  const cardHeader = document.createElement("div");
  cardHeader.classList.add("card-header");

  const name = document.createElement("h3");
  name.textContent = doctor.name;

  const specialization = document.createElement("p");
  specialization.textContent = doctor.specialty || doctor.specialization || "General";

  cardHeader.appendChild(name);
  cardHeader.appendChild(specialization);

  // Body details
  const cardBody = document.createElement("div");
  cardBody.classList.add("card-body");

  const email = document.createElement("p");
  email.innerHTML = `<span>📧</span> ${doctor.email}`;

  const phone = document.createElement("p");
  phone.innerHTML = `<span>📞</span> ${doctor.phone || "N/A"}`;

  const availability = document.createElement("p");
  const times = Array.isArray(doctor.availableTimes)
    ? doctor.availableTimes.join(", ")
    : (doctor.availableTimes || "N/A");
  availability.innerHTML = `<span>🕐</span> ${times}`;

  cardBody.appendChild(email);
  cardBody.appendChild(phone);
  cardBody.appendChild(availability);

  infoDiv.appendChild(cardHeader);
  infoDiv.appendChild(cardBody);

  // ── 4. Action buttons container ───────────────────────────────
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  // ── 5. Role-based buttons ─────────────────────────────────────

  if (role === "admin") {
    // Admin: Delete button
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.classList.add("prescription-btn");

    removeBtn.addEventListener("click", async () => {
      const confirmed = confirm(
        `Are you sure you want to delete Dr. ${doctor.name}?`
      );
      if (!confirmed) return;

      const token = localStorage.getItem("token");
      try {
        await deleteDoctor(doctor.id, token);
        // Remove card from the DOM on success
        card.remove();
      } catch (err) {
        alert("Failed to delete doctor. Please try again.");
        console.error(err);
      }
    });

    actionsDiv.appendChild(removeBtn);

  } else if (role === "patient") {
    // Non-logged-in patient: prompt to login
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("prescription-btn");

    bookNow.addEventListener("click", () => {
      alert("Please log in to book an appointment.");
    });

    actionsDiv.appendChild(bookNow);

  } else if (role === "loggedPatient") {
    // Logged-in patient: real booking flow
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("prescription-btn");

    bookNow.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      try {
        const patientData = await getPatientData(token);
        showBookingOverlay(e, doctor, patientData);
      } catch (err) {
        alert("Could not load patient data. Please try again.");
        console.error(err);
      }
    });

    actionsDiv.appendChild(bookNow);
  }

  // ── 6. Assemble and return the card ──────────────────────────
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}
