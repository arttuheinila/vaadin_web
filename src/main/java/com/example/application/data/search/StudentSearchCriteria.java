package com.example.application.data.search;

import java.time.LocalDate;

public class StudentSearchCriteria {

    private String filterText;
    private String city;
    private Long departmentId;
    private Long clubId;
    private String emergencyContactName;
    private LocalDate birthDateFrom;
    private LocalDate birthDateTo;

    public String getFilterText() {
        return filterText;
    }

    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public LocalDate getBirthDateFrom() {
        return birthDateFrom;
    }

    public void setBirthDateFrom(LocalDate birthDateFrom) {
        this.birthDateFrom = birthDateFrom;
    }

    public LocalDate getBirthDateTo() {
        return birthDateTo;
    }

    public void setBirthDateTo(LocalDate birthDateTo) {
        this.birthDateTo = birthDateTo;
    }
}
