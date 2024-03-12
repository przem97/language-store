package com.przem7.englishcourseapp.controller.util;

import com.jayway.jsonpath.JsonPath;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeUtil {

    public static ResultMatcher isOfDesiredFormat(String path, DateTimeFormatter format) throws Exception {
        return mvcResult -> {
            String contentAsString = mvcResult.getResponse().getContentAsString();
            String date = "";
            try {
                date = JsonPath.read(contentAsString, path);
                LocalDateTime.parse(date, format);
            } catch (DateTimeParseException e) {
                throw new AssertionError("Date " + date + " is not in desired format of " + format);
            }
        };
    }

    public static ResultMatcher isEqual(String path, LocalDateTime expected, DateTimeFormatter format) throws Exception {
        return mvcResult -> {
            String contentAsString = mvcResult.getResponse().getContentAsString();
            String date = JsonPath.read(contentAsString, path);
            LocalDateTime parsed = LocalDateTime.parse(date, format);
            if (!parsed.isEqual(expected)) {
                throw new AssertionError("Date " + date + " is not in equal to expected " + expected);
            }
        };
    }
}
