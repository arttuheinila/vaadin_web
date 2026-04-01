package com.example.application.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.application.data.Student;
import com.example.application.data.search.StudentSearchCriteria;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> search(String filter);

    List<Student> advancedSearch(StudentSearchCriteria criteria);

    @Query("""
            select s from Student s
            left join fetch s.department
            left join fetch s.profile
            where s.profile is null or s.id = :selectedStudentId
            order by s.lastName, s.firstName
            """)
    List<Student> findStudentsAvailableForProfile(@Param("selectedStudentId") Long selectedStudentId);
}
