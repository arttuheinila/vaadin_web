package com.example.application.data.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.application.data.StudentProfile;
import com.example.application.data.repository.StudentProfileRepository;

@Service
public class StudentProfileService {

    private final StudentProfileRepository profileRepository;

    public StudentProfileService(StudentProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public List<StudentProfile> findAll(String filter) {
        return profileRepository.search(normalizeFilter(filter));
    }

    @Transactional
    public StudentProfile save(StudentProfile profile) {
        profileRepository.findByStudentId(profile.getStudent().getId())
                .filter(existing -> !existing.equals(profile))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Valitulla opiskelijalla on jo profiili");
                });
        return profileRepository.save(profile);
    }

    @Transactional
    public void delete(StudentProfile profile) {
        profileRepository.delete(profile);
    }

    private String normalizeFilter(String filter) {
        return filter == null ? "" : filter.trim();
    }
}
