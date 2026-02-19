package com.maplewood.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum StudentStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    GRADUATED("graduated");

    private final String value;

    StudentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StudentStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (StudentStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown student status: " + value);
    }

    @Converter(autoApply = false)
    public static class StudentStatusConverter implements AttributeConverter<StudentStatus, String> {
        @Override
        public String convertToDatabaseColumn(StudentStatus attribute) {
            return attribute == null ? null : attribute.getValue();
        }

        @Override
        public StudentStatus convertToEntityAttribute(String dbData) {
            return StudentStatus.fromValue(dbData);
        }
    }
}
