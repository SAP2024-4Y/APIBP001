package com.university.api.graphql;

import com.university.api.dto.CourseDto;
import com.university.api.dto.FacultyDto;
import com.university.api.repository.CourseRepository;
import com.university.api.model.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class FacultyCoursesResolver {

    private final CourseRepository courseRepository;

    @BatchMapping(typeName = "Faculty", field = "courses")
    public Map<FacultyDto, List<CourseDto>> courses(List<FacultyDto> facultyList) {
        List<Long> ids = facultyList.stream().map(FacultyDto::getId).collect(Collectors.toList());
        List<Course> allCourses = courseRepository.findAll().stream()
                .filter(c -> c.getInstructor() != null && ids.contains(c.getInstructor().getId()))
                .collect(Collectors.toList());

        Map<Long, List<CourseDto>> byFacultyId = allCourses.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getInstructor().getId(),
                        Collectors.mapping(c -> CourseDto.builder()
                                .id(c.getId()).code(c.getCode()).title(c.getTitle())
                                .category(c.getCategory()).status(c.getStatus())
                                .credits(c.getCredits()).price(c.getPrice())
                                .build(), Collectors.toList())));

        return facultyList.stream().collect(Collectors.toMap(
                f -> f,
                f -> byFacultyId.getOrDefault(f.getId(), List.of())));
    }
}
