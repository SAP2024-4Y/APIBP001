package com.university.api.repository;

import com.university.api.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);

    @Query("SELECT s FROM Student s WHERE s.id > :afterId ORDER BY s.id ASC")
    List<Student> findPageAfter(@Param("afterId") Long afterId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT s FROM Student s ORDER BY s.id ASC")
    List<Student> findFirstPage(org.springframework.data.domain.Pageable pageable);

    List<Student> findByStatus(String status);
    List<Student> findByMajor(String major);
}
