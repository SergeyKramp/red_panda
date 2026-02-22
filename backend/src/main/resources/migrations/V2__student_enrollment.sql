CREATE TABLE IF NOT EXISTS student_enrollments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id INTEGER NOT NULL,
    course_id INTEGER NOT NULL,
    semester_id INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('enrolled', 'completed', 'dropped')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (semester_id) REFERENCES semesters(id),
    UNIQUE(student_id, course_id, semester_id)
);

CREATE INDEX IF NOT EXISTS idx_student_enrollments_student
    ON student_enrollments(student_id);
CREATE INDEX IF NOT EXISTS idx_student_enrollments_course
    ON student_enrollments(course_id);
CREATE INDEX IF NOT EXISTS idx_student_enrollments_semester
    ON student_enrollments(semester_id);
