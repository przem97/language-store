package com.przem7.englishcourseapp.configuration.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.przem7.englishcourseapp.configuration.jackson.deserializer.LocalDateTimeDeserializer;
import com.przem7.englishcourseapp.configuration.jackson.serializer.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfiguration {

    @Bean(name = "dtoDateFormat")
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ISO_DATE_TIME;
    }
}
