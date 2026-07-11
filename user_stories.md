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
