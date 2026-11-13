package com.university.api.graphql;

import com.university.api.dto.*;
import com.university.api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class QueryResolver {

    private final StudentService studentService;
    private final CourseService courseService;
    private final FacultyService facultyService;
    private final CampusLocationService locationService;
    private final CourseScheduleService scheduleService;

    @QueryMapping
    public PageResponse<StudentDto> students(@Argument String after, @Argument Integer limit) {
        return studentService.getStudents(after, limit != null ? limit : 20);
    }

    @QueryMapping
    public StudentDto student(@Argument Long id) {
        return studentService.getStudent(id);
    }

    @QueryMapping
    public PageResponse<CourseDto> courses(@Argument String after, @Argument Integer limit) {
        return courseService.getCourses(after, limit != null ? limit : 20);
    }

    @QueryMapping
    public CourseDto course(@Argument Long id) {
        return courseService.getCourse(id);
    }

    @QueryMapping
    public List<FacultyDto> faculty() {
        return facultyService.getAllFaculty();
    }

    @QueryMapping
    public FacultyDto facultyMember(@Argument Long id) {
        return facultyService.getFaculty(id);
    }

    @QueryMapping
    public List<CampusLocationDto> locations() {
        return locationService.getAllLocations();
    }

    @QueryMapping
    public CampusLocationDto location(@Argument Long id) {
        return locationService.getLocation(id);
    }

    @QueryMapping
    public List<CourseScheduleDto> schedules(@Argument String start, @Argument String end) {
        return scheduleService.getSchedules(start, end);
    }

    @QueryMapping
    public CourseScheduleDto schedule(@Argument Long id) {
        return scheduleService.getSchedule(id);
    }
}
