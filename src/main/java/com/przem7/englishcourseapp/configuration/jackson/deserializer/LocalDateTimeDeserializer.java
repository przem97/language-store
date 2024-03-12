package com.przem7.englishcourseapp.configuration.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private final DateTimeFormatter format;

    public LocalDateTimeDeserializer(@Autowired @Qualifier("dtoDateFormat") DateTimeFormatter format) {
        this.format = format;
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext context) throws IOException {
        return LocalDateTime.parse(p.getValueAsString(), format);
    }

}