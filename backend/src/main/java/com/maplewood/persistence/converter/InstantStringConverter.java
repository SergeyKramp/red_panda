package com.maplewood.persistence.converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class InstantStringConverter implements AttributeConverter<Instant, String> {
    private static final DateTimeFormatter DATETIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String convertToDatabaseColumn(Instant attribute) {
        if (attribute == null) {
            return null;
        }
        return DATETIME_FORMAT.format(attribute.atOffset(ZoneOffset.UTC));
    }

    @Override
    public Instant convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }

        String value = dbData.trim();
        if (value.length() == 10) {
            LocalDate localDate = LocalDate.parse(value, DATE_FORMAT);
            return localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        }

        if (value.contains("T")) {
            return Instant.parse(value);
        }

        LocalDateTime localDateTime = LocalDateTime.parse(value, DATETIME_FORMAT);
        return localDateTime.toInstant(ZoneOffset.UTC);
    }
}
