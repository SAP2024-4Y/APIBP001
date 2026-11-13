package com.university.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseScheduleDto {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private Long locationId;
    private String locationName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String recurrenceRule;
    private String status;
}
