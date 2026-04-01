package com.example.application.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.application.data.repository.ClubRepository;
import com.example.application.data.repository.DepartmentRepository;
import com.example.application.data.repository.AppUserRepository;
import com.example.application.data.repository.StudentProfileRepository;
import com.example.application.data.repository.StudentRepository;
import com.example.application.security.Role;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner loadDemoData(DepartmentRepository departmentRepository, ClubRepository clubRepository,
            StudentRepository studentRepository, StudentProfileRepository profileRepository,
            AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            createUserIfMissing(appUserRepository, passwordEncoder, "admin", "Campus Admin", "admin@kampus.fi",
                    "admin123", Set.of(Role.ADMIN));
            createUserIfMissing(appUserRepository, passwordEncoder, "super", "Campus Super", "super@kampus.fi",
                    "super123", Set.of(Role.SUPER));
            createUserIfMissing(appUserRepository, passwordEncoder, "user", "Campus User", "user@kampus.fi",
                    "user123", Set.of(Role.USER));

            if (departmentRepository.count() > 0) {
                return;
            }

            Department software = new Department();
            software.setName("Ohjelmistotekniikka");
            software.setCode("SOFT-10");
            software.setBuilding("Tietotalo A");
            software.setEmail("soft@kampus.fi");
            software.setPhone("+358 40 123 4567");
            software.setAnnualBudget(new BigDecimal("450000.00"));

            Department business = new Department();
            business.setName("Liiketalous");
            business.setCode("BIZ-20");
            business.setBuilding("Päärakennus");
            business.setEmail("biz@kampus.fi");
            business.setPhone("+358 40 555 1122");
            business.setAnnualBudget(new BigDecimal("320000.00"));

            departmentRepository.save(software);
            departmentRepository.save(business);

            Club robotics = new Club();
            robotics.setName("Robotiikkakerho");
            robotics.setCategory("Tekniikka");
            robotics.setDescription("Rakennetaan robotteja ja harjoitellaan automaatioprojekteja.");
            robotics.setMeetingDay("Tiistai");
            robotics.setMeetingTime(LocalTime.of(17, 0));
            robotics.setRoom("Labra 203");

            Club debate = new Club();
            debate.setName("Debattiklubi");
            debate.setCategory("Viestintä");
            debate.setDescription("Harjoitellaan esiintymistä, argumentointia ja väittelyä.");
            debate.setMeetingDay("Torstai");
            debate.setMeetingTime(LocalTime.of(16, 30));
            debate.setRoom("Aula 2");

            Club sports = new Club();
            sports.setName("Liikuntatiimi");
            sports.setCategory("Hyvinvointi");
            sports.setDescription("Järjestetään matalan kynnyksen liikuntaa ja tapahtumia opiskelijoille.");
            sports.setMeetingDay("Maanantai");
            sports.setMeetingTime(LocalTime.of(18, 0));
            sports.setRoom("Sali 1");

            clubRepository.save(robotics);
            clubRepository.save(debate);
            clubRepository.save(sports);

            Student aino = new Student();
            aino.setFirstName("Aino");
            aino.setLastName("Lahti");
            aino.setEmail("aino.lahti@kampus.fi");
            aino.setStudentNumber("S12345");
            aino.setCity("Tampere");
            aino.setPhone("+358 50 123 0001");
            aino.setEnrollmentYear(2023);
            aino.setDepartment(software);
            aino.addClub(robotics);
            aino.addClub(sports);

            Student mika = new Student();
            mika.setFirstName("Mika");
            mika.setLastName("Virtanen");
            mika.setEmail("mika.virtanen@kampus.fi");
            mika.setStudentNumber("S12346");
            mika.setCity("Turku");
            mika.setPhone("+358 50 123 0002");
            mika.setEnrollmentYear(2022);
            mika.setDepartment(business);
            mika.addClub(debate);

            Student sara = new Student();
            sara.setFirstName("Sara");
            sara.setLastName("Korhonen");
            sara.setEmail("sara.korhonen@kampus.fi");
            sara.setStudentNumber("S12347");
            sara.setCity("Oulu");
            sara.setPhone("+358 50 123 0003");
            sara.setEnrollmentYear(2024);
            sara.setDepartment(software);
            sara.addClub(robotics);
            sara.addClub(debate);

            studentRepository.save(aino);
            studentRepository.save(mika);
            studentRepository.save(sara);

            StudentProfile ainoProfile = new StudentProfile();
            ainoProfile.setStreetAddress("Esimerkkikatu 1 A 2");
            ainoProfile.setPostalCode("33100");
            ainoProfile.setEmergencyContactName("Leena Lahti");
            ainoProfile.setEmergencyContactPhone("+358 40 765 1001");
            ainoProfile.setBirthDate(LocalDate.of(2001, 4, 12));
            ainoProfile.setStudyGoal("Tavoitteena on erikoistua ohjelmistotestaukseen.");
            ainoProfile.setStudent(aino);

            StudentProfile mikaProfile = new StudentProfile();
            mikaProfile.setStreetAddress("Satamakatu 8 B 11");
            mikaProfile.setPostalCode("20100");
            mikaProfile.setEmergencyContactName("Pekka Virtanen");
            mikaProfile.setEmergencyContactPhone("+358 40 765 1002");
            mikaProfile.setBirthDate(LocalDate.of(2000, 9, 3));
            mikaProfile.setStudyGoal("Tavoitteena on oppia datavetoista markkinointia.");
            mikaProfile.setStudent(mika);

            profileRepository.save(ainoProfile);
            profileRepository.save(mikaProfile);
        };
    }

    private void createUserIfMissing(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder,
            String username, String displayName, String email, String rawPassword, Set<Role> roles) {
        if (appUserRepository.findByUsernameIgnoreCase(username).isPresent()) {
            return;
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRoles(roles);
        user.setActive(true);
        appUserRepository.save(user);
    }
}
