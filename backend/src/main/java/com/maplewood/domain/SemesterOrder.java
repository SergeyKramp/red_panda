package com.maplewood.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum SemesterOrder {
    FALL(1), SPRING(2);

    private final int code;

    SemesterOrder(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static SemesterOrder fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (SemesterOrder value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown semester order code: " + code);
    }

    @Converter(autoApply = false)
    public static class SemesterOrderConverter
            implements AttributeConverter<SemesterOrder, Integer> {
        @Override
        public Integer convertToDatabaseColumn(SemesterOrder attribute) {
            return attribute == null ? null : attribute.getCode();
        }

        @Override
        public SemesterOrder convertToEntityAttribute(Integer dbData) {
            return SemesterOrder.fromCode(dbData);
        }
    }

    public static SemesterOrder getCurrentSemesterOrder() {
        var currentMonth = java.time.LocalDate.now().getMonthValue();
        return (currentMonth >= 1 && currentMonth <= 5) ? SPRING : FALL;
    }
}
