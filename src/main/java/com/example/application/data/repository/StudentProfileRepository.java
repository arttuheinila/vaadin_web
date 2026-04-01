package com.example.application.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.application.data.StudentProfile;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    @Query("""
            select p from StudentProfile p
            join fetch p.student s
            left join fetch s.department
            where :filter = '' or
                  lower(s.firstName) like lower(concat('%', :filter, '%')) or
                  lower(s.lastName) like lower(concat('%', :filter, '%')) or
                  lower(p.postalCode) like lower(concat('%', :filter, '%')) or
                  lower(p.emergencyContactName) like lower(concat('%', :filter, '%')) or
                  lower(p.studyGoal) like lower(concat('%', :filter, '%'))
            order by s.lastName, s.firstName
            """)
    List<StudentProfile> search(@Param("filter") String filter);

    Optional<StudentProfile> findByStudentId(Long studentId);
}
