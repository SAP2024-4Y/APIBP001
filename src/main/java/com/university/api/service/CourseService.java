package com.university.api.service;

import com.university.api.dto.CourseDto;
import com.university.api.dto.PageResponse;
import com.university.api.model.Course;
import com.university.api.repository.CourseRepository;
import com.university.api.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;

    public PageResponse<CourseDto> getCourses(String after, int limit) {
        List<Course> courses;
        if (after == null || after.isBlank()) {
            courses = courseRepository.findFirstPage(PageRequest.of(0, limit + 1));
        } else {
            long afterId = decodeCursor(after);
            courses = courseRepository.findPageAfter(afterId, PageRequest.of(0, limit + 1));
        }

        boolean hasMore = courses.size() > limit;
        if (hasMore) courses = courses.subList(0, limit);

        String nextCursor = hasMore ? encodeCursor(courses.get(courses.size() - 1).getId()) : null;

        return PageResponse.<CourseDto>builder()
                .data(courses.stream().map(this::toDto).collect(Collectors.toList()))
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .count(courses.size())
                .build();
    }

    public CourseDto getCourse(Long id) {
        return courseRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new com.university.api.exception.ResourceNotFoundException("Course", id));
    }

    public CourseDto createCourse(CourseDto dto) {
        Course course = new Course();
        course.setCode(dto.getCode());
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setCategory(dto.getCategory());
        course.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        course.setCredits(dto.getCredits());
        course.setPrice(dto.getPrice());
        if (dto.getInstructorId() != null) {
            facultyRepository.findById(dto.getInstructorId()).ifPresent(course::setInstructor);
        }
        return toDto(courseRepository.save(course));
    }

    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private CourseDto toDto(Course c) {
        return CourseDto.builder()
                .id(c.getId())
                .code(c.getCode())
                .title(c.getTitle())
                .description(c.getDescription())
                .category(c.getCategory())
                .status(c.getStatus())
                .credits(c.getCredits())
                .price(c.getPrice())
                .instructorId(c.getInstructor() != null ? c.getInstructor().getId() : null)
                .instructorName(c.getInstructor() != null
                        ? c.getInstructor().getFirstName() + " " + c.getInstructor().getLastName() : null)
                .build();
    }

    private String encodeCursor(Long id) {
        return Base64.getUrlEncoder().encodeToString(("id:" + id).getBytes());
    }

    private long decodeCursor(String cursor) {
        String decoded = new String(Base64.getUrlDecoder().decode(cursor));
        return Long.parseLong(decoded.split(":")[1]);
    }
}
