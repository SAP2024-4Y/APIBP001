package com.university.api.controller;

import com.university.api.dto.CourseDto;
import com.university.api.dto.PageResponse;
import com.university.api.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Courses")
@SecurityRequirement(name = "bearerAuth")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @Operation(summary = "List courses with cursor pagination")
    public ResponseEntity<PageResponse<CourseDto>> listCourses(
            @RequestParam(required = false) String after,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(courseService.getCourses(after, Math.min(limit, 100)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a course by ID")
    public ResponseEntity<CourseDto> getCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourse(id));
    }

    @PostMapping
    @Operation(summary = "Create a new course")
    public ResponseEntity<CourseDto> createCourse(@RequestBody CourseDto dto) {
        return ResponseEntity.status(201).body(courseService.createCourse(dto));
    }
}
