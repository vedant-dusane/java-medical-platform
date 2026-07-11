## Admin User Stories
# Admin User Story 1

**Title:**
*As an admin, I want to log into the portal with my username and password, so that I can securely manage the platform.*

**Acceptance Criteria:**

1. Admin can enter valid credentials.
2. System authenticates the admin.
3. Admin is redirected to the dashboard.

**Priority:** High
**Story Points:** 3

**Notes:**

* Invalid credentials should display an error message.

---

# Admin User Story 2

**Title:**
*As an admin, I want to log out of the portal, so that I can protect system access.*

**Acceptance Criteria:**

1. Admin can log out from any page.
2. User session is terminated.
3. Login is required to access the dashboard again.

**Priority:** High
**Story Points:** 2

**Notes:**

* Session should expire after logout.

---

# Admin User Story 3

**Title:**
*As an admin, I want to add doctors to the portal, so that they can provide appointments to patients.*

**Acceptance Criteria:**

1. Admin can enter doctor details.
2. Doctor account is created successfully.
3. Doctor appears in the doctors list.

**Priority:** High
**Story Points:** 5

**Notes:**

* Email should be unique.

---

# Admin User Story 4

**Title:**
*As an admin, I want to delete a doctor's profile from the portal, so that inactive doctors are removed from the system.*

**Acceptance Criteria:**

1. Admin can select a doctor.
2. System asks for confirmation.
3. Doctor profile is removed successfully.

**Priority:** Medium
**Story Points:** 3

**Notes:**

* Existing appointments should be handled appropriately.

---

# Admin User Story 5

**Title:**
*As an admin, I want to run a stored procedure in MySQL CLI to view the number of appointments per month, so that I can track system usage.*

**Acceptance Criteria:**

1. Stored procedure executes successfully.
2. Monthly appointment statistics are displayed.
3. Results are accurate.

**Priority:** Medium
**Story Points:** 3

**Notes:**

* Procedure should work on the latest database.

## Patient User Stories
# Patient User Story 1

**Title:**
*As a patient, I want to view a list of doctors without logging in, so that I can explore available doctors before registering.*

**Acceptance Criteria:**

1. Doctor list is publicly visible.
2. Doctor details include specialization.
3. No login is required.

**Priority:** Medium
**Story Points:** 2

**Notes:**

* Appointment booking requires login.

---

# Patient User Story 2

**Title:**
*As a patient, I want to sign up using my email and password, so that I can book appointments.*

**Acceptance Criteria:**

1. Patient can register with email and password.
2. Duplicate emails are not allowed.
3. Account is created successfully.

**Priority:** High
**Story Points:** 3

**Notes:**

* Password should meet security requirements.

---

# Patient User Story 3

**Title:**
*As a patient, I want to log into the portal, so that I can manage my bookings.*

**Acceptance Criteria:**

1. Valid credentials allow login.
2. Invalid credentials show an error.
3. Patient reaches the dashboard after login.

**Priority:** High
**Story Points:** 3

**Notes:**

* Session should remain active until logout.

---

# Patient User Story 4

**Title:**
*As a patient, I want to log out of the portal, so that I can secure my account.*

**Acceptance Criteria:**

1. Logout option is available.
2. User session ends successfully.
3. Login is required to access the account again.

**Priority:** High
**Story Points:** 2

**Notes:**

* Redirect user to the home page after logout.

---

# Patient User Story 5

**Title:**
*As a patient, I want to book an hour-long appointment with a doctor, so that I can receive medical consultation.*

**Acceptance Criteria:**

1. Patient selects an available doctor.
2. Patient selects an available one-hour slot.
3. Appointment is confirmed.

**Priority:** High
**Story Points:** 5

**Notes:**

* Double booking should not be allowed.

---

# Patient User Story 6

**Title:**
*As a patient, I want to view my upcoming appointments, so that I can prepare accordingly.*

**Acceptance Criteria:**

1. Upcoming appointments are listed.
2. Appointment details include doctor, date, and time.
3. Cancelled appointments are clearly marked.

**Priority:** Medium
**Story Points:** 2

**Notes:**

* List should be sorted by date.

## Doctor User Stories
# Doctor User Story 1

**Title:**
*As a doctor, I want to log into the portal, so that I can manage my appointments.*

**Acceptance Criteria:**

1. Doctor enters valid credentials.
2. Login is successful.
3. Dashboard is displayed.

**Priority:** High
**Story Points:** 3

**Notes:**

* Invalid login should show an error.

---

# Doctor User Story 2

**Title:**
*As a doctor, I want to log out of the portal, so that I can protect my data.*

**Acceptance Criteria:**

1. Logout option is available.
2. Session ends successfully.
3. Login is required to access the dashboard again.

**Priority:** High
**Story Points:** 2

**Notes:**

* Logout should invalidate the session.

---

# Doctor User Story 3

**Title:**
*As a doctor, I want to view my appointment calendar, so that I can stay organized.*

**Acceptance Criteria:**

1. Calendar displays all appointments.
2. Date and time are shown.
3. Calendar updates automatically.

**Priority:** High
**Story Points:** 3

**Notes:**

* Only the doctor's appointments should be displayed.

---

# Doctor User Story 4

**Title:**
*As a doctor, I want to mark my unavailability, so that patients can only book available time slots.*

**Acceptance Criteria:**

1. Doctor can block dates and times.
2. Blocked slots cannot be booked.
3. Availability updates immediately.

**Priority:** High
**Story Points:** 5

**Notes:**

* Existing appointments cannot be blocked.

---

# Doctor User Story 5

**Title:**
*As a doctor, I want to update my profile with my specialization and contact information, so that patients have up-to-date information.*

**Acceptance Criteria:**

1. Doctor can edit profile details.
2. Changes are saved successfully.
3. Updated profile is visible to patients.

**Priority:** Medium
**Story Points:** 3

**Notes:**

* Contact details should be validated.

---

# Doctor User Story 6

**Title:**
*As a doctor, I want to view patient details for upcoming appointments, so that I can be prepared for consultations.*

**Acceptance Criteria:**

1. Doctor can view upcoming appointments.
2. Patient information is displayed.
3. Only authorized doctors can access patient details.

**Priority:** High
**Story Points:** 3

**Notes:**

* Patient information must remain confidential.
