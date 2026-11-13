package com.university.api.controller;

import com.university.api.dto.FacultyDto;
import com.university.api.service.FacultyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/faculty")
@RequiredArgsConstructor
@Tag(name = "Faculty")
@SecurityRequirement(name = "bearerAuth")
public class FacultyController {

    private final FacultyService facultyService;

    @GetMapping
    @Operation(summary = "List all faculty members")
    public ResponseEntity<List<FacultyDto>> listFaculty() {
        return ResponseEntity.ok(facultyService.getAllFaculty());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a faculty member by ID")
    public ResponseEntity<FacultyDto> getFaculty(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.getFaculty(id));
    }

    @PostMapping
    @Operation(summary = "Create a new faculty member")
    public ResponseEntity<FacultyDto> createFaculty(@RequestBody FacultyDto dto) {
        return ResponseEntity.status(201).body(facultyService.createFaculty(dto));
    }
}
