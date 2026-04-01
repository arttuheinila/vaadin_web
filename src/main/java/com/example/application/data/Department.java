package com.example.application.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "department")
public class Department extends AbstractEntity {

    @NotBlank(message = "Osaston nimi on pakollinen")
    @Size(min = 2, max = 60, message = "Osaston nimen pituuden tulee olla 2-60 merkkiä")
    @Column(nullable = false, length = 60)
    private String name;

    @NotBlank(message = "Osastokoodi on pakollinen")
    @Pattern(regexp = "[A-Z]{2,5}-\\d{2}", message = "Käytä muotoa ABC-12")
    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @NotBlank(message = "Rakennus on pakollinen")
    @Size(min = 2, max = 60, message = "Rakennuksen nimen pituuden tulee olla 2-60 merkkiä")
    @Column(nullable = false, length = 60)
    private String building;

    @NotBlank(message = "Sähköposti on pakollinen")
    @Email(message = "Sähköpostin pitää olla kelvollinen")
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @NotBlank(message = "Puhelinnumero on pakollinen")
    @Pattern(regexp = "\\+?[0-9 -]{7,20}", message = "Syötä puhelinnumero kansainvälisessä tai kotimaisessa muodossa")
    @Column(nullable = false, length = 20)
    private String phone;

    @NotNull(message = "Budjetti on pakollinen")
    @DecimalMin(value = "1000.00", message = "Budjetin tulee olla vähintään 1000")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal annualBudget;

    @OneToMany(mappedBy = "department", cascade = CascadeType.PERSIST)
    private List<Student> students = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getAnnualBudget() {
        return annualBudget;
    }

    public void setAnnualBudget(BigDecimal annualBudget) {
        this.annualBudget = annualBudget;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void addStudent(Student student) {
        if (!students.contains(student)) {
            students.add(student);
        }
        if (student.getDepartment() != this) {
            student.setDepartment(this);
        }
    }

    public void removeStudent(Student student) {
        students.remove(student);
        if (student.getDepartment() == this) {
            student.setDepartment(null);
        }
    }
}
