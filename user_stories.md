# User Story 1 – Admin Manages Doctors

**Title:**
*As an admin, I want to add and manage doctor accounts, so that only authorized doctors can access the system.*

**Acceptance Criteria:**

1. The admin can add a new doctor account.
2. The admin can edit or delete doctor details.
3. Changes are saved successfully in the database.

**Priority:** High
**Story Points:** 5

**Notes:**

* Only admins have permission to manage doctor accounts.

---

# User Story 2 – Patient Books an Appointment

**Title:**
*As a patient, I want to book an appointment with a doctor, so that I can receive medical consultation.*

**Acceptance Criteria:**

1. The patient can view available doctors and time slots.
2. The patient can select a preferred date and time.
3. The appointment is confirmed after successful booking.

**Priority:** High
**Story Points:** 5

**Notes:**

* A time slot cannot be double-booked.

---

# User Story 3 – Patient Manages Appointments

**Title:**
*As a patient, I want to reschedule or cancel my appointment, so that I can manage my schedule.*

**Acceptance Criteria:**

1. The patient can view upcoming appointments.
2. The patient can reschedule an appointment to an available slot.
3. The patient can cancel an appointment before its scheduled time.

**Priority:** Medium
**Story Points:** 3

**Notes:**

* Cancelled slots become available for other patients.

---

# User Story 4 – Doctor Manages Availability

**Title:**
*As a doctor, I want to set my available appointment slots, so that patients can book appointments during my working hours.*

**Acceptance Criteria:**

1. The doctor can add available time slots.
2. The doctor can edit or remove existing slots.
3. Only available slots are visible to patients.

**Priority:** High
**Story Points:** 5

**Notes:**

* Existing appointments cannot be overwritten.

---

# User Story 5 – Doctor Views Appointments

**Title:**
*As a doctor, I want to view my scheduled appointments, so that I can prepare for patient consultations.*

**Acceptance Criteria:**

1. The doctor can view all upcoming appointments.
2. Appointment details include patient name, date, and time.
3. The appointment list updates automatically after changes.

**Priority:** High
**Story Points:** 3

**Notes:**

* Doctors can only view their own appointments.
