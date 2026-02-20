CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login_at DATETIME,
    student_id INTEGER,
    FOREIGN KEY (student_id) REFERENCES students(id)
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE UNIQUE INDEX IF NOT EXISTS uq_users_student_id ON users(student_id);

-- Seed a student for assignment login testing
INSERT OR IGNORE INTO students (
    first_name,
    last_name,
    email,
    grade_level,
    enrollment_year,
    expected_graduation_year,
    status,
    created_at
) VALUES (
    'Sergey',
    'Kramp',
    'sergey.kramp9@student.maplewood.edu',
    9,
    2024,
    2028,
    'active',
    CURRENT_TIMESTAMP
);

-- Login credentials:
-- username: root
-- password: rootPassword
INSERT OR IGNORE INTO users (
    username,
    password_hash,
    role,
    enabled,
    created_at,
    last_login_at,
    student_id
)
SELECT
    'root',
    '$2a$10$E0oROm36mg8UPuO9pCW.O.bDgVGCHvHqkfoA5FX6aSkowhaI8uCuS',
    'USER',
    1,
    CURRENT_TIMESTAMP,
    NULL,
    s.id
FROM students s
WHERE s.email = 'sergey.kramp9@student.maplewood.edu';

-- Ensure the user stays associated even if the row existed before this seed.
UPDATE users
SET student_id = (
    SELECT id
    FROM students
    WHERE email = 'sergey.kramp9@student.maplewood.edu'
)
WHERE username = 'root';
