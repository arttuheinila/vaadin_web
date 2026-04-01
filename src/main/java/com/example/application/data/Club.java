package com.example.application.data;

import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "club")
public class Club extends AbstractEntity {

    @NotBlank(message = "Kerhon nimi on pakollinen")
    @Size(min = 2, max = 60, message = "Kerhon nimen pituuden tulee olla 2-60 merkkiä")
    @Column(nullable = false, unique = true, length = 60)
    private String name;

    @NotBlank(message = "Kategoria on pakollinen")
    @Size(min = 3, max = 40, message = "Kategorian pituuden tulee olla 3-40 merkkiä")
    @Column(nullable = false, length = 40)
    private String category;

    @NotBlank(message = "Kuvaus on pakollinen")
    @Size(min = 10, max = 250, message = "Kuvauksen pituuden tulee olla 10-250 merkkiä")
    @Column(nullable = false, length = 250)
    private String description;

    @NotBlank(message = "Kokoontumispäivä on pakollinen")
    @Size(min = 2, max = 20, message = "Päivän pituuden tulee olla 2-20 merkkiä")
    @Column(nullable = false, length = 20)
    private String meetingDay;

    @NotNull(message = "Kokoontumisaika on pakollinen")
    @Column(nullable = false)
    private LocalTime meetingTime;

    @NotBlank(message = "Tilan nimi on pakollinen")
    @Size(min = 2, max = 30, message = "Tilan nimen pituuden tulee olla 2-30 merkkiä")
    @Column(nullable = false, length = 30)
    private String room;

    @ManyToMany(mappedBy = "clubs")
    private Set<Student> students = new LinkedHashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMeetingDay() {
        return meetingDay;
    }

    public void setMeetingDay(String meetingDay) {
        this.meetingDay = meetingDay;
    }

    public LocalTime getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(LocalTime meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Set<Student> getStudents() {
        return students;
    }
}
