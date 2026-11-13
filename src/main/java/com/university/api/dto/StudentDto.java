package com.university.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private String major;
    private Integer enrollmentYear;
}
