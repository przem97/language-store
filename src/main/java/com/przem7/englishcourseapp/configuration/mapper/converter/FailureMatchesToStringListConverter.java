package com.przem7.englishcourseapp.configuration.mapper.converter;

import com.przem7.englishcourseapp.model.orm.FailureMatch;
import org.modelmapper.AbstractConverter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FailureMatchesToStringListConverter extends AbstractConverter<List<FailureMatch>, List<String>> {
    @Override
    protected List<String> convert(List<FailureMatch> source) {
        if (source == null) {
            return Collections.emptyList();
        }

        return source.stream().map(FailureMatch::getErrorValue).collect(Collectors.toList());
    }
}