# Smart Clinic System - Schema Design

## MySQL Database Design

### Table: patients
- patient_id: INT, Primary Key, AUTO_INCREMENT
- first_name: VARCHAR(50), NOT NULL
- last_name: VARCHAR(50), NOT NULL
- email: VARCHAR(100), UNIQUE, NOT NULL
- phone: VARCHAR(15), UNIQUE, NOT NULL
- date_of_birth: DATE, NOT NULL
- gender: ENUM('Male','Female','Other'), NOT NULL
- password: VARCHAR(255), NOT NULL
- created_at: TIMESTAMP, DEFAULT CURRENT_TIMESTAMP

> **Note:** Email and phone are unique. Passwords should be stored as hashed values.

---

### Table: doctors
- doctor_id: INT, Primary Key, AUTO_INCREMENT
- first_name: VARCHAR(50), NOT NULL
- last_name: VARCHAR(50), NOT NULL
- specialization: VARCHAR(100), NOT NULL
- email: VARCHAR(100), UNIQUE, NOT NULL
- phone: VARCHAR(15), UNIQUE, NOT NULL
- consultation_fee: DECIMAL(8,2), NOT NULL
- availability: BOOLEAN, DEFAULT TRUE

> **Note:** Each doctor has a unique email and phone number.

---

### Table: appointments
- appointment_id: INT, Primary Key, AUTO_INCREMENT
- patient_id: INT, Foreign Key → patients(patient_id), NOT NULL
- doctor_id: INT, Foreign Key → doctors(doctor_id), NOT NULL
- appointment_datetime: DATETIME, NOT NULL
- status: ENUM('Scheduled','Completed','Cancelled'), DEFAULT 'Scheduled'
- reason: VARCHAR(255)

> **Note:** A patient or doctor should not have overlapping appointments. Patient records should remain even if appointments are cancelled.

---

### Table: admin
- admin_id: INT, Primary Key, AUTO_INCREMENT
- username: VARCHAR(50), UNIQUE, NOT NULL
- email: VARCHAR(100), UNIQUE, NOT NULL
- password: VARCHAR(255), NOT NULL
- role: VARCHAR(30), DEFAULT 'Administrator'

> **Note:** Only administrators can manage doctors and clinic settings.

---

### Table: doctor_availability
- availability_id: INT, Primary Key, AUTO_INCREMENT
- doctor_id: INT, Foreign Key → doctors(doctor_id), NOT NULL
- available_date: DATE, NOT NULL
- start_time: TIME, NOT NULL
- end_time: TIME, NOT NULL

> **Note:** Separating availability from appointments makes scheduling easier and prevents double bookings.

---

### Design Decisions
- Patients and doctors have separate tables because they have different roles.
- The **appointments** table links patients and doctors using foreign keys.
- Appointment history should be retained even after completion for future reference.
- If a patient or doctor is deleted, appointments should be archived rather than deleted to preserve medical records.
- Email and phone format validation should be handled in the application layer.

---

## MongoDB Collection Design

### Collection: prescriptions

```json
{
  "_id": "ObjectId('64abc123456')",
  "appointmentId": 105,
  "patientId": 12,
  "doctorId": 4,
  "prescriptionDate": "2026-07-12",
  "medicines": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Twice a day",
      "duration": "5 days"
    },
    {
      "name": "Vitamin C",
      "dosage": "1000mg",
      "frequency": "Once a day",
      "duration": "10 days"
    }
  ],
  "doctorNotes": {
    "diagnosis": "Viral Fever",
    "instructions": "Take medicines after meals and drink plenty of water."
  },
  "attachments": [
    "blood_report.pdf",
    "xray_image.jpg"
  ],
  "tags": [
    "fever",
    "follow-up"
  ],
  "createdAt": "2026-07-12T10:30:00Z"
}
```

### Design Decisions
- MongoDB is used because prescriptions vary in the number of medicines and attachments.
- Only **patientId**, **doctorId**, and **appointmentId** are stored instead of complete patient or doctor details to avoid duplicate data.
- Arrays allow multiple medicines and files to be stored in a single document.
- The schema is flexible, making it easy to add new fields such as allergies, lab reports, or follow-up notes without changing the database structure.