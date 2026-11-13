package com.university.api.config;

import com.university.api.model.*;
import com.university.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final FacultyRepository facultyRepo;
    private final CourseRepository courseRepo;
    private final StudentRepository studentRepo;
    private final CampusLocationRepository locationRepo;
    private final CourseScheduleRepository scheduleRepo;

    @Override
    public void run(String... args) {
        if (facultyRepo.count() > 0) {
            log.info("Database already seeded, skipping.");
            return;
        }
        log.info("Seeding database...");
        seedFaculty();
        seedLocations();
        seedCourses();
        seedStudents();
        seedSchedules();
        log.info("Seeding complete.");
    }

    private Faculty f1, f2, f3, f4, f5;

    private void seedFaculty() {
        f1 = facultyRepo.save(faculty("Alice", "Johnson", "alice.johnson@university.edu", "Computer Science", "Professor", "ACTIVE"));
        f2 = facultyRepo.save(faculty("Bob", "Williams", "bob.williams@university.edu", "Mathematics", "Associate Professor", "ACTIVE"));
        f3 = facultyRepo.save(faculty("Carol", "Davis", "carol.davis@university.edu", "Physics", "Assistant Professor", "ACTIVE"));
        f4 = facultyRepo.save(faculty("David", "Miller", "david.miller@university.edu", "Engineering", "Professor", "ACTIVE"));
        f5 = facultyRepo.save(faculty("Eve", "Wilson", "eve.wilson@university.edu", "Biology", "Lecturer", "ACTIVE"));
    }

    private CampusLocation l1, l2, l3, l4, l5;

    private void seedLocations() {
        l1 = locationRepo.save(location("Main Auditorium", "AUDITORIUM", "Main Hall", "101", 500, 40.7128, -74.0060, "1 University Ave"));
        l2 = locationRepo.save(location("CS Lab A", "LAB", "Tech Building", "201", 40, 40.7135, -74.0065, "2 Tech Street"));
        l3 = locationRepo.save(location("Math Classroom 1", "CLASSROOM", "Science Block", "301", 60, 40.7140, -74.0055, "3 Science Blvd"));
        l4 = locationRepo.save(location("Physics Lab", "LAB", "Physics Wing", "101", 30, 40.7120, -74.0070, "4 Physics Lane"));
        l5 = locationRepo.save(location("Engineering Hall", "CLASSROOM", "Engineering Complex", "401", 80, 40.7150, -74.0045, "5 Engineer Road"));
    }

    private void seedCourses() {
        courseRepo.save(course("CS101", "Introduction to Programming", "Fundamentals of programming using Java", "Computer Science", "ACTIVE", 3, new BigDecimal("999.00"), f1));
        courseRepo.save(course("CS201", "Data Structures", "Arrays, linked lists, trees, and graphs", "Computer Science", "ACTIVE", 3, new BigDecimal("1099.00"), f1));
        courseRepo.save(course("CS301", "Algorithms", "Algorithm design and complexity analysis", "Computer Science", "ACTIVE", 3, new BigDecimal("1099.00"), f1));
        courseRepo.save(course("MATH101", "Calculus I", "Limits, derivatives, and integrals", "Mathematics", "ACTIVE", 4, new BigDecimal("799.00"), f2));
        courseRepo.save(course("MATH201", "Linear Algebra", "Vectors, matrices, and linear transformations", "Mathematics", "ACTIVE", 3, new BigDecimal("849.00"), f2));
        courseRepo.save(course("PHYS101", "Classical Mechanics", "Newton's laws and kinematics", "Physics", "ACTIVE", 4, new BigDecimal("899.00"), f3));
        courseRepo.save(course("PHYS201", "Electromagnetism", "Electric and magnetic fields", "Physics", "ACTIVE", 4, new BigDecimal("949.00"), f3));
        courseRepo.save(course("ENG101", "Introduction to Engineering", "Engineering principles and design", "Engineering", "ACTIVE", 3, new BigDecimal("1049.00"), f4));
        courseRepo.save(course("BIO101", "Cell Biology", "Structure and function of cells", "Biology", "ACTIVE", 3, new BigDecimal("799.00"), f5));
        courseRepo.save(course("CS401", "Machine Learning", "Supervised and unsupervised learning", "Computer Science", "ACTIVE", 3, new BigDecimal("1199.00"), f1));
    }

    private void seedStudents() {
        studentRepo.save(student("Emma", "Brown", "emma.brown@student.edu", "ACTIVE", "Computer Science", 2023));
        studentRepo.save(student("James", "Taylor", "james.taylor@student.edu", "ACTIVE", "Mathematics", 2022));
        studentRepo.save(student("Olivia", "Anderson", "olivia.anderson@student.edu", "ACTIVE", "Physics", 2023));
        studentRepo.save(student("Noah", "Thomas", "noah.thomas@student.edu", "ACTIVE", "Engineering", 2021));
        studentRepo.save(student("Ava", "Jackson", "ava.jackson@student.edu", "ACTIVE", "Biology", 2024));
        studentRepo.save(student("Liam", "White", "liam.white@student.edu", "ACTIVE", "Computer Science", 2022));
        studentRepo.save(student("Sophia", "Harris", "sophia.harris@student.edu", "INACTIVE", "Mathematics", 2020));
        studentRepo.save(student("Mason", "Martin", "mason.martin@student.edu", "ACTIVE", "Computer Science", 2023));
        studentRepo.save(student("Isabella", "Garcia", "isabella.garcia@student.edu", "ACTIVE", "Physics", 2024));
        studentRepo.save(student("Ethan", "Martinez", "ethan.martinez@student.edu", "ACTIVE", "Engineering", 2022));
    }

    private void seedSchedules() {
        var courses = courseRepo.findAll();
        if (courses.size() < 5) return;

        LocalDateTime base = LocalDateTime.of(2025, 2, 10, 9, 0);

        scheduleRepo.save(schedule(courses.get(0), l2, base, base.plusHours(2), "WEEKLY", "SCHEDULED"));
        scheduleRepo.save(schedule(courses.get(1), l2, base.plusDays(2), base.plusDays(2).plusHours(2), "WEEKLY", "SCHEDULED"));
        scheduleRepo.save(schedule(courses.get(2), l5, base.plusDays(4), base.plusDays(4).plusHours(2), "WEEKLY", "SCHEDULED"));
        scheduleRepo.save(schedule(courses.get(3), l3, base.plusHours(3), base.plusHours(5), "WEEKLY", "SCHEDULED"));
        scheduleRepo.save(schedule(courses.get(4), l3, base.plusDays(2).plusHours(3), base.plusDays(2).plusHours(5), "WEEKLY", "SCHEDULED"));
        scheduleRepo.save(schedule(courses.get(5), l4, base.plusDays(1), base.plusDays(1).plusHours(2), "WEEKLY", "SCHEDULED"));
        scheduleRepo.save(schedule(courses.get(6), l4, base.plusDays(3), base.plusDays(3).plusHours(2), "WEEKLY", "SCHEDULED"));
        scheduleRepo.save(schedule(courses.get(7), l5, base.plusDays(1).plusHours(3), base.plusDays(1).plusHours(5), "WEEKLY", "SCHEDULED"));
        scheduleRepo.save(schedule(courses.get(8), l3, base.plusDays(4).plusHours(3), base.plusDays(4).plusHours(5), "WEEKLY", "SCHEDULED"));
        scheduleRepo.save(schedule(courses.get(9), l2, base.plusDays(3).plusHours(3), base.plusDays(3).plusHours(5), "WEEKLY", "SCHEDULED"));
    }

    private Faculty faculty(String first, String last, String email, String dept, String title, String status) {
        Faculty f = new Faculty();
        f.setFirstName(first); f.setLastName(last); f.setEmail(email);
        f.setDepartment(dept); f.setTitle(title); f.setStatus(status);
        return f;
    }

    private CampusLocation location(String name, String type, String building, String room,
                                    int capacity, double lat, double lon, String address) {
        CampusLocation l = new CampusLocation();
        l.setName(name); l.setType(type); l.setBuilding(building); l.setRoom(room);
        l.setCapacity(capacity); l.setLatitude(lat); l.setLongitude(lon); l.setAddress(address);
        return l;
    }

    private Course course(String code, String title, String desc, String category, String status,
                          int credits, BigDecimal price, Faculty instructor) {
        Course c = new Course();
        c.setCode(code); c.setTitle(title); c.setDescription(desc); c.setCategory(category);
        c.setStatus(status); c.setCredits(credits); c.setPrice(price); c.setInstructor(instructor);
        return c;
    }

    private Student student(String first, String last, String email, String status, String major, int year) {
        Student s = new Student();
        s.setFirstName(first); s.setLastName(last); s.setEmail(email);
        s.setStatus(status); s.setMajor(major); s.setEnrollmentYear(year);
        return s;
    }

    private CourseSchedule schedule(Course course, CampusLocation location,
                                    LocalDateTime start, LocalDateTime end,
                                    String recurrence, String status) {
        CourseSchedule s = new CourseSchedule();
        s.setCourse(course); s.setLocation(location);
        s.setStartTime(start); s.setEndTime(end);
        s.setRecurrenceRule(recurrence); s.setStatus(status);
        return s;
    }
}
