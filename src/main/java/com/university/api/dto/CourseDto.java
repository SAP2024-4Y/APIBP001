package com.university.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    private Long id;
    private String code;
    private String title;
    private String description;
    private String category;
    private String status;
    private Integer credits;
    private BigDecimal price;
    private Long instructorId;
    private String instructorName;
}
