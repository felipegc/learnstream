package com.felipe.learnstream.jpa.models;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @ManyToMany(mappedBy = "likedCourses")
    Set<Student> likes;
}
