package com.project.backend.config;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs once at startup to make sure the database is ready to go —
 * creates the stored procedures and seeds sample data if the tables are empty.
 * This way you can clone the project, point it at a fresh database, and it just works.
 */
@Component
public class DatabaseDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseDataInitializer.class);

    private final JdbcTemplate jdbcTemplate;
    private final MongoTemplate mongoTemplate;

    public DatabaseDataInitializer(JdbcTemplate jdbcTemplate, MongoTemplate mongoTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Running startup database checks...");
        initMySqlProcedures();
        initMySqlData();
        initMongoData();
        log.info("Database checks done.");
    }

    private void initMySqlProcedures() {
        try {
            log.info("Setting up stored procedures...");

            // Procedure 1: GetDailyAppointmentReportByDoctor
            jdbcTemplate.execute("DROP PROCEDURE IF EXISTS GetDailyAppointmentReportByDoctor");
            jdbcTemplate.execute(
                "CREATE PROCEDURE GetDailyAppointmentReportByDoctor(\n" +
                "    IN report_date DATE\n" +
                ")\n" +
                "BEGIN\n" +
                "    SELECT \n" +
                "        d.name AS doctor_name,\n" +
                "        a.appointment_time,\n" +
                "        a.status,\n" +
                "        p.name AS patient_name,\n" +
                "        p.phone AS patient_phone\n" +
                "    FROM \n" +
                "        appointment a\n" +
                "    JOIN \n" +
                "        doctor d ON a.doctor_id = d.id\n" +
                "    JOIN \n" +
                "        patient p ON a.patient_id = p.id\n" +
                "    WHERE \n" +
                "        DATE(a.appointment_time) = report_date\n" +
                "    ORDER BY \n" +
                "        d.name, a.appointment_time;\n" +
                "END"
            );

            // Procedure 2: GetDoctorWithMostPatientsByMonth
            jdbcTemplate.execute("DROP PROCEDURE IF EXISTS GetDoctorWithMostPatientsByMonth");
            jdbcTemplate.execute(
                "CREATE PROCEDURE GetDoctorWithMostPatientsByMonth(\n" +
                "    IN input_month INT, \n" +
                "    IN input_year INT\n" +
                ")\n" +
                "BEGIN\n" +
                "    SELECT\n" +
                "        doctor_id, \n" +
                "        COUNT(patient_id) AS patients_seen\n" +
                "    FROM\n" +
                "        appointment\n" +
                "    WHERE\n" +
                "        MONTH(appointment_time) = input_month \n" +
                "        AND YEAR(appointment_time) = input_year\n" +
                "    GROUP BY\n" +
                "        doctor_id\n" +
                "    ORDER BY\n" +
                "        patients_seen DESC\n" +
                "    LIMIT 1;\n" +
                "END"
            );

            // Procedure 3: GetDoctorWithMostPatientsByYear
            jdbcTemplate.execute("DROP PROCEDURE IF EXISTS GetDoctorWithMostPatientsByYear");
            jdbcTemplate.execute(
                "CREATE PROCEDURE GetDoctorWithMostPatientsByYear(\n" +
                "    IN input_year INT\n" +
                ")\n" +
                "BEGIN\n" +
                "    SELECT\n" +
                "        doctor_id, \n" +
                "        COUNT(patient_id) AS patients_seen\n" +
                "    FROM\n" +
                "        appointment\n" +
                "    WHERE\n" +
                "        YEAR(appointment_time) = input_year\n" +
                "    GROUP BY\n" +
                "        doctor_id\n" +
                "    ORDER BY\n" +
                "        patients_seen DESC\n" +
                "    LIMIT 1;\n" +
                "END"
            );

            log.info("MySQL stored procedures initialized successfully.");
        } catch (Exception e) {
            log.warn("Could not initialize MySQL stored procedures: {}", e.getMessage());
        }
    }

    private void initMySqlData() {
        try {
            // 1. Admin
            Integer adminCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM admin", Integer.class);
            if (adminCount == null || adminCount == 0) {
                log.info("Seeding admin table...");
                jdbcTemplate.execute("INSERT INTO admin (username, password) VALUES ('admin', 'admin@1234')");
            }

            // 2. Doctor
            Integer doctorCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM doctor", Integer.class);
            if (doctorCount == null || doctorCount == 0) {
                log.info("Seeding doctor table...");
                jdbcTemplate.execute(
                    "INSERT INTO doctor (id, email, name, password, phone, specialty) VALUES\n" +
                    "(1, 'dr.adams@example.com', 'Dr. Emily Adams', 'pass12345', '555-101-2020', 'Cardiologist'),\n" +
                    "(2, 'dr.johnson@example.com', 'Dr. Mark Johnson', 'secure4567', '555-202-3030', 'Neurologist'),\n" +
                    "(3, 'dr.lee@example.com', 'Dr. Sarah Lee', 'leePass987', '555-303-4040', 'Orthopedist'),\n" +
                    "(4, 'dr.wilson@example.com', 'Dr. Tom Wilson', 'w!ls0nPwd', '555-404-5050', 'Pediatrician'),\n" +
                    "(5, 'dr.brown@example.com', 'Dr. Alice Brown', 'brownie123', '555-505-6060', 'Dermatologist'),\n" +
                    "(6, 'dr.taylor@example.com', 'Dr. Taylor Grant', 'taylor321', '555-606-7070', 'Cardiologist'),\n" +
                    "(7, 'dr.white@example.com', 'Dr. Sam White', 'whiteSecure1', '555-707-8080', 'Neurologist'),\n" +
                    "(8, 'dr.clark@example.com', 'Dr. Emma Clark', 'clarkPass456', '555-808-9090', 'Orthopedist'),\n" +
                    "(9, 'dr.davis@example.com', 'Dr. Olivia Davis', 'davis789', '555-909-0101', 'Pediatrician'),\n" +
                    "(10, 'dr.miller@example.com', 'Dr. Henry Miller', 'millertime!', '555-010-1111', 'Dermatologist'),\n" +
                    "(11, 'dr.moore@example.com', 'Dr. Ella Moore', 'ellapass33', '555-111-2222', 'Cardiologist'),\n" +
                    "(12, 'dr.martin@example.com', 'Dr. Leo Martin', 'martinpass', '555-222-3333', 'Neurologist'),\n" +
                    "(13, 'dr.jackson@example.com', 'Dr. Ivy Jackson', 'jackson11', '555-333-4444', 'Orthopedist'),\n" +
                    "(14, 'dr.thomas@example.com', 'Dr. Owen Thomas', 'thomasPWD', '555-444-5555', 'Pediatrician'),\n" +
                    "(15, 'dr.hall@example.com', 'Dr. Ava Hall', 'hallhall', '555-555-6666', 'Dermatologist'),\n" +
                    "(16, 'dr.green@example.com', 'Dr. Mia Green', 'greenleaf', '555-666-7777', 'Cardiologist'),\n" +
                    "(17, 'dr.baker@example.com', 'Dr. Jack Baker', 'bakeitup', '555-777-8888', 'Neurologist'),\n" +
                    "(18, 'dr.walker@example.com', 'Dr. Nora Walker', 'walkpass12', '555-888-9999', 'Orthopedist'),\n" +
                    "(19, 'dr.young@example.com', 'Dr. Liam Young', 'young123', '555-999-0000', 'Pediatrician'),\n" +
                    "(20, 'dr.king@example.com', 'Dr. Zoe King', 'kingkong1', '555-000-1111', 'Dermatologist'),\n" +
                    "(21, 'dr.scott@example.com', 'Dr. Lily Scott', 'scottish', '555-111-2223', 'Cardiologist'),\n" +
                    "(22, 'dr.evans@example.com', 'Dr. Lucas Evans', 'evansEv1', '555-222-3334', 'Neurologist'),\n" +
                    "(23, 'dr.turner@example.com', 'Dr. Grace Turner', 'turnerBurner', '555-333-4445', 'Orthopedist'),\n" +
                    "(24, 'dr.hill@example.com', 'Dr. Ethan Hill', 'hillclimb', '555-444-5556', 'Pediatrician'),\n" +
                    "(25, 'dr.ward@example.com', 'Dr. Ruby Ward', 'wardWard', '555-555-6667', 'Dermatologist')"
                );
            }

            // 3. Doctor Available Times
            Integer timesCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM doctor_available_times", Integer.class);
            if (timesCount == null || timesCount == 0) {
                log.info("Seeding doctor_available_times table...");
                jdbcTemplate.execute(
                    "INSERT INTO doctor_available_times (doctor_id, available_times) VALUES\n" +
                    "(1, '09:00-10:00'), (1, '10:00-11:00'), (1, '11:00-12:00'), (1, '14:00-15:00'),\n" +
                    "(2, '10:00-11:00'), (2, '11:00-12:00'), (2, '14:00-15:00'), (2, '15:00-16:00'),\n" +
                    "(3, '09:00-10:00'), (3, '11:00-12:00'), (3, '14:00-15:00'), (3, '16:00-17:00'),\n" +
                    "(4, '09:00-10:00'), (4, '10:00-11:00'), (4, '15:00-16:00'), (4, '16:00-17:00'),\n" +
                    "(5, '09:00-10:00'), (5, '10:00-11:00'), (5, '14:00-15:00'), (5, '15:00-16:00'),\n" +
                    "(6, '09:00-10:00'), (6, '10:00-11:00'), (6, '11:00-12:00'), (6, '14:00-15:00'),\n" +
                    "(7, '09:00-10:00'), (7, '10:00-11:00'), (7, '15:00-16:00'), (7, '16:00-17:00'),\n" +
                    "(8, '10:00-11:00'), (8, '11:00-12:00'), (8, '14:00-15:00'), (8, '15:00-16:00'),\n" +
                    "(9, '09:00-10:00'), (9, '11:00-12:00'), (9, '13:00-14:00'), (9, '14:00-15:00'),\n" +
                    "(10, '10:00-11:00'), (10, '11:00-12:00'), (10, '14:00-15:00'), (10, '16:00-17:00'),\n" +
                    "(11, '09:00-10:00'), (11, '12:00-13:00'), (11, '14:00-15:00'), (11, '15:00-16:00'),\n" +
                    "(12, '10:00-11:00'), (12, '11:00-12:00'), (12, '13:00-14:00'), (12, '14:00-15:00'),\n" +
                    "(13, '13:00-14:00'), (13, '14:00-15:00'), (13, '15:00-16:00'), (13, '16:00-17:00'),\n" +
                    "(14, '09:00-10:00'), (14, '10:00-11:00'), (14, '14:00-15:00'), (14, '16:00-17:00'),\n" +
                    "(15, '10:00-11:00'), (15, '11:00-12:00'), (15, '13:00-14:00'), (15, '14:00-15:00'),\n" +
                    "(16, '09:00-10:00'), (16, '11:00-12:00'), (16, '14:00-15:00'), (16, '16:00-17:00'),\n" +
                    "(17, '09:00-10:00'), (17, '10:00-11:00'), (17, '11:00-12:00'), (17, '12:00-13:00'),\n" +
                    "(18, '09:00-10:00'), (18, '10:00-11:00'), (18, '11:00-12:00'), (18, '15:00-16:00'),\n" +
                    "(19, '13:00-14:00'), (19, '14:00-15:00'), (19, '15:00-16:00'), (19, '16:00-17:00'),\n" +
                    "(20, '10:00-11:00'), (20, '13:00-14:00'), (20, '14:00-15:00'), (20, '15:00-16:00'),\n" +
                    "(21, '09:00-10:00'), (21, '10:00-11:00'), (21, '14:00-15:00'), (21, '15:00-16:00'),\n" +
                    "(22, '10:00-11:00'), (22, '11:00-12:00'), (22, '14:00-15:00'), (22, '16:00-17:00'),\n" +
                    "(23, '11:00-12:00'), (23, '13:00-14:00'), (23, '15:00-16:00'), (23, '16:00-17:00'),\n" +
                    "(24, '12:00-13:00'), (24, '13:00-14:00'), (24, '14:00-15:00'), (24, '15:00-16:00'),\n" +
                    "(25, '09:00-10:00'), (25, '10:00-11:00'), (25, '14:00-15:00'), (25, '15:00-16:00')"
                );
            }

            // 4. Patient
            Integer patientCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM patient", Integer.class);
            if (patientCount == null || patientCount == 0) {
                log.info("Seeding patient table...");
                jdbcTemplate.execute(
                    "INSERT INTO patient (id, address, email, name, password, phone) VALUES\n" +
                    "(1, '101 Oak St, Cityville', 'jane.doe@example.com', 'Jane Doe', 'passJane1', '888-111-1111'),\n" +
                    "(2, '202 Maple Rd, Townsville', 'john.smith@example.com', 'John Smith', 'smithSecure', '888-222-2222'),\n" +
                    "(3, '303 Pine Ave, Villageton', 'emily.rose@example.com', 'Emily Rose', 'emilyPass99', '888-333-3333'),\n" +
                    "(4, '404 Birch Ln, Metropolis', 'michael.j@example.com', 'Michael Jordan', 'airmj23', '888-444-4444'),\n" +
                    "(5, '505 Cedar Blvd, Springfield', 'olivia.m@example.com', 'Olivia Moon', 'moonshine12', '888-555-5555'),\n" +
                    "(6, '606 Spruce Ct, Gotham', 'liam.k@example.com', 'Liam King', 'king321', '888-666-6666'),\n" +
                    "(7, '707 Aspen Dr, Riverdale', 'sophia.l@example.com', 'Sophia Lane', 'sophieLane', '888-777-7777'),\n" +
                    "(8, '808 Elm St, Newtown', 'noah.b@example.com', 'Noah Brooks', 'noahBest!', '888-888-8888'),\n" +
                    "(9, '909 Willow Way, Star City', 'ava.d@example.com', 'Ava Daniels', 'avaSecure8', '888-999-9999'),\n" +
                    "(10, '111 Chestnut Pl, Midvale', 'william.h@example.com', 'William Harris', 'willH2025', '888-000-0000'),\n" +
                    "(11, '112 Redwood St, Fairview', 'mia.g@example.com', 'Mia Green', 'miagreen1', '889-111-1111'),\n" +
                    "(12, '113 Cypress Rd, Edgewater', 'james.b@example.com', 'James Brown', 'jamiebrown', '889-222-2222'),\n" +
                    "(13, '114 Poplar Ave, Crestwood', 'amelia.c@example.com', 'Amelia Clark', 'ameliacool', '889-333-3333'),\n" +
                    "(14, '115 Sequoia Dr, Elmwood', 'ben.j@example.com', 'Ben Johnson', 'bennyJ', '889-444-4444'),\n" +
                    "(15, '116 Palm Blvd, Harborview', 'ella.m@example.com', 'Ella Monroe', 'ellam123', '889-555-5555'),\n" +
                    "(16, '117 Cottonwood Ct, Laketown', 'lucas.t@example.com', 'Lucas Turner', 'lucasTurn', '889-666-6666'),\n" +
                    "(17, '118 Sycamore Ln, Hilltop', 'grace.s@example.com', 'Grace Scott', 'graceful', '889-777-7777'),\n" +
                    "(18, '119 Magnolia Pl, Brookside', 'ethan.h@example.com', 'Ethan Hill', 'hill2025', '889-888-8888'),\n" +
                    "(19, '120 Fir St, Woodland', 'ruby.w@example.com', 'Ruby Ward', 'rubypass', '889-999-9999'),\n" +
                    "(20, '121 Beech Way, Lakeside', 'jack.b@example.com', 'Jack Baker', 'bakerjack', '889-000-0000'),\n" +
                    "(21, '122 Alder Ave, Pinehill', 'mia.h@example.com', 'Mia Hall', 'hallMia', '890-111-1111'),\n" +
                    "(22, '123 Hawthorn Blvd, Meadowbrook', 'owen.t@example.com', 'Owen Thomas', 'owen123', '890-222-2222'),\n" +
                    "(23, '124 Dogwood Dr, Summit', 'ivy.j@example.com', 'Ivy Jackson', 'ivyIvy', '890-333-3333'),\n" +
                    "(24, '125 Juniper Ct, Greenwood', 'leo.m@example.com', 'Leo Martin', 'leopass', '890-444-4444'),\n" +
                    "(25, '126 Olive Rd, Ashville', 'ella.moore@example.com', 'Ella Moore', 'ellamoore', '890-555-5555')"
                );
            }

            // 5. Appointment
            Integer apptCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM appointment", Integer.class);
            if (apptCount == null || apptCount == 0) {
                log.info("Seeding appointment table...");
                jdbcTemplate.execute(
                    "INSERT INTO appointment (appointment_time, status, doctor_id, patient_id) VALUES\n" +
                    "('2025-05-01 09:00:00.000000', 0, 1, 1),\n" +
                    "('2025-05-02 10:00:00.000000', 0, 1, 2),\n" +
                    "('2025-05-03 11:00:00.000000', 0, 1, 3),\n" +
                    "('2025-05-04 14:00:00.000000', 0, 1, 4),\n" +
                    "('2025-05-05 15:00:00.000000', 0, 1, 5),\n" +
                    "('2025-05-06 13:00:00.000000', 0, 1, 6),\n" +
                    "('2025-05-07 09:00:00.000000', 0, 1, 7),\n" +
                    "('2025-05-08 16:00:00.000000', 0, 1, 8),\n" +
                    "('2025-05-09 11:00:00.000000', 0, 1, 9),\n" +
                    "('2025-05-10 10:00:00.000000', 0, 1, 10),\n" +
                    "('2025-05-11 12:00:00.000000', 0, 1, 11),\n" +
                    "('2025-05-12 15:00:00.000000', 0, 1, 12),\n" +
                    "('2025-05-13 13:00:00.000000', 0, 1, 13),\n" +
                    "('2025-05-14 10:00:00.000000', 0, 1, 14),\n" +
                    "('2025-05-15 11:00:00.000000', 0, 1, 15),\n" +
                    "('2025-05-16 14:00:00.000000', 0, 1, 16),\n" +
                    "('2025-05-17 09:00:00.000000', 0, 1, 17),\n" +
                    "('2025-05-18 12:00:00.000000', 0, 1, 18),\n" +
                    "('2025-05-19 13:00:00.000000', 0, 1, 19),\n" +
                    "('2025-05-20 16:00:00.000000', 0, 1, 20),\n" +
                    "('2025-05-21 14:00:00.000000', 0, 1, 21),\n" +
                    "('2025-05-22 10:00:00.000000', 0, 1, 22),\n" +
                    "('2025-05-23 11:00:00.000000', 0, 1, 23),\n" +
                    "('2025-05-24 15:00:00.000000', 0, 1, 24),\n" +
                    "('2025-05-25 09:00:00.000000', 0, 1, 25),\n" +
                    "('2025-05-01 10:00:00.000000', 0, 2, 1),\n" +
                    "('2025-05-02 11:00:00.000000', 0, 3, 2),\n" +
                    "('2025-05-03 14:00:00.000000', 0, 4, 3),\n" +
                    "('2025-05-04 15:00:00.000000', 0, 5, 4),\n" +
                    "('2025-05-05 10:00:00.000000', 0, 6, 5),\n" +
                    "('2025-05-06 11:00:00.000000', 0, 7, 6),\n" +
                    "('2025-05-07 14:00:00.000000', 0, 8, 7),\n" +
                    "('2025-05-08 15:00:00.000000', 0, 9, 8),\n" +
                    "('2025-05-09 10:00:00.000000', 0, 10, 9),\n" +
                    "('2025-05-10 14:00:00.000000', 0, 11, 10),\n" +
                    "('2025-05-11 13:00:00.000000', 0, 12, 11),\n" +
                    "('2025-05-12 14:00:00.000000', 0, 13, 12),\n" +
                    "('2025-05-13 15:00:00.000000', 0, 14, 13),\n" +
                    "('2025-05-14 10:00:00.000000', 0, 15, 14),\n" +
                    "('2025-05-15 11:00:00.000000', 0, 16, 15),\n" +
                    "('2025-05-16 14:00:00.000000', 0, 17, 16),\n" +
                    "('2025-05-17 10:00:00.000000', 0, 18, 17),\n" +
                    "('2025-05-18 13:00:00.000000', 0, 19, 18),\n" +
                    "('2025-05-19 14:00:00.000000', 0, 20, 19),\n" +
                    "('2025-05-20 11:00:00.000000', 0, 21, 20),\n" +
                    "('2025-05-21 13:00:00.000000', 0, 22, 21),\n" +
                    "('2025-05-22 14:00:00.000000', 0, 23, 22),\n" +
                    "('2025-05-23 10:00:00.000000', 0, 24, 23),\n" +
                    "('2025-05-24 15:00:00.000000', 0, 25, 24),\n" +
                    "('2025-05-25 13:00:00.000000', 0, 25, 25),\n" +
                    "('2025-04-01 10:00:00.000000', 1, 1, 2),\n" +
                    "('2025-04-02 11:00:00.000000', 1, 2, 3),\n" +
                    "('2025-04-03 14:00:00.000000', 1, 3, 4),\n" +
                    "('2025-04-04 15:00:00.000000', 1, 4, 5),\n" +
                    "('2025-04-05 10:00:00.000000', 1, 5, 6),\n" +
                    "('2025-04-06 11:00:00.000000', 1, 6, 7),\n" +
                    "('2025-04-07 14:00:00.000000', 1, 7, 8),\n" +
                    "('2025-04-08 15:00:00.000000', 1, 8, 9),\n" +
                    "('2025-04-09 10:00:00.000000', 1, 9, 10),\n" +
                    "('2025-04-10 14:00:00.000000', 1, 10, 11),\n" +
                    "('2025-04-11 13:00:00.000000', 1, 11, 12),\n" +
                    "('2025-04-12 14:00:00.000000', 1, 12, 13),\n" +
                    "('2025-04-13 15:00:00.000000', 1, 13, 14),\n" +
                    "('2025-04-14 10:00:00.000000', 1, 14, 15),\n" +
                    "('2025-04-15 11:00:00.000000', 1, 15, 16),\n" +
                    "('2025-04-16 14:00:00.000000', 1, 16, 17),\n" +
                    "('2025-04-17 10:00:00.000000', 1, 17, 18),\n" +
                    "('2025-04-18 13:00:00.000000', 1, 18, 19),\n" +
                    "('2025-04-19 14:00:00.000000', 1, 19, 20),\n" +
                    "('2025-04-20 11:00:00.000000', 1, 20, 21),\n" +
                    "('2025-04-21 13:00:00.000000', 1, 21, 22),\n" +
                    "('2025-04-22 14:00:00.000000', 1, 22, 23),\n" +
                    "('2025-04-23 10:00:00.000000', 1, 23, 24),\n" +
                    "('2025-04-24 15:00:00.000000', 1, 24, 25),\n" +
                    "('2025-04-25 13:00:00.000000', 1, 25, 25),\n" +
                    "('2025-04-01 09:00:00.000000', 1, 1, 1),\n" +
                    "('2025-04-02 10:00:00.000000', 1, 1, 2),\n" +
                    "('2025-04-03 11:00:00.000000', 1, 1, 3),\n" +
                    "('2025-04-04 14:00:00.000000', 1, 1, 4),\n" +
                    "('2025-04-05 10:00:00.000000', 1, 1, 5),\n" +
                    "('2025-04-10 10:00:00.000000', 1, 1, 6),\n" +
                    "('2025-04-11 09:00:00.000000', 1, 1, 7),\n" +
                    "('2025-04-14 13:00:00.000000', 1, 1, 8),\n" +
                    "('2025-04-01 10:00:00.000000', 1, 2, 1),\n" +
                    "('2025-04-01 11:00:00.000000', 1, 2, 2),\n" +
                    "('2025-04-02 09:00:00.000000', 1, 2, 3),\n" +
                    "('2025-04-02 10:00:00.000000', 1, 2, 4),\n" +
                    "('2025-04-03 11:00:00.000000', 1, 2, 5),\n" +
                    "('2025-04-03 12:00:00.000000', 1, 2, 6),\n" +
                    "('2025-04-04 14:00:00.000000', 1, 2, 7),\n" +
                    "('2025-04-04 15:00:00.000000', 1, 2, 8),\n" +
                    "('2025-04-05 10:00:00.000000', 1, 2, 9),\n" +
                    "('2025-04-05 11:00:00.000000', 1, 2, 10),\n" +
                    "('2025-04-06 13:00:00.000000', 1, 2, 11),\n" +
                    "('2025-04-06 14:00:00.000000', 1, 2, 12),\n" +
                    "('2025-04-07 09:00:00.000000', 1, 2, 13),\n" +
                    "('2025-04-07 10:00:00.000000', 1, 2, 14),\n" +
                    "('2025-04-08 11:00:00.000000', 1, 2, 15),\n" +
                    "('2025-04-08 12:00:00.000000', 1, 2, 16),\n" +
                    "('2025-04-09 13:00:00.000000', 1, 2, 17),\n" +
                    "('2025-04-09 14:00:00.000000', 1, 2, 18),\n" +
                    "('2025-04-10 11:00:00.000000', 1, 2, 19),\n" +
                    "('2025-04-10 12:00:00.000000', 1, 2, 20),\n" +
                    "('2025-04-11 14:00:00.000000', 1, 2, 21),\n" +
                    "('2025-04-11 15:00:00.000000', 1, 2, 22),\n" +
                    "('2025-04-12 10:00:00.000000', 1, 2, 23),\n" +
                    "('2025-04-12 11:00:00.000000', 1, 2, 24),\n" +
                    "('2025-04-13 13:00:00.000000', 1, 2, 25),\n" +
                    "('2025-04-13 14:00:00.000000', 1, 2, 1),\n" +
                    "('2025-04-14 09:00:00.000000', 1, 2, 2),\n" +
                    "('2025-04-14 10:00:00.000000', 1, 2, 3),\n" +
                    "('2025-04-15 12:00:00.000000', 1, 2, 4),\n" +
                    "('2025-04-15 13:00:00.000000', 1, 2, 5),\n" +
                    "('2025-04-01 12:00:00.000000', 1, 3, 1),\n" +
                    "('2025-04-02 11:00:00.000000', 1, 3, 2),\n" +
                    "('2025-04-03 13:00:00.000000', 1, 3, 3),\n" +
                    "('2025-04-04 15:00:00.000000', 1, 3, 4),\n" +
                    "('2025-04-05 12:00:00.000000', 1, 3, 5),\n" +
                    "('2025-04-08 13:00:00.000000', 1, 3, 6),\n" +
                    "('2025-04-09 10:00:00.000000', 1, 3, 7),\n" +
                    "('2025-04-10 14:00:00.000000', 1, 3, 8),\n" +
                    "('2025-04-11 13:00:00.000000', 1, 3, 9),\n" +
                    "('2025-04-12 09:00:00.000000', 1, 3, 10),\n" +
                    "('2025-04-01 14:00:00.000000', 1, 4, 1),\n" +
                    "('2025-04-02 12:00:00.000000', 1, 4, 2),\n" +
                    "('2025-04-03 14:00:00.000000', 1, 4, 3),\n" +
                    "('2025-04-04 16:00:00.000000', 1, 4, 4),\n" +
                    "('2025-04-05 14:00:00.000000', 1, 4, 5),\n" +
                    "('2025-04-09 11:00:00.000000', 1, 4, 6),\n" +
                    "('2025-04-10 13:00:00.000000', 1, 4, 7)"
                );
            }

            log.info("MySQL initial data check and seeding completed.");
        } catch (Exception e) {
            log.warn("Could not complete MySQL data initialization: {}", e.getMessage());
        }
    }

    private void initMongoData() {
        try {
            if (!mongoTemplate.collectionExists("prescriptions") || mongoTemplate.getCollection("prescriptions").countDocuments() == 0) {
                log.info("Seeding MongoDB prescriptions collection...");
                List<Document> docs = new ArrayList<>();
                docs.add(createPrescriptionDoc("6807dd712725f013281e7201", "John Smith", 51L, "Paracetamol", "500mg", "Take 1 tablet every 6 hours."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7202", "Emily Rose", 52L, "Aspirin", "300mg", "Take 1 tablet after meals."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7203", "Michael Jordan", 53L, "Ibuprofen", "400mg", "Take 1 tablet every 8 hours."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7204", "Olivia Moon", 54L, "Antihistamine", "10mg", "Take 1 tablet daily before bed."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7205", "Liam King", 55L, "Vitamin C", "1000mg", "Take 1 tablet daily."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7206", "Sophia Lane", 56L, "Antibiotics", "500mg", "Take 1 tablet every 12 hours."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7207", "Noah Brooks", 57L, "Paracetamol", "500mg", "Take 1 tablet every 6 hours."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7208", "Ava Daniels", 58L, "Ibuprofen", "200mg", "Take 1 tablet every 8 hours."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7209", "William Harris", 59L, "Aspirin", "300mg", "Take 1 tablet after meals."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7210", "Mia Green", 60L, "Vitamin D", "1000 IU", "Take 1 tablet daily with food."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7211", "James Brown", 61L, "Antihistamine", "10mg", "Take 1 tablet every morning."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7212", "Amelia Clark", 62L, "Paracetamol", "500mg", "Take 1 tablet every 6 hours."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7213", "Ben Johnson", 63L, "Ibuprofen", "400mg", "Take 1 tablet every 8 hours."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7214", "Ella Monroe", 64L, "Vitamin C", "1000mg", "Take 1 tablet daily."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7215", "Lucas Turner", 65L, "Aspirin", "300mg", "Take 1 tablet after meals."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7216", "Grace Scott", 66L, "Paracetamol", "500mg", "Take 1 tablet every 6 hours."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7217", "Ethan Hill", 67L, "Ibuprofen", "400mg", "Take 1 tablet every 8 hours."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7218", "Ruby Ward", 68L, "Vitamin D", "1000 IU", "Take 1 tablet daily with food."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7219", "Jack Baker", 69L, "Antibiotics", "500mg", "Take 1 tablet every 12 hours."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7220", "Mia Hall", 70L, "Paracetamol", "500mg", "Take 1 tablet every 6 hours."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7221", "Owen Thomas", 71L, "Ibuprofen", "200mg", "Take 1 tablet every 8 hours."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7222", "Ivy Jackson", 72L, "Antihistamine", "10mg", "Take 1 tablet every morning."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7223", "Leo Martin", 73L, "Vitamin C", "1000mg", "Take 1 tablet daily."));
                docs.add(createPrescriptionDoc("6807dd712725f013281e7224", "Ella Moore", 74L, "Aspirin", "300mg", "Take 1 tablet after meals."));

                mongoTemplate.getCollection("prescriptions").insertMany(docs);
                log.info("MongoDB prescriptions collection seeded successfully with {} documents.", docs.size());
            }
        } catch (Exception e) {
            log.warn("Could not complete MongoDB data initialization: {}", e.getMessage());
        }
    }

    private Document createPrescriptionDoc(String id, String patientName, Long appointmentId,
                                          String medication, String dosage, String doctorNotes) {
        Document doc = new Document();
        doc.put("_id", new ObjectId(id));
        doc.put("patientName", patientName);
        doc.put("appointmentId", appointmentId);
        doc.put("medication", medication);
        doc.put("dosage", dosage);
        doc.put("doctorNotes", doctorNotes);
        doc.put("_class", "com.project.backend.models.Prescription");
        return doc;
    }
}
