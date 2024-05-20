package com.przem7.englishcourseapp.model.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParamErrorDetail {
    private String message;
    private String value;
}
