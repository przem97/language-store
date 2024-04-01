package com.przem7.englishcourseapp.configuration.jackson;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfiguration {

    private static final String PATTERN = "yyyy-MM-dd hh:mm:ss.SSSSSS";

    @Bean(name = "dtoDateFormat")
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern(PATTERN);
    }
}
