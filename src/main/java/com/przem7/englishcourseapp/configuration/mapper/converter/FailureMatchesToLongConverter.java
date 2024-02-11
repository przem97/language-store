package com.przem7.englishcourseapp.configuration.mapper.converter;

import com.przem7.englishcourseapp.model.orm.FailureMatch;
import org.modelmapper.AbstractConverter;

import java.util.List;

public class FailureMatchesToLongConverter extends AbstractConverter<List<FailureMatch>, Long> {
    @Override
    protected Long convert(List<FailureMatch> source) {
        if (source == null) {
            return 0L;
        }

        return (long) source.size();
    }
}
