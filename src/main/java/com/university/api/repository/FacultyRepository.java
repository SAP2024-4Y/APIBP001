package com.university.api.repository;

import com.university.api.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByEmail(String email);
    List<Faculty> findByDepartment(String department);
    List<Faculty> findByStatus(String status);
}
