package com.university.api.service;

import com.university.api.dto.PageResponse;
import com.university.api.dto.StudentDto;
import com.university.api.model.Student;
import com.university.api.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public PageResponse<StudentDto> getStudents(String after, int limit) {
        List<Student> students;
        if (after == null || after.isBlank()) {
            students = studentRepository.findFirstPage(PageRequest.of(0, limit + 1));
        } else {
            long afterId = decodeCursor(after);
            students = studentRepository.findPageAfter(afterId, PageRequest.of(0, limit + 1));
        }

        boolean hasMore = students.size() > limit;
        if (hasMore) students = students.subList(0, limit);

        String nextCursor = hasMore ? encodeCursor(students.get(students.size() - 1).getId()) : null;

        return PageResponse.<StudentDto>builder()
                .data(students.stream().map(this::toDto).collect(Collectors.toList()))
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .count(students.size())
                .build();
    }

    public StudentDto getStudent(Long id) {
        return studentRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new com.university.api.exception.ResourceNotFoundException("Student", id));
    }

    public StudentDto createStudent(StudentDto dto) {
        Student student = new Student();
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        student.setMajor(dto.getMajor());
        student.setEnrollmentYear(dto.getEnrollmentYear());
        return toDto(studentRepository.save(student));
    }

    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private StudentDto toDto(Student s) {
        return StudentDto.builder()
                .id(s.getId())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .email(s.getEmail())
                .status(s.getStatus())
                .major(s.getMajor())
                .enrollmentYear(s.getEnrollmentYear())
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
