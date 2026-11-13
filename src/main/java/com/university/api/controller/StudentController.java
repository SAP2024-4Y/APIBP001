package com.university.api.controller;

import com.university.api.dto.PageResponse;
import com.university.api.dto.StudentDto;
import com.university.api.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Students")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @Operation(summary = "List students with cursor pagination")
    public ResponseEntity<PageResponse<StudentDto>> listStudents(
            @RequestParam(required = false) String after,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(studentService.getStudents(after, Math.min(limit, 100)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a student by ID")
    public ResponseEntity<StudentDto> getStudent(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudent(id));
    }

    @PostMapping
    @Operation(summary = "Create a new student")
    public ResponseEntity<StudentDto> createStudent(@RequestBody StudentDto dto) {
        return ResponseEntity.status(201).body(studentService.createStudent(dto));
    }
}
