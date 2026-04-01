package com.example.application.views.departments;

import org.springframework.dao.DataIntegrityViolationException;

import com.example.application.data.Department;
import com.example.application.data.service.DepartmentService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "departments", layout = MainLayout.class)
@PageTitle("Osastot")
@RolesAllowed("ADMIN")
public class DepartmentView extends Div {

    private final DepartmentService departmentService;
    private final Grid<Department> grid = new Grid<>(Department.class, false);
    private final TextField filter = new TextField();
    private final DepartmentForm form = new DepartmentForm();

    public DepartmentView(DepartmentService departmentService) {
        this.departmentService = departmentService;

        addClassName("crud-view");
        setSizeFull();

        configureGrid();
        form.addSaveListener(event -> saveDepartment(event.getDepartment()));
        form.addDeleteListener(event -> deleteDepartment(event.getDepartment()));
        form.addCloseListener(event -> closeEditor());

        add(buildToolbar(), buildContent());
        refreshGrid();
        closeEditor();
    }

    private HorizontalLayout buildToolbar() {
        filter.setPlaceholder("Suodata osastoja");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(event -> refreshGrid());

        Button addDepartment = new Button("Lisää osasto", event -> editDepartment(new Department()));
        addDepartment.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return new HorizontalLayout(filter, addDepartment);
    }

    private Component buildContent() {
        SplitLayout content = new SplitLayout(grid, form);
        content.setSizeFull();
        return content;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addColumn(Department::getName).setHeader("Osasto").setAutoWidth(true);
        grid.addColumn(Department::getCode).setHeader("Koodi").setAutoWidth(true);
        grid.addColumn(Department::getBuilding).setHeader("Rakennus").setAutoWidth(true);
        grid.addColumn(Department::getEmail).setHeader("Sähköposti").setAutoWidth(true);
        grid.addColumn(Department::getPhone).setHeader("Puhelin").setAutoWidth(true);
        grid.addColumn(department -> department.getAnnualBudget().toPlainString() + " €").setHeader("Budjetti")
                .setAutoWidth(true);
        grid.asSingleSelect().addValueChangeListener(event -> editDepartment(event.getValue()));
    }

    private void refreshGrid() {
        grid.setItems(departmentService.findAll(filter.getValue()));
    }

    private void editDepartment(Department department) {
        if (department == null) {
            closeEditor();
            return;
        }
        form.setDepartment(department);
        form.setVisible(true);
    }

    private void closeEditor() {
        form.setDepartment(null);
        form.setVisible(false);
        grid.asSingleSelect().clear();
    }

    private void saveDepartment(Department department) {
        try {
            departmentService.save(department);
            Notification.show("Osasto tallennettu");
            refreshGrid();
            closeEditor();
        } catch (DataIntegrityViolationException exception) {
            Notification.show("Tallennus epäonnistui. Tarkista uniikit kentät, kuten koodi ja sähköposti.");
        }
    }

    private void deleteDepartment(Department department) {
        departmentService.delete(department);
        Notification.show("Osasto poistettu. Siltä opiskelijoilta poistettiin osastoviite.");
        refreshGrid();
        closeEditor();
    }

    public static class DepartmentForm extends VerticalLayout {

        private final BeanValidationBinder<Department> binder = new BeanValidationBinder<>(Department.class);

        private final TextField name = new TextField("Osaston nimi");
        private final TextField code = new TextField("Koodi");
        private final TextField building = new TextField("Rakennus");
        private final EmailField email = new EmailField("Sähköposti");
        private final TextField phone = new TextField("Puhelin");
        private final BigDecimalField annualBudget = new BigDecimalField("Vuotuinen budjetti");

        private final Button save = new Button("Tallenna");
        private final Button delete = new Button("Poista");
        private final Button cancel = new Button("Sulje");

        private Department department;

        public DepartmentForm() {
            addClassName("editor-form");
            setWidth("420px");

            annualBudget.setSuffixComponent(new Span("€"));

            binder.forField(name).bind(Department::getName, Department::setName);
            binder.forField(code).bind(Department::getCode, Department::setCode);
            binder.forField(building).bind(Department::getBuilding, Department::setBuilding);
            binder.forField(email).bind(Department::getEmail, Department::setEmail);
            binder.forField(phone).bind(Department::getPhone, Department::setPhone);
            binder.forField(annualBudget).bind(Department::getAnnualBudget, Department::setAnnualBudget);

            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);

            save.addClickListener(event -> validateAndSave());
            delete.addClickListener(event -> fireEvent(new DeleteEvent(this, department)));
            cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));

            add(new Span("Osaston tiedot"), name, code, building, email, phone, annualBudget,
                    new HorizontalLayout(save, delete, cancel));
        }

        public void setDepartment(Department department) {
            this.department = department;
            if (department == null) {
                binder.readBean(new Department());
                return;
            }
            binder.readBean(department);
            delete.setVisible(department.getId() != null);
        }

        private void validateAndSave() {
            if (department == null) {
                department = new Department();
            }
            try {
                binder.writeBean(department);
                fireEvent(new SaveEvent(this, department));
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

    public abstract static class DepartmentFormEvent extends com.vaadin.flow.component.ComponentEvent<DepartmentForm> {

        private final Department department;

        protected DepartmentFormEvent(DepartmentForm source, Department department) {
            super(source, false);
            this.department = department;
        }

        public Department getDepartment() {
            return department;
        }
    }

    public static class SaveEvent extends DepartmentFormEvent {
        SaveEvent(DepartmentForm source, Department department) {
            super(source, department);
        }
    }

    public static class DeleteEvent extends DepartmentFormEvent {
        DeleteEvent(DepartmentForm source, Department department) {
            super(source, department);
        }
    }

    public static class CloseEvent extends DepartmentFormEvent {
        CloseEvent(DepartmentForm source) {
            super(source, null);
        }
    }

}
