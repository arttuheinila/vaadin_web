package com.example.application.views.home;

import java.util.Locale;

import com.example.application.data.service.ClubService;
import com.example.application.data.service.DepartmentService;
import com.example.application.data.service.StudentProfileService;
import com.example.application.data.service.StudentService;
import com.example.application.views.MainLayout;
import com.example.application.views.search.AdvancedStudentSearchView;
import com.example.application.views.students.StudentView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Etusivu")
@PermitAll
public class HomeView extends VerticalLayout implements LocaleChangeObserver {

    private final H2 headline = new H2();
    private final Paragraph description = new Paragraph();
    private final Button studentsButton = createNavigationButton(StudentView.class, VaadinIcon.USERS);
    private final Button searchButton = createNavigationButton(AdvancedStudentSearchView.class, VaadinIcon.SEARCH);

    private final StatCard studentsStat;
    private final StatCard departmentsStat;
    private final StatCard profilesStat;
    private final StatCard clubsStat;

    private final FeaturePanel shellPanel = new FeaturePanel();
    private final FeaturePanel layoutsPanel = new FeaturePanel();
    private final FeaturePanel responsivePanel = new FeaturePanel();

    public HomeView(StudentService studentService, DepartmentService departmentService,
            StudentProfileService profileService, ClubService clubService) {
        addClassName("home-view");
        setSizeFull();

        Div hero = new Div();
        hero.addClassName("home-hero");
        HorizontalLayout actions = new HorizontalLayout(
                studentsButton,
                searchButton);
        actions.addClassName("home-actions");
        hero.add(headline, description, actions);

        studentsStat = new StatCard(String.valueOf(studentService.findAll("").size()));
        departmentsStat = new StatCard(String.valueOf(departmentService.findAll().size()));
        profilesStat = new StatCard(String.valueOf(profileService.findAll("").size()));
        clubsStat = new StatCard(String.valueOf(clubService.findAll().size()));

        HorizontalLayout stats = new HorizontalLayout(
                studentsStat,
                departmentsStat,
                profilesStat,
                clubsStat);
        stats.addClassName("home-stats");
        stats.setWidthFull();

        HorizontalLayout sections = new HorizontalLayout(
                shellPanel,
                layoutsPanel,
                responsivePanel);
        sections.addClassName("home-sections");
        sections.setWidthFull();

        add(hero, stats, sections);
        updateTexts(resolveLocale(getLocale()));
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        updateTexts(resolveLocale(event.getLocale()));
    }

    private Button createNavigationButton(Class<? extends com.vaadin.flow.component.Component> target, VaadinIcon icon) {
        Button button = new Button(icon.create());
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(event -> button.getUI().ifPresent(ui -> ui.navigate(target)));
        return button;
    }

    private void updateTexts(Locale locale) {
        boolean english = Locale.ENGLISH.getLanguage().equals(locale.getLanguage());

        headline.setText(english ? "Single-page student management" : "Yksisivuinen opiskelijahallinta");
        description.setText(english
                ? "The home page uses the shared MainLayout shell. From here you can move to CRUD views, advanced search and relation data without leaving the SPA structure."
                : "Etusivu käyttää yhteistä MainLayout-kuorta. Täältä siirryt CRUD-näkymiin, edistyneeseen hakuun ja relaatioiden tarkasteluun ilman että SPA-rakenne vaihtuu.");

        studentsButton.setText(english ? "Open students" : "Avaa opiskelijat");
        searchButton.setText(english ? "Open advanced search" : "Avaa edistynyt haku");

        studentsStat.setTexts(english ? "Students" : "Opiskelijat", english ? "Main entity" : "Pääentiteetti");
        departmentsStat.setTexts(english ? "Departments" : "Osastot", "1:N");
        profilesStat.setTexts(english ? "Profiles" : "Profiilit", "1:1");
        clubsStat.setTexts(english ? "Clubs" : "Kerhot", "M:N");

        shellPanel.setTexts(english ? "SPA shell" : "SPA-kuori",
                english
                        ? "Header, navigation and footer come from MainLayout. Each view is rendered inside the same shell."
                        : "Header, navigaatio ja footer tulevat MainLayoutista. Jokainen näkymä renderöityy saman kuoren sisällä.");
        layoutsPanel.setTexts(english ? "Different layouts" : "Erilaiset näkymät",
                english
                        ? "The home page is card-based, the student page uses a split CRUD layout and advanced search uses a filter form with a result grid."
                        : "Etusivu on korttipohjainen, opiskelijasivu käyttää split-layout CRUD-rakennetta ja edistynyt haku yhdistää suodatinlomakkeen sekä tulosgridin.");
        responsivePanel.setTexts(english ? "Responsive structure" : "Responsiivinen rakenne",
                english
                        ? "Cards, toolbars and footer scale down cleanly on smaller screens while the navigation stays usable."
                        : "Kortit, toolbarit ja footer skaalautuvat siististi pienemmille näytöille samalla kun navigaatio pysyy käytettävänä.");
    }

    private Locale resolveLocale(Locale locale) {
        if (locale != null && "fi".equals(locale.getLanguage())) {
            return Locale.forLanguageTag("fi");
        }
        return Locale.ENGLISH;
    }

    private static final class StatCard extends Div {

        private final Paragraph title = new Paragraph();
        private final H2 value = new H2();
        private final Paragraph subtitle = new Paragraph();

        private StatCard(String valueText) {
            addClassName("home-stat-card");
            value.setText(valueText);
            add(title, value, subtitle);
        }

        private void setTexts(String titleText, String subtitleText) {
            title.setText(titleText);
            subtitle.setText(subtitleText);
        }
    }

    private static final class FeaturePanel extends Div {

        private final H2 title = new H2();
        private final Paragraph description = new Paragraph();

        private FeaturePanel() {
            addClassName("home-feature-panel");
            add(title, description);
        }

        private void setTexts(String titleText, String descriptionText) {
            title.setText(titleText);
            description.setText(descriptionText);
        }
    }
}
