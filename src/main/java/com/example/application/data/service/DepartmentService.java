package com.example.application.data.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.application.data.Department;
import com.example.application.data.Student;
import com.example.application.data.repository.DepartmentRepository;
import com.example.application.data.repository.StudentRepository;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;

    public DepartmentService(DepartmentRepository departmentRepository, StudentRepository studentRepository) {
        this.departmentRepository = departmentRepository;
        this.studentRepository = studentRepository;
    }

    public List<Department> findAll(String filter) {
        return departmentRepository.search(normalizeFilter(filter));
    }

    public List<Department> findAll() {
        return departmentRepository.findAll().stream().sorted((left, right) -> left.getName().compareToIgnoreCase(right.getName()))
                .toList();
    }

    @Transactional
    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    @Transactional
    public void delete(Department department) {
        List<Student> students = studentRepository.search("").stream()
                .filter(student -> student.getDepartment() != null && student.getDepartment().equals(department))
                .toList();
        for (Student student : students) {
            student.setDepartment(null);
            studentRepository.save(student);
        }
        departmentRepository.delete(department);
    }

    private String normalizeFilter(String filter) {
        return filter == null ? "" : filter.trim();
    }
}
