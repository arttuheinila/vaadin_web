package com.example.application.data.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Repository;

import com.example.application.data.Club;
import com.example.application.data.Department;
import com.example.application.data.Student;
import com.example.application.data.StudentProfile;
import com.example.application.data.search.StudentSearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class StudentRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Student> search(String filter) {
        StudentSearchCriteria criteria = new StudentSearchCriteria();
        criteria.setFilterText(filter);
        return advancedSearch(criteria);
    }

    public List<Student> advancedSearch(StudentSearchCriteria searchCriteria) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> query = builder.createQuery(Student.class);
        Root<Student> root = query.from(Student.class);

        root.fetch("department", JoinType.LEFT);
        root.fetch("profile", JoinType.LEFT);
        root.fetch("clubs", JoinType.LEFT);

        Join<Student, Department> departmentJoin = root.join("department", JoinType.LEFT);
        Join<Student, StudentProfile> profileJoin = root.join("profile", JoinType.LEFT);
        Join<Student, Club> clubJoin = root.join("clubs", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        // (X OR Y) AND Z: keyword is an OR block, and department/city/date filters are added with AND.
        String filterText = normalize(searchCriteria.getFilterText());
        if (!filterText.isBlank()) {
            String pattern = likePattern(filterText);
            predicates.add(builder.or(
                    builder.like(builder.lower(root.get("firstName")), pattern),
                    builder.like(builder.lower(root.get("lastName")), pattern),
                    builder.like(builder.lower(root.get("email")), pattern),
                    builder.like(builder.lower(root.get("studentNumber")), pattern)));
        }

        String city = normalize(searchCriteria.getCity());
        if (!city.isBlank()) {
            predicates.add(builder.like(builder.lower(root.get("city")), likePattern(city)));
        }

        if (searchCriteria.getDepartmentId() != null) {
            predicates.add(builder.equal(departmentJoin.get("id"), searchCriteria.getDepartmentId()));
        }

        if (searchCriteria.getClubId() != null) {
            predicates.add(builder.equal(clubJoin.get("id"), searchCriteria.getClubId()));
        }

        String emergencyContact = normalize(searchCriteria.getEmergencyContactName());
        if (!emergencyContact.isBlank()) {
            predicates.add(builder.like(builder.lower(profileJoin.get("emergencyContactName")), likePattern(emergencyContact)));
        }

        if (searchCriteria.getBirthDateFrom() != null) {
            predicates.add(builder.greaterThanOrEqualTo(profileJoin.get("birthDate"), searchCriteria.getBirthDateFrom()));
        }

        if (searchCriteria.getBirthDateTo() != null) {
            predicates.add(builder.lessThanOrEqualTo(profileJoin.get("birthDate"), searchCriteria.getBirthDateTo()));
        }

        query.select(root).distinct(true);
        if (!predicates.isEmpty()) {
            query.where(builder.and(predicates.toArray(Predicate[]::new)));
        }
        query.orderBy(builder.asc(root.get("lastName")), builder.asc(root.get("firstName")));

        return entityManager.createQuery(query).getResultList();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String likePattern(String value) {
        return "%" + value + "%";
    }
}
