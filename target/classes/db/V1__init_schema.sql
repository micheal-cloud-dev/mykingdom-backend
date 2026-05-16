-- ============================================================
--  MY KINGDOM SCHOOL MANAGEMENT - MySQL Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS mykingdom_db;
USE mykingdom_db;

-- 1. USERS TABLE
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(100) NOT NULL UNIQUE,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('ADMIN','TEACHER','STUDENT') NOT NULL DEFAULT 'STUDENT',
    is_active   BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. STUDENTS TABLE
CREATE TABLE IF NOT EXISTS students (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id      VARCHAR(20) NOT NULL UNIQUE,
    user_id         BIGINT,
    full_name       VARCHAR(150) NOT NULL,
    class           VARCHAR(20) NOT NULL,
    section         VARCHAR(5) NOT NULL,
    dob             DATE,
    gender          ENUM('Male','Female','Other'),
    blood_group     VARCHAR(5),
    phone           VARCHAR(15),
    email           VARCHAR(150),
    address         TEXT,
    guardian_name   VARCHAR(150),
    guardian_phone  VARCHAR(15),
    admission_year  YEAR,
    avatar_url      VARCHAR(255),
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 3. FEE STRUCTURE TABLE
CREATE TABLE IF NOT EXISTS fee_structure (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    fee_name      VARCHAR(100) NOT NULL,
    amount        DECIMAL(10,2) NOT NULL,
    category      ENUM('Academic','Transport','Extra','Exam') NOT NULL DEFAULT 'Academic',
    frequency     ENUM('Monthly','Quarterly','Yearly','OneTime') NOT NULL DEFAULT 'Yearly',
    class_name    VARCHAR(20),
    academic_year VARCHAR(10) NOT NULL,
    due_date      DATE,
    description   TEXT,
    is_active     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. FEE PAYMENTS TABLE
CREATE TABLE IF NOT EXISTS fee_payments (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id       BIGINT NOT NULL,
    fee_structure_id BIGINT NOT NULL,
    amount_paid      DECIMAL(10,2) NOT NULL,
    payment_method   ENUM('Cash','UPI','CreditCard','DebitCard','Cheque','Online') NOT NULL,
    payment_status   ENUM('Paid','Pending','Overdue','Partial') NOT NULL DEFAULT 'Pending',
    transaction_id   VARCHAR(100),
    receipt_no       VARCHAR(50) UNIQUE,
    paid_on          DATE,
    remarks          TEXT,
    recorded_by      BIGINT,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (fee_structure_id) REFERENCES fee_structure(id) ON DELETE RESTRICT,
    FOREIGN KEY (recorded_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 5. RESULTS TABLE
CREATE TABLE IF NOT EXISTS results (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id     BIGINT NOT NULL,
    subject        VARCHAR(100) NOT NULL,
    max_marks      INT NOT NULL DEFAULT 100,
    marks_obtained DECIMAL(5,2) NOT NULL,
    grade          VARCHAR(5),
    exam_type      ENUM('Term1','Term2','Annual','Unit') NOT NULL,
    academic_year  VARCHAR(10) NOT NULL,
    teacher_name   VARCHAR(150),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- 6. ATTENDANCE TABLE
CREATE TABLE IF NOT EXISTS attendance (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    date       DATE NOT NULL,
    status     ENUM('Present','Absent','Late','Holiday') NOT NULL DEFAULT 'Present',
    remarks    VARCHAR(255),
    marked_by  BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_student_date (student_id, date),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (marked_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 7. NOTICES TABLE
CREATE TABLE IF NOT EXISTS notices (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    content      TEXT NOT NULL,
    type         ENUM('Important','Reminder','Info','Event') NOT NULL DEFAULT 'Info',
    target_class VARCHAR(20),
    notice_date  DATE NOT NULL,
    is_active    BOOLEAN DEFAULT TRUE,
    created_by   BIGINT,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- ─────────────────────────────────────────────
--  SAMPLE DATA
-- ─────────────────────────────────────────────

-- Admin password: Admin@Kingdom123 (BCrypt hashed)
INSERT INTO users (username, email, password, role) VALUES
('admin', 'admin@mykingdom.com',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBP4J8X.5.5/lW', 'ADMIN'),
('teacher1', 'teacher1@mykingdom.com',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBP4J8X.5.5/lW', 'TEACHER');

-- Sample Student
INSERT INTO students (student_id, full_name, class, section, dob, gender, phone, email,
                      address, guardian_name, guardian_phone, admission_year)
VALUES ('ST1023', 'Micheal Edison', '12', 'A', '2007-08-15', 'Male',
        '+91 98765 43210', 'micheal.edison@email.com',
        'Chennai, Tamil Nadu', 'Mr. Edison Sr.', '+91 90826 97041', 2022);

-- Fee Structure
INSERT INTO fee_structure (fee_name, amount, category, frequency, class_name, academic_year, due_date) VALUES
('1st Installment', 5000.00, 'Academic', 'Yearly', '12', '2024-25', '2024-06-30'),
('2nd Installment', 5000.00, 'Academic', 'Yearly', '12', '2024-25', '2024-09-30'),
('3rd Installment', 5000.00, 'Academic', 'Yearly', '12', '2024-25', '2024-12-31'),
('4th Installment', 5000.00, 'Academic', 'Yearly', '12', '2024-25', '2025-03-31'),
('5th Installment', 5000.00, 'Academic', 'Yearly', '12', '2024-25', '2025-05-31');

-- Fee Payments for Micheal Edison
INSERT INTO fee_payments (student_id, fee_structure_id, amount_paid, payment_method,
                          payment_status, receipt_no, paid_on) VALUES
(1, 1, 5000.00, 'Online',      'Paid',    'REC-2024-001', '2024-06-10'),
(1, 2, 5000.00, 'CreditCard',  'Paid',    'REC-2024-002', '2024-09-10'),
(1, 3, 5000.00, 'UPI',         'Paid',    'REC-2024-003', '2024-12-10'),
(1, 4, 5000.00, 'UPI',         'Pending', NULL,            NULL),
(1, 5, 5000.00, 'Cash',        'Pending', NULL,            NULL);

-- Results
INSERT INTO results (student_id, subject, max_marks, marks_obtained, grade, exam_type, academic_year, teacher_name) VALUES
(1, 'English',          100, 88, 'A+', 'Term2', '2024-25', 'Mrs. Priya'),
(1, 'Mathematics',      100, 92, 'A+', 'Term2', '2024-25', 'Mr. Ramesh'),
(1, 'Physics',          100, 85, 'A',  'Term2', '2024-25', 'Mr. Vivek'),
(1, 'Chemistry',        100, 80, 'A',  'Term2', '2024-25', 'Mrs. Lakshmi'),
(1, 'Biology',          100, 78, 'B+', 'Term2', '2024-25', 'Mr. Arun'),
(1, 'Computer Science', 100, 89, 'A+', 'Term2', '2024-25', 'Mr. Deepak');

-- Indexes
CREATE INDEX idx_student_class   ON students(class, section);
CREATE INDEX idx_fee_pay_student ON fee_payments(student_id, payment_status);
CREATE INDEX idx_result_student  ON results(student_id, academic_year);
CREATE INDEX idx_attend_student  ON attendance(student_id, date);
CREATE INDEX idx_notice_date     ON notices(notice_date, is_active);
