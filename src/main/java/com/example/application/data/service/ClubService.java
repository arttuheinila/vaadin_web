package com.example.application.data.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.application.data.Club;
import com.example.application.data.Student;
import com.example.application.data.repository.ClubRepository;
import com.example.application.data.repository.StudentRepository;

@Service
public class ClubService {

    private final ClubRepository clubRepository;
    private final StudentRepository studentRepository;

    public ClubService(ClubRepository clubRepository, StudentRepository studentRepository) {
        this.clubRepository = clubRepository;
        this.studentRepository = studentRepository;
    }

    public List<Club> findAll(String filter) {
        return clubRepository.search(normalizeFilter(filter));
    }

    public List<Club> findAll() {
        return clubRepository.findAll().stream().sorted((left, right) -> left.getName().compareToIgnoreCase(right.getName()))
                .toList();
    }

    @Transactional
    public Club save(Club club) {
        return clubRepository.save(club);
    }

    @Transactional
    public void delete(Club club) {
        List<Student> students = new ArrayList<>(studentRepository.search("").stream()
                .filter(student -> student.getClubs().contains(club))
                .toList());
        for (Student student : students) {
            student.removeClub(club);
            studentRepository.save(student);
        }
        clubRepository.delete(club);
    }

    private String normalizeFilter(String filter) {
        return filter == null ? "" : filter.trim();
    }
}
