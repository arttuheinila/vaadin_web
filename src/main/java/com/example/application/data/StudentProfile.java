package com.example.application.data;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "student_profile")
public class StudentProfile extends AbstractEntity {

    @NotBlank(message = "Katuosoite on pakollinen")
    @Size(min = 5, max = 120, message = "Katuosoitteen pituuden tulee olla 5-120 merkkiä")
    @Column(nullable = false, length = 120)
    private String streetAddress;

    @NotBlank(message = "Postinumero on pakollinen")
    @Pattern(regexp = "\\d{5}", message = "Postinumeron tulee olla viisi numeroa")
    @Column(nullable = false, length = 5)
    private String postalCode;

    @NotBlank(message = "Hätäyhteyshenkilö on pakollinen")
    @Size(min = 2, max = 80, message = "Nimen pituuden tulee olla 2-80 merkkiä")
    @Column(nullable = false, length = 80)
    private String emergencyContactName;

    @NotBlank(message = "Hätäyhteyshenkilön puhelin on pakollinen")
    @Pattern(regexp = "\\+?[0-9 -]{7,20}", message = "Syötä puhelinnumero kansainvälisessä tai kotimaisessa muodossa")
    @Column(nullable = false, length = 20)
    private String emergencyContactPhone;

    @NotNull(message = "Syntymäpäivä on pakollinen")
    @Past(message = "Syntymäpäivän tulee olla menneisyydessä")
    @Column(nullable = false)
    private LocalDate birthDate;

    @NotBlank(message = "Opintotavoite on pakollinen")
    @Size(min = 10, max = 200, message = "Tavoitteen pituuden tulee olla 10-200 merkkiä")
    @Column(nullable = false, length = 200)
    private String studyGoal;

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getStudyGoal() {
        return studyGoal;
    }

    public void setStudyGoal(String studyGoal) {
        this.studyGoal = studyGoal;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        if (this.student != null && this.student != student) {
            this.student.setProfile(null);
        }
        this.student = student;
        if (student != null && student.getProfile() != this) {
            student.setProfile(this);
        }
    }
}
