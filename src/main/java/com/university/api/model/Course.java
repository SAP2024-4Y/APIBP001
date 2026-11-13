package com.university.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String status;

    private Integer credits;

    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty instructor;
}
