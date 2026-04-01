package com.example.application.views.profiles;

import java.time.LocalDate;

import org.springframework.dao.DataIntegrityViolationException;

import com.example.application.data.Student;
import com.example.application.data.StudentProfile;
import com.example.application.data.service.StudentProfileService;
import com.example.application.data.service.StudentService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "profiles", layout = MainLayout.class)
@PageTitle("Profiilit")
@RolesAllowed({ "ADMIN", "SUPER" })
public class StudentProfileView extends Div {

    private final StudentProfileService profileService;
    private final StudentService studentService;

    private final Grid<StudentProfile> grid = new Grid<>(StudentProfile.class, false);
    private final TextField filter = new TextField();
    private final StudentProfileForm form = new StudentProfileForm();

    public StudentProfileView(StudentProfileService profileService, StudentService studentService) {
        this.profileService = profileService;
        this.studentService = studentService;

        addClassName("crud-view");
        setSizeFull();

        configureGrid();
        form.addSaveListener(event -> saveProfile(event.getProfile()));
        form.addDeleteListener(event -> deleteProfile(event.getProfile()));
        form.addCloseListener(event -> closeEditor());

        add(buildToolbar(), buildContent());
        refreshGrid();
        closeEditor();
    }

    private HorizontalLayout buildToolbar() {
        filter.setPlaceholder("Suodata profiileja");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(event -> refreshGrid());

        Button addProfile = new Button("Lisää profiili", event -> editProfile(new StudentProfile()));
        addProfile.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return new HorizontalLayout(filter, addProfile);
    }

    private Component buildContent() {
        SplitLayout content = new SplitLayout(grid, form);
        content.setSizeFull();
        return content;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addColumn(profile -> profile.getStudent().getFullName()).setHeader("Opiskelija").setAutoWidth(true);
        grid.addColumn(profile -> profile.getStudent().getDepartment() != null ? profile.getStudent().getDepartment().getName() : "-")
                .setHeader("Osasto").setAutoWidth(true);
        grid.addColumn(StudentProfile::getStreetAddress).setHeader("Osoite").setAutoWidth(true);
        grid.addColumn(StudentProfile::getPostalCode).setHeader("Postinumero").setAutoWidth(true);
        grid.addColumn(StudentProfile::getEmergencyContactName).setHeader("Hätäyhteys").setAutoWidth(true);
        grid.addColumn(StudentProfile::getStudyGoal).setHeader("Tavoite").setFlexGrow(1);
        grid.asSingleSelect().addValueChangeListener(event -> editProfile(event.getValue()));
    }

    private void refreshGrid() {
        grid.setItems(profileService.findAll(filter.getValue()));
    }

    private void editProfile(StudentProfile profile) {
        if (profile == null) {
            closeEditor();
            return;
        }
        Long selectedStudentId = profile.getStudent() != null ? profile.getStudent().getId() : null;
        form.setStudents(studentService.findAvailableForProfile(selectedStudentId));
        form.setProfile(profile);
        form.setVisible(true);
    }

    private void closeEditor() {
        form.setProfile(null);
        form.setVisible(false);
        grid.asSingleSelect().clear();
    }

    private void saveProfile(StudentProfile profile) {
        try {
            profileService.save(profile);
            Notification.show("Profiili tallennettu");
            refreshGrid();
            closeEditor();
        } catch (IllegalArgumentException | DataIntegrityViolationException exception) {
            Notification.show(exception.getMessage());
        }
    }

    private void deleteProfile(StudentProfile profile) {
        profileService.delete(profile);
        Notification.show("Profiili poistettu");
        refreshGrid();
        closeEditor();
    }

    public static class StudentProfileForm extends VerticalLayout {

        private final BeanValidationBinder<StudentProfile> binder = new BeanValidationBinder<>(StudentProfile.class);

        private final ComboBox<Student> student = new ComboBox<>("Opiskelija");
        private final TextField streetAddress = new TextField("Katuosoite");
        private final TextField postalCode = new TextField("Postinumero");
        private final TextField emergencyContactName = new TextField("Hätäyhteyshenkilö");
        private final TextField emergencyContactPhone = new TextField("Hätäyhteyshenkilön puhelin");
        private final DatePicker birthDate = new DatePicker("Syntymäpäivä");
        private final TextArea studyGoal = new TextArea("Opintotavoite");

        private final Button save = new Button("Tallenna");
        private final Button delete = new Button("Poista");
        private final Button cancel = new Button("Sulje");

        private StudentProfile profile;

        public StudentProfileForm() {
            addClassName("editor-form");
            setWidth("420px");

            student.setItemLabelGenerator(Student::getFullName);
            birthDate.setMax(LocalDate.now().minusDays(1));
            studyGoal.setMaxLength(200);

            binder.forField(student)
                    .asRequired("Opiskelija on pakollinen")
                    .bind(StudentProfile::getStudent, StudentProfile::setStudent);
            binder.forField(streetAddress).bind(StudentProfile::getStreetAddress, StudentProfile::setStreetAddress);
            binder.forField(postalCode).bind(StudentProfile::getPostalCode, StudentProfile::setPostalCode);
            binder.forField(emergencyContactName)
                    .bind(StudentProfile::getEmergencyContactName, StudentProfile::setEmergencyContactName);
            binder.forField(emergencyContactPhone)
                    .bind(StudentProfile::getEmergencyContactPhone, StudentProfile::setEmergencyContactPhone);
            binder.forField(birthDate).bind(StudentProfile::getBirthDate, StudentProfile::setBirthDate);
            binder.forField(studyGoal).bind(StudentProfile::getStudyGoal, StudentProfile::setStudyGoal);

            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);

            save.addClickListener(event -> validateAndSave());
            delete.addClickListener(event -> fireEvent(new DeleteEvent(this, profile)));
            cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));

            add(new Span("Profiilin tiedot"), student, streetAddress, postalCode, emergencyContactName,
                    emergencyContactPhone, birthDate, studyGoal, new HorizontalLayout(save, delete, cancel));
        }

        public void setStudents(java.util.Collection<Student> students) {
            student.setItems(students);
        }

        public void setProfile(StudentProfile profile) {
            this.profile = profile;
            if (profile == null) {
                binder.readBean(new StudentProfile());
                return;
            }
            binder.readBean(profile);
            delete.setVisible(profile.getId() != null);
        }

        private void validateAndSave() {
            if (profile == null) {
                profile = new StudentProfile();
            }
            try {
                binder.writeBean(profile);
                fireEvent(new SaveEvent(this, profile));
            } catch (ValidationException exception) {
                Notification.show("Korjaa lomakkeen virheet ennen tallennusta");
            }
        }

        public void addSaveListener(ComponentEventListener<SaveEvent> listener) {
            addListener(SaveEvent.class, listener);
        }

        public void addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
            addListener(DeleteEvent.class, listener);
        }

        public void addCloseListener(ComponentEventListener<CloseEvent> listener) {
            addListener(CloseEvent.class, listener);
        }
    }

    public abstract static class StudentProfileFormEvent extends
            com.vaadin.flow.component.ComponentEvent<StudentProfileForm> {

        private final StudentProfile profile;

        protected StudentProfileFormEvent(StudentProfileForm source, StudentProfile profile) {
            super(source, false);
            this.profile = profile;
        }

        public StudentProfile getProfile() {
            return profile;
        }
    }

    public static class SaveEvent extends StudentProfileFormEvent {
        SaveEvent(StudentProfileForm source, StudentProfile profile) {
            super(source, profile);
        }
    }

    public static class DeleteEvent extends StudentProfileFormEvent {
        DeleteEvent(StudentProfileForm source, StudentProfile profile) {
            super(source, profile);
        }
    }

    public static class CloseEvent extends StudentProfileFormEvent {
        CloseEvent(StudentProfileForm source) {
            super(source, null);
        }
    }

}
