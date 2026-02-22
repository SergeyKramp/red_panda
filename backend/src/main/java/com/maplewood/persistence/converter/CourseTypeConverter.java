package com.maplewood.persistence.converter;

import com.maplewood.domain.CourseType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CourseTypeConverter implements AttributeConverter<CourseType, String> {
    @Override
    public String convertToDatabaseColumn(CourseType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name().toLowerCase();
    }

    @Override
    public CourseType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        return CourseType.fromValue(dbData);
    }
}
