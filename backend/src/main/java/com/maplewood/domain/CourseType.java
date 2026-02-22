package com.maplewood.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CourseType {
    CORE,
    ELECTIVE;

    @JsonCreator
    public static CourseType fromValue(String value) {
        if (value == null) {
            return null;
        }
        return CourseType.valueOf(value.trim().toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
