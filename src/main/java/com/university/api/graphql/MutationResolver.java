package com.university.api.graphql;

import com.university.api.dto.*;
import com.university.api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MutationResolver {

    private final StudentService studentService;
    private final CourseService courseService;
    private final FacultyService facultyService;
    private final CourseScheduleService scheduleService;

    @MutationMapping
    public StudentDto createStudent(@Argument Map<String, Object> input) {
        StudentDto dto = new StudentDto();
        dto.setFirstName((String) input.get("firstName"));
        dto.setLastName((String) input.get("lastName"));
        dto.setEmail((String) input.get("email"));
        dto.setStatus((String) input.getOrDefault("status", "ACTIVE"));
        dto.setMajor((String) input.get("major"));
        if (input.get("enrollmentYear") != null) {
            dto.setEnrollmentYear((Integer) input.get("enrollmentYear"));
        }
        return studentService.createStudent(dto);
    }

    @MutationMapping
    public CourseDto createCourse(@Argument Map<String, Object> input) {
        CourseDto dto = new CourseDto();
        dto.setCode((String) input.get("code"));
        dto.setTitle((String) input.get("title"));
        dto.setDescription((String) input.get("description"));
        dto.setCategory((String) input.get("category"));
        dto.setStatus((String) input.getOrDefault("status", "ACTIVE"));
        dto.setCredits((Integer) input.get("credits"));
        if (input.get("price") != null) dto.setPrice(new BigDecimal((String) input.get("price")));
        if (input.get("instructorId") != null) dto.setInstructorId(Long.valueOf((String) input.get("instructorId")));
        return courseService.createCourse(dto);
    }

    @MutationMapping
    public FacultyDto createFaculty(@Argument Map<String, Object> input) {
        FacultyDto dto = new FacultyDto();
        dto.setFirstName((String) input.get("firstName"));
        dto.setLastName((String) input.get("lastName"));
        dto.setEmail((String) input.get("email"));
        dto.setDepartment((String) input.get("department"));
        dto.setTitle((String) input.get("title"));
        dto.setStatus((String) input.getOrDefault("status", "ACTIVE"));
        return facultyService.createFaculty(dto);
    }

    @MutationMapping
    public CourseScheduleDto createSchedule(@Argument Map<String, Object> input) {
        CourseScheduleDto dto = new CourseScheduleDto();
        dto.setCourseId(Long.valueOf((String) input.get("courseId")));
        dto.setLocationId(Long.valueOf((String) input.get("locationId")));
        dto.setStartTime(LocalDateTime.parse((String) input.get("startTime")));
        dto.setEndTime(LocalDateTime.parse((String) input.get("endTime")));
        dto.setRecurrenceRule((String) input.get("recurrenceRule"));
        dto.setStatus((String) input.getOrDefault("status", "SCHEDULED"));
        return scheduleService.createSchedule(dto);
    }
}
