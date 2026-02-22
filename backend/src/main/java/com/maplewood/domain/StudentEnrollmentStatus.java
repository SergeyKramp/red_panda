package com.maplewood.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum StudentEnrollmentStatus {
    ENROLLED("enrolled"),
    COMPLETED("completed"),
    DROPPED("dropped");

    private final String value;

    StudentEnrollmentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StudentEnrollmentStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (var status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown student enrollment status: " + value);
    }

    @Converter(autoApply = false)
    public static class StudentEnrollmentStatusConverter
            implements AttributeConverter<StudentEnrollmentStatus, String> {
        @Override
        public String convertToDatabaseColumn(StudentEnrollmentStatus attribute) {
            return attribute == null ? null : attribute.getValue();
        }

        @Override
        public StudentEnrollmentStatus convertToEntityAttribute(String dbData) {
            return StudentEnrollmentStatus.fromValue(dbData);
        }
    }
}
