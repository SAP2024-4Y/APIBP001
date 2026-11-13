package com.university.api.repository;

import com.university.api.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c WHERE c.id > :afterId ORDER BY c.id ASC")
    List<Course> findPageAfter(@Param("afterId") Long afterId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT c FROM Course c ORDER BY c.id ASC")
    List<Course> findFirstPage(org.springframework.data.domain.Pageable pageable);

    List<Course> findByCategory(String category);
    List<Course> findByStatus(String status);
    List<Course> findByInstructorId(Long instructorId);
}
