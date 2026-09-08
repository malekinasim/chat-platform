package com.nasim.chat.auth_service.model.convertor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AudienceListConverter implements jakarta.persistence.AttributeConverter<List<String>, String>{
    @Override
    public String convertToDatabaseColumn(List<String> audiences) {
        if (audiences == null || audiences.isEmpty()) {
            return null;
        }
        return audiences.stream()
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .collect(Collectors.joining(","));
    }

    @Override
    public List<String> convertToEntityAttribute(String databaseValue) {
       if(databaseValue == null || databaseValue.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(databaseValue.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
