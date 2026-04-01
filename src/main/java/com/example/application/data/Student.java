package com.example.application.data;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "student")
public class Student extends AbstractEntity {

    @NotBlank(message = "Etunimi on pakollinen")
    @Size(min = 2, max = 50, message = "Etunimen pituuden tulee olla 2-50 merkkiä")
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Sukunimi on pakollinen")
    @Size(min = 2, max = 50, message = "Sukunimen pituuden tulee olla 2-50 merkkiä")
    @Column(nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "Sähköposti on pakollinen")
    @Email(message = "Sähköpostin pitää olla kelvollinen")
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @NotBlank(message = "Opiskelijanumero on pakollinen")
    @Pattern(regexp = "S\\d{5}", message = "Käytä muotoa S12345")
    @Column(nullable = false, unique = true, length = 6)
    private String studentNumber;

    @NotBlank(message = "Kotikunta on pakollinen")
    @Size(min = 2, max = 50, message = "Kotikunnan pituuden tulee olla 2-50 merkkiä")
    @Column(nullable = false, length = 50)
    private String city;

    @NotBlank(message = "Puhelinnumero on pakollinen")
    @Pattern(regexp = "\\+?[0-9 -]{7,20}", message = "Syötä puhelinnumero kansainvälisessä tai kotimaisessa muodossa")
    @Column(nullable = false, length = 20)
    private String phone;

    @NotNull(message = "Aloitusvuosi on pakollinen")
    @Min(value = 2020, message = "Aloitusvuoden tulee olla vähintään 2020")
    @Max(value = 2035, message = "Aloitusvuoden tulee olla enintään 2035")
    @Column(nullable = false)
    private Integer enrollmentYear;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne(mappedBy = "student", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private StudentProfile profile;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "student_club", joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "club_id"))
    private Set<Club> clubs = new LinkedHashSet<>();

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getEnrollmentYear() {
        return enrollmentYear;
    }

    public void setEnrollmentYear(Integer enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        if (this.department != null && this.department != department) {
            this.department.getStudents().remove(this);
        }
        this.department = department;
        if (department != null && !department.getStudents().contains(this)) {
            department.getStudents().add(this);
        }
    }

    public StudentProfile getProfile() {
        return profile;
    }

    public void setProfile(StudentProfile profile) {
        if (this.profile != null && this.profile != profile) {
            this.profile.setStudent(null);
        }
        this.profile = profile;
        if (profile != null && profile.getStudent() != this) {
            profile.setStudent(this);
        }
    }

    public Set<Club> getClubs() {
        return clubs;
    }

    public void setClubs(Set<Club> clubs) {
        this.clubs.clear();
        if (clubs != null) {
            clubs.forEach(this::addClub);
        }
    }

    public void addClub(Club club) {
        clubs.add(club);
        club.getStudents().add(this);
    }

    public void removeClub(Club club) {
        clubs.remove(club);
        club.getStudents().remove(this);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
