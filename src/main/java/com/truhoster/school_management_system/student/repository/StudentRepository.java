package com.truhoster.school_management_system.student.repository;

import com.truhoster.school_management_system.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    List<Student> findByClassroomId(Integer classroomId);

    boolean existsByMatricule(String matricule);
}
