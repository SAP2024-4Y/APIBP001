package com.university.api.repository;

import com.university.api.model.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long> {

    List<CourseSchedule> findByCourseId(Long courseId);

    @Query("SELECT s FROM CourseSchedule s WHERE s.startTime >= :start AND s.endTime <= :end")
    List<CourseSchedule> findByWindow(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT s FROM CourseSchedule s WHERE s.startTime < :end AND s.endTime > :start AND s.location.id = :locationId")
    List<CourseSchedule> findConflictsByLocation(
            @Param("locationId") Long locationId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT s FROM CourseSchedule s WHERE s.startTime < :end AND s.endTime > :start AND s.course.instructor.id = :facultyId")
    List<CourseSchedule> findConflictsByFaculty(
            @Param("facultyId") Long facultyId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT s FROM CourseSchedule s WHERE s.startTime < :end AND s.endTime > :start AND s.course.id = :courseId AND s.id <> :excludeId")
    List<CourseSchedule> findConflictsByCourse(
            @Param("courseId") Long courseId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("excludeId") Long excludeId);
}
