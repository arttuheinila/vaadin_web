package com.example.application.data.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.application.data.Student;
import com.example.application.data.repository.StudentRepository;
import com.example.application.data.search.StudentSearchCriteria;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> findAll(String filter) {
        return studentRepository.search(normalizeFilter(filter));
    }

    public List<Student> findAvailableForProfile(Long selectedStudentId) {
        return studentRepository.findStudentsAvailableForProfile(selectedStudentId);
    }

    public List<Student> advancedSearch(StudentSearchCriteria criteria) {
        return studentRepository.advancedSearch(criteria);
    }

    @Transactional
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    @Transactional
    public void delete(Student student) {
        studentRepository.delete(student);
    }

    private String normalizeFilter(String filter) {
        return filter == null ? "" : filter.trim();
    }
}
