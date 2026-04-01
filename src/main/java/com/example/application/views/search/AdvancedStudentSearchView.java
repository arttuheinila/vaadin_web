package com.example.application.views.search;

import java.time.LocalDate;

import com.example.application.data.Club;
import com.example.application.data.Department;
import com.example.application.data.Student;
import com.example.application.data.search.StudentSearchCriteria;
import com.example.application.data.service.ClubService;
import com.example.application.data.service.DepartmentService;
import com.example.application.data.service.StudentService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxShadow;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "advanced-search", layout = MainLayout.class)
@PageTitle("Edistynyt haku")
@StyleSheet("advanced-search-view.css")
@RolesAllowed({ "SUPER", "USER" })
public class AdvancedStudentSearchView extends VerticalLayout {

    private final StudentService studentService;

    private final TextField filterText = new TextField("Hakusana");
    private final TextField city = new TextField("Kotikunta");
    private final com.vaadin.flow.component.combobox.ComboBox<Department> department = new com.vaadin.flow.component.combobox.ComboBox<>(
            "Osasto");
    private final com.vaadin.flow.component.combobox.ComboBox<Club> club = new com.vaadin.flow.component.combobox.ComboBox<>(
            "Kerho");
    private final TextField emergencyContact = new TextField("Hätäyhteyshenkilö");
    private final DatePicker birthDateFrom = new DatePicker("Syntymäpäivä alkaen");
    private final DatePicker birthDateTo = new DatePicker("Syntymäpäivä asti");

    private final Span resultSummary = new Span();
    private final Grid<Student> grid = new Grid<>(Student.class, false);

    public AdvancedStudentSearchView(StudentService studentService, DepartmentService departmentService,
            ClubService clubService) {
        this.studentService = studentService;

        addClassName("advanced-search-view");
        addClassName("crud-view");
        setSizeFull();

        configureFields(departmentService, clubService);
        configureGrid();

        add(buildIntro(), buildSearchPanel(), resultSummary, grid);
        runSearch();
    }

    private Div buildIntro() {
        Div intro = new Div();
        intro.addClassName("search-intro");
        intro.addClassNames(Background.BASE, Padding.LARGE, BoxShadow.SMALL, BorderRadius.LARGE, Width.FULL);
        intro.add(new H3("Criteria API - opiskelijahaku"),
                new Paragraph(
                        "Tämä näkymä rakentaa dynaamisen CriteriaQueryn. Predikaatti lisätään mukaan vain silloin, kun kentässä on arvo."),
                new Paragraph(
                        "Hakusana muodostaa OR-lohkon: etunimi OR sukunimi OR sähköposti OR opiskelijanumero. Muut ehdot yhdistyvät siihen AND-logiikalla."));
        return intro;
    }

    private Div buildSearchPanel() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(filterText, city, department, club, emergencyContact, birthDateFrom, birthDateTo);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("760px", 2),
                new FormLayout.ResponsiveStep("1100px", 3));

        Button search = new Button("Hae", event -> runSearch());
        search.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button clear = new Button("Tyhjennä", event -> clearFilters());
        clear.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actions = new HorizontalLayout(search, clear);
        actions.addClassNames(Display.FLEX, Margin.NONE);

        Div searchPanel = new Div(formLayout, actions);
        searchPanel.addClassName("search-panel");
        searchPanel.addClassNames(Background.BASE, Padding.MEDIUM, BoxShadow.SMALL, BorderRadius.LARGE);
        return searchPanel;
    }

    private void configureFields(DepartmentService departmentService, ClubService clubService) {
        filterText.setPlaceholder("nimi, sähköposti tai opiskelijanumero");
        city.setPlaceholder("esim. Tampere");
        emergencyContact.setPlaceholder("esim. Leena");
        filterText.getStyle().set("min-width", "18rem");
        city.getStyle().set("min-width", "14rem");
        resultSummary.getStyle().set("border-left", "4px solid var(--app-accent)");
        resultSummary.getStyle().set("background", "rgba(255,255,255,0.65)");

        department.setItems(departmentService.findAll());
        department.setItemLabelGenerator(Department::getName);
        department.setClearButtonVisible(true);

        club.setItems(clubService.findAll());
        club.setItemLabelGenerator(Club::getName);
        club.setClearButtonVisible(true);

        birthDateFrom.setClearButtonVisible(true);
        birthDateTo.setClearButtonVisible(true);

        resultSummary.addClassNames(TextColor.SECONDARY, FontSize.SMALL, Padding.MEDIUM, BorderRadius.MEDIUM,
                Width.FULL);
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addColumn(Student::getStudentNumber).setHeader("Opiskelijanumero").setAutoWidth(true);
        grid.addColumn(Student::getFullName).setHeader("Nimi").setAutoWidth(true);
        grid.addColumn(Student::getEmail).setHeader("Sähköposti").setAutoWidth(true);
        grid.addColumn(Student::getCity).setHeader("Kotikunta").setAutoWidth(true);
        grid.addColumn(student -> student.getDepartment() != null ? student.getDepartment().getName() : "-")
                .setHeader("JOIN Osasto").setAutoWidth(true);
        grid.addColumn(student -> student.getProfile() != null ? student.getProfile().getBirthDate() : null)
                .setHeader("Päivämäärä").setAutoWidth(true);
        grid.addColumn(student -> student.getProfile() != null ? student.getProfile().getEmergencyContactName() : "-")
                .setHeader("JOIN Profiilin yhteyshenkilö").setAutoWidth(true);
        grid.addColumn(student -> student.getClubs().stream().map(Club::getName).sorted()
                .reduce((left, right) -> left + ", " + right).orElse("Ei kerhoja"))
                .setHeader("JOIN Kerhot").setFlexGrow(1);
    }

    private void runSearch() {
        if (birthDateFrom.getValue() != null && birthDateTo.getValue() != null
                && birthDateFrom.getValue().isAfter(birthDateTo.getValue())) {
            Notification.show("Päivämäärävälin alku ei voi olla lopun jälkeen");
            return;
        }

        StudentSearchCriteria criteria = new StudentSearchCriteria();
        criteria.setFilterText(filterText.getValue());
        criteria.setCity(city.getValue());
        criteria.setDepartmentId(department.getValue() != null ? department.getValue().getId() : null);
        criteria.setClubId(club.getValue() != null ? club.getValue().getId() : null);
        criteria.setEmergencyContactName(emergencyContact.getValue());
        criteria.setBirthDateFrom(birthDateFrom.getValue());
        criteria.setBirthDateTo(birthDateTo.getValue());

        var results = studentService.advancedSearch(criteria);
        grid.setItems(results);
        resultSummary.setText(buildSummary(criteria, results.size()));
    }

    private void clearFilters() {
        filterText.clear();
        city.clear();
        department.clear();
        club.clear();
        emergencyContact.clear();
        birthDateFrom.clear();
        birthDateTo.clear();
        runSearch();
    }

    private String buildSummary(StudentSearchCriteria criteria, int count) {
        String logic = "(etunimi OR sukunimi OR sähköposti OR opiskelijanumero)";
        if (isBlank(criteria.getFilterText())) {
            logic = "ei OR-lohkoa";
        }

        StringBuilder summary = new StringBuilder("Tuloksia: ").append(count).append(" | Käytössä: ").append(logic);
        if (criteria.getDepartmentId() != null) {
            summary.append(" AND osasto");
        }
        if (criteria.getClubId() != null) {
            summary.append(" AND kerho");
        }
        if (!isBlank(criteria.getCity())) {
            summary.append(" AND kotikunta");
        }
        if (!isBlank(criteria.getEmergencyContactName())) {
            summary.append(" AND profiilin hätäyhteys");
        }
        if (criteria.getBirthDateFrom() != null || criteria.getBirthDateTo() != null) {
            summary.append(" AND syntymäpäiväväli");
        }
        return summary.toString();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
