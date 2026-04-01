package com.example.application.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.application.data.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("""
            select d from Department d
            where :filter = '' or
                  lower(d.name) like lower(concat('%', :filter, '%')) or
                  lower(d.code) like lower(concat('%', :filter, '%')) or
                  lower(d.building) like lower(concat('%', :filter, '%')) or
                  lower(d.email) like lower(concat('%', :filter, '%'))
            order by d.name
            """)
    List<Department> search(@Param("filter") String filter);
}
