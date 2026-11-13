package com.university.api.controller;

import com.university.api.dto.CourseScheduleDto;
import com.university.api.service.CourseScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
@Tag(name = "Course Schedules")
@SecurityRequirement(name = "bearerAuth")
public class CourseScheduleController {

    private final CourseScheduleService scheduleService;

    @GetMapping
    @Operation(summary = "List schedules; optionally filter by ISO 8601 window")
    public ResponseEntity<List<CourseScheduleDto>> listSchedules(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        return ResponseEntity.ok(scheduleService.getSchedules(start, end));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a schedule by ID")
    public ResponseEntity<CourseScheduleDto> getSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getSchedule(id));
    }

    @PostMapping
    @Operation(summary = "Create a schedule; returns 409 on conflict")
    public ResponseEntity<CourseScheduleDto> createSchedule(@RequestBody CourseScheduleDto dto) {
        return ResponseEntity.status(201).body(scheduleService.createSchedule(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a schedule; returns 409 on conflict")
    public ResponseEntity<CourseScheduleDto> updateSchedule(@PathVariable Long id,
                                                             @RequestBody CourseScheduleDto dto) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, dto));
    }
}
