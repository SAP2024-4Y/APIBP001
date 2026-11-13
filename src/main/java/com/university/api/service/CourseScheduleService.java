package com.university.api.service;

import com.university.api.dto.CourseScheduleDto;
import com.university.api.exception.ConflictException;
import com.university.api.model.CampusLocation;
import com.university.api.model.Course;
import com.university.api.model.CourseSchedule;
import com.university.api.repository.CampusLocationRepository;
import com.university.api.repository.CourseRepository;
import com.university.api.repository.CourseScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseScheduleService {

    private final CourseScheduleRepository scheduleRepository;
    private final CourseRepository courseRepository;
    private final CampusLocationRepository locationRepository;

    public List<CourseScheduleDto> getSchedules(String start, String end) {
        if (start != null && end != null) {
            LocalDateTime startDt = LocalDateTime.parse(start);
            LocalDateTime endDt = LocalDateTime.parse(end);
            return scheduleRepository.findByWindow(startDt, endDt)
                    .stream().map(this::toDto).collect(Collectors.toList());
        }
        return scheduleRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public CourseScheduleDto getSchedule(Long id) {
        return scheduleRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new com.university.api.exception.ResourceNotFoundException("CourseSchedule", id));
    }

    public CourseScheduleDto createSchedule(CourseScheduleDto dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new com.university.api.exception.ResourceNotFoundException("Course", dto.getCourseId()));
        CampusLocation location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new com.university.api.exception.ResourceNotFoundException("CampusLocation", dto.getLocationId()));

        checkConflicts(course, location, dto.getStartTime(), dto.getEndTime(), -1L);

        CourseSchedule schedule = new CourseSchedule();
        schedule.setCourse(course);
        schedule.setLocation(location);
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setRecurrenceRule(dto.getRecurrenceRule());
        schedule.setStatus(dto.getStatus() != null ? dto.getStatus() : "SCHEDULED");
        return toDto(scheduleRepository.save(schedule));
    }

    public CourseScheduleDto updateSchedule(Long id, CourseScheduleDto dto) {
        CourseSchedule existing = scheduleRepository.findById(id)
                .orElseThrow(() -> new com.university.api.exception.ResourceNotFoundException("CourseSchedule", id));

        Course course = dto.getCourseId() != null
                ? courseRepository.findById(dto.getCourseId()).orElse(existing.getCourse())
                : existing.getCourse();
        CampusLocation location = dto.getLocationId() != null
                ? locationRepository.findById(dto.getLocationId()).orElse(existing.getLocation())
                : existing.getLocation();

        LocalDateTime start = dto.getStartTime() != null ? dto.getStartTime() : existing.getStartTime();
        LocalDateTime end = dto.getEndTime() != null ? dto.getEndTime() : existing.getEndTime();

        checkConflicts(course, location, start, end, id);

        existing.setCourse(course);
        existing.setLocation(location);
        existing.setStartTime(start);
        existing.setEndTime(end);
        if (dto.getRecurrenceRule() != null) existing.setRecurrenceRule(dto.getRecurrenceRule());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
        return toDto(scheduleRepository.save(existing));
    }

    private void checkConflicts(Course course, CampusLocation location,
                                LocalDateTime start, LocalDateTime end, Long excludeId) {
        List<CourseSchedule> locationConflicts = scheduleRepository
                .findConflictsByLocation(location.getId(), start, end);
        if (excludeId > 0) locationConflicts = locationConflicts.stream()
                .filter(s -> !s.getId().equals(excludeId)).collect(Collectors.toList());
        if (!locationConflicts.isEmpty()) {
            throw new ConflictException("Location is already booked during the requested time window");
        }

        if (course.getInstructor() != null) {
            List<CourseSchedule> facultyConflicts = scheduleRepository
                    .findConflictsByFaculty(course.getInstructor().getId(), start, end);
            if (excludeId > 0) facultyConflicts = facultyConflicts.stream()
                    .filter(s -> !s.getId().equals(excludeId)).collect(Collectors.toList());
            if (!facultyConflicts.isEmpty()) {
                throw new ConflictException("Instructor already has a scheduled class during the requested time window");
            }
        }
    }

    private CourseScheduleDto toDto(CourseSchedule s) {
        return CourseScheduleDto.builder()
                .id(s.getId())
                .courseId(s.getCourse() != null ? s.getCourse().getId() : null)
                .courseTitle(s.getCourse() != null ? s.getCourse().getTitle() : null)
                .locationId(s.getLocation() != null ? s.getLocation().getId() : null)
                .locationName(s.getLocation() != null ? s.getLocation().getName() : null)
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .recurrenceRule(s.getRecurrenceRule())
                .status(s.getStatus())
                .build();
    }
}
