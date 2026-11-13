package com.university.api.service;

import com.university.api.dto.FacultyDto;
import com.university.api.model.Faculty;
import com.university.api.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public List<FacultyDto> getAllFaculty() {
        return facultyRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public FacultyDto getFaculty(Long id) {
        return facultyRepository.findById(id).map(this::toDto)
                .orElseThrow(() -> new com.university.api.exception.ResourceNotFoundException("Faculty", id));
    }

    public FacultyDto createFaculty(FacultyDto dto) {
        Faculty faculty = new Faculty();
        faculty.setFirstName(dto.getFirstName());
        faculty.setLastName(dto.getLastName());
        faculty.setEmail(dto.getEmail());
        faculty.setDepartment(dto.getDepartment());
        faculty.setTitle(dto.getTitle());
        faculty.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        return toDto(facultyRepository.save(faculty));
    }

    private FacultyDto toDto(Faculty f) {
        return FacultyDto.builder()
                .id(f.getId())
                .firstName(f.getFirstName())
                .lastName(f.getLastName())
                .email(f.getEmail())
                .department(f.getDepartment())
                .title(f.getTitle())
                .status(f.getStatus())
                .build();
    }
}
