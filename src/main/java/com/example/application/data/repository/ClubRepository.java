package com.example.application.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.application.data.Club;

public interface ClubRepository extends JpaRepository<Club, Long> {

    @Query("""
            select distinct c from Club c
            left join fetch c.students s
            where :filter = '' or
                  lower(c.name) like lower(concat('%', :filter, '%')) or
                  lower(c.category) like lower(concat('%', :filter, '%')) or
                  lower(c.meetingDay) like lower(concat('%', :filter, '%')) or
                  lower(s.firstName) like lower(concat('%', :filter, '%')) or
                  lower(s.lastName) like lower(concat('%', :filter, '%')) or
                  lower(c.room) like lower(concat('%', :filter, '%'))
            order by c.name
            """)
    List<Club> search(@Param("filter") String filter);
}
