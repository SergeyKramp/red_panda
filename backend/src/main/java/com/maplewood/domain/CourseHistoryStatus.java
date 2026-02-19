package com.maplewood.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum CourseHistoryStatus {
    PASSED("passed"),
    FAILED("failed");

    private final String value;

    CourseHistoryStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CourseHistoryStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (CourseHistoryStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown course history status: " + value);
    }

    @Converter(autoApply = false)
    public static class CourseHistoryStatusConverter implements AttributeConverter<CourseHistoryStatus, String> {
        @Override
        public String convertToDatabaseColumn(CourseHistoryStatus attribute) {
            return attribute == null ? null : attribute.getValue();
        }

        @Override
        public CourseHistoryStatus convertToEntityAttribute(String dbData) {
            return CourseHistoryStatus.fromValue(dbData);
        }
    }
}
