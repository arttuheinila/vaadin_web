package com.example.application.views.students;

import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;

import com.example.application.data.Club;
import com.example.application.data.Department;
import com.example.application.data.Student;
import com.example.application.data.service.ClubService;
import com.example.application.data.service.DepartmentService;
import com.example.application.data.service.StudentService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "students", layout = MainLayout.class)
@PageTitle("Opiskelijat")
@RolesAllowed({ "ADMIN", "SUPER", "USER" })
public class StudentView extends Div {

    private final StudentService studentService;
    private final DepartmentService departmentService;
    private final ClubService clubService;

    private final Grid<Student> grid = new Grid<>(Student.class, false);
    private final TextField filter = new TextField();
    private final StudentForm form;

    public StudentView(StudentService studentService, DepartmentService departmentService, ClubService clubService) {
        this.studentService = studentService;
        this.departmentService = departmentService;
        this.clubService = clubService;

        addClassName("crud-view");
        setSizeFull();

        configureGrid();
        form = new StudentForm();
        form.setDepartments(departmentService.findAll());
        form.setClubs(clubService.findAll());
        form.addSaveListener(event -> saveStudent(event.getStudent()));
        form.addDeleteListener(event -> deleteStudent(event.getStudent()));
        form.addCloseListener(event -> closeEditor());

        add(buildToolbar(), buildContent());
        refreshGrid();
        closeEditor();
    }

    private HorizontalLayout buildToolbar() {
        filter.setPlaceholder("Suodata opiskelijoita");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(event -> refreshGrid());

        Button addStudent = new Button("Lisää opiskelija");
        addStudent.addClickListener(event -> editStudent(new Student()));
        addStudent.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout toolbar = new HorizontalLayout(filter, addStudent);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private Component buildContent() {
        SplitLayout content = new SplitLayout(grid, form);
        content.setSizeFull();
        content.addClassName("content");
        return content;
    }

    private void configureGrid() {
        grid.addClassName("entity-grid");
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addColumn(Student::getStudentNumber).setHeader("Opiskelijanumero").setAutoWidth(true);
        grid.addColumn(Student::getFullName).setHeader("Nimi").setAutoWidth(true);
        grid.addColumn(Student::getEmail).setHeader("Sähköposti").setAutoWidth(true);
        grid.addColumn(student -> student.getDepartment() != null ? student.getDepartment().getName() : "-")
                .setHeader("Osasto").setAutoWidth(true);
        grid.addColumn(student -> student.getProfile() != null ? student.getProfile().getPostalCode() : "Ei profiilia")
                .setHeader("1:1 Profiili").setAutoWidth(true);
        grid.addColumn(student -> student.getClubs().stream().map(Club::getName).sorted().reduce((left, right) -> left + ", " + right)
                .orElse("Ei kerhoja")).setHeader("M:N Kerhot").setFlexGrow(1);
        grid.asSingleSelect().addValueChangeListener(event -> editStudent(event.getValue()));
    }

    private void refreshGrid() {
        grid.setItems(studentService.findAll(filter.getValue()));
    }

    private void editStudent(Student student) {
        if (student == null) {
            closeEditor();
            return;
        }
        form.setStudent(student);
        form.setVisible(true);
        addClassName("editing");
    }

    private void closeEditor() {
        form.setStudent(null);
        form.setVisible(false);
        removeClassName("editing");
        grid.asSingleSelect().clear();
    }

    private void saveStudent(Student student) {
        try {
            studentService.save(student);
            Notification.show("Opiskelija tallennettu");
            refreshGrid();
            closeEditor();
        } catch (DataIntegrityViolationException exception) {
            Notification.show("Tallennus epäonnistui. Tarkista uniikit kentät, kuten sähköposti ja opiskelijanumero.");
        }
    }

    private void deleteStudent(Student student) {
        studentService.delete(student);
        Notification.show("Opiskelija poistettu");
        refreshGrid();
        closeEditor();
    }

    public static class StudentForm extends VerticalLayout {

        private final BeanValidationBinder<Student> binder = new BeanValidationBinder<>(Student.class);

        private final TextField firstName = new TextField("Etunimi");
        private final TextField lastName = new TextField("Sukunimi");
        private final EmailField email = new EmailField("Sähköposti");
        private final TextField studentNumber = new TextField("Opiskelijanumero");
        private final TextField city = new TextField("Kotikunta");
        private final TextField phone = new TextField("Puhelin");
        private final IntegerField enrollmentYear = new IntegerField("Aloitusvuosi");
        private final ComboBox<Department> department = new ComboBox<>("Osasto");
        private final MultiSelectComboBox<Club> clubs = new MultiSelectComboBox<>("Kerhot");

        private final Button save = new Button("Tallenna");
        private final Button delete = new Button("Poista");
        private final Button cancel = new Button("Sulje");

        private Student student;

        public StudentForm() {
            addClassName("editor-form");
            setWidth("420px");

            department.setItemLabelGenerator(Department::getName);
            clubs.setItemLabelGenerator(Club::getName);
            clubs.setHelperText("Valitse yksi tai useampi kerho");

            binder.forField(firstName).bind(Student::getFirstName, Student::setFirstName);
            binder.forField(lastName).bind(Student::getLastName, Student::setLastName);
            binder.forField(email).bind(Student::getEmail, Student::setEmail);
            binder.forField(studentNumber).bind(Student::getStudentNumber, Student::setStudentNumber);
            binder.forField(city).bind(Student::getCity, Student::setCity);
            binder.forField(phone).bind(Student::getPhone, Student::setPhone);
            binder.forField(enrollmentYear).bind(Student::getEnrollmentYear, Student::setEnrollmentYear);
            binder.forField(department)
                    .asRequired("Osasto on pakollinen")
                    .bind(Student::getDepartment, Student::setDepartment);
            binder.forField(clubs)
                    .asRequired("Valitse vähintään yksi kerho")
                    .bind(Student::getClubs, Student::setClubs);

            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);

            save.addClickListener(event -> validateAndSave());
            delete.addClickListener(event -> fireEvent(new DeleteEvent(this, student)));
            cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));

            add(new Span("Opiskelijan tiedot"), firstName, lastName, email, studentNumber, city, phone, enrollmentYear,
                    department, clubs, new HorizontalLayout(save, delete, cancel));
        }

        public void setDepartments(List<Department> departments) {
            department.setItems(departments);
        }

        public void setClubs(List<Club> clubs) {
            this.clubs.setItems(clubs);
        }

        public void setStudent(Student student) {
            this.student = student;
            if (student == null) {
                binder.readBean(new Student());
                return;
            }
            if (student.getClubs().isEmpty()) {
                student.setClubs(new LinkedHashSet<>());
            }
            binder.readBean(student);
            delete.setVisible(student.getId() != null);
        }

        private void validateAndSave() {
            if (student == null) {
                student = new Student();
            }
            try {
                binder.writeBean(student);
                fireEvent(new SaveEvent(this, student));
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

    public abstract static class StudentFormEvent extends com.vaadin.flow.component.ComponentEvent<StudentForm> {

        private final Student student;

        protected StudentFormEvent(StudentForm source, Student student) {
            super(source, false);
            this.student = student;
        }

        public Student getStudent() {
            return student;
        }
    }

    public static class SaveEvent extends StudentFormEvent {
        SaveEvent(StudentForm source, Student student) {
            super(source, student);
        }
    }

    public static class DeleteEvent extends StudentFormEvent {
        DeleteEvent(StudentForm source, Student student) {
            super(source, student);
        }
    }

    public static class CloseEvent extends StudentFormEvent {
        CloseEvent(StudentForm source) {
            super(source, null);
        }
    }

}
