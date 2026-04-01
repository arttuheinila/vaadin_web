package com.example.application.views;

import java.util.Collection;
import java.util.Locale;

import com.example.application.views.clubs.ClubView;
import com.example.application.views.departments.DepartmentView;
import com.example.application.views.home.HomeView;
import com.example.application.views.profiles.StudentProfileView;
import com.example.application.views.search.AdvancedStudentSearchView;
import com.example.application.views.students.StudentView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.security.AuthenticationContext;

import jakarta.annotation.security.PermitAll;

@PermitAll
public class MainLayout extends AppLayout {

    private static final Locale FINNISH = Locale.forLanguageTag("fi");
    private static final Locale ENGLISH = Locale.ENGLISH;

    private final VerticalLayout viewContainer = new VerticalLayout();
    private final AuthenticationContext authenticationContext;

    public MainLayout(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
        addClassName("main-layout");
        setPrimarySection(Section.DRAWER);

        addToNavbar(buildHeader());
        addToDrawer(buildDrawer());
        setContent(buildShell());
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        viewContainer.removeAll();
        if (content instanceof Component component) {
            component.getElement().getStyle().set("width", "100%");
            viewContainer.add(component);
        }
    }

    private HorizontalLayout buildHeader() {
        Icon logo = VaadinIcon.ACADEMY_CAP.create();
        logo.addClassName("app-logo");

        H1 title = new H1("Campus SPA");
        title.addClassName("app-title");

        Span userLabel = new Span(buildUserLabel());
        userLabel.addClassName("user-label");

        Select<Locale> localeSelect = buildLocaleSelect();

        Button authButton = authenticationContext.isAuthenticated()
                ? new Button("Kirjaudu ulos", event -> authenticationContext.logout())
                : new Button("Kirjaudu sisään", event -> UI.getCurrent().navigate(LoginView.class));
        authButton.addClassName("logout-button");

        HorizontalLayout brand = new HorizontalLayout(logo, title);
        brand.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        brand.addClassName("brand-group");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), brand, localeSelect, userLabel, authButton);
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        header.expand(brand);
        header.setWidthFull();
        header.addClassName("app-header");
        return header;
    }

    private Component buildDrawer() {
        Span eyebrow = new Span("Navigointi");
        eyebrow.addClassName("drawer-eyebrow");

        Span helper = new Span("Yksi kuori, useita näkymiä: etusivu, CRUD, haku ja relaatiot.");
        helper.addClassName("drawer-helper");

        VerticalLayout navigation = new VerticalLayout();
        navigation.add(createLink("Etusivu", HomeView.class, VaadinIcon.HOME));

        if (authenticationContext.isAuthenticated()) {
            navigation.add(createLink("Opiskelijat", StudentView.class, VaadinIcon.USERS));
        }
        if (authenticationContext.hasAnyRole("SUPER", "USER")) {
            navigation.add(createLink("Edistynyt haku", AdvancedStudentSearchView.class, VaadinIcon.SEARCH));
            navigation.add(createLink("Kerhot", ClubView.class, VaadinIcon.GROUP));
        }
        if (authenticationContext.hasAnyRole("SUPER", "ADMIN")) {
            navigation.add(createLink("Profiilit", StudentProfileView.class, VaadinIcon.USER_CARD));
        }
        if (authenticationContext.hasRole("ADMIN")) {
            navigation.add(createLink("Osastot", DepartmentView.class, VaadinIcon.BUILDING));
        }

        navigation.addClassName("drawer-nav");
        navigation.setPadding(false);
        navigation.setSpacing(false);

        VerticalLayout drawerContent = new VerticalLayout(eyebrow, helper, navigation);
        drawerContent.addClassName("drawer-content");
        drawerContent.setSpacing(false);
        drawerContent.setPadding(false);

        return new Scroller(drawerContent);
    }

    private Component buildShell() {
        viewContainer.addClassName("app-view-container");
        viewContainer.setPadding(false);
        viewContainer.setSpacing(false);
        viewContainer.setWidthFull();

        Footer footer = buildFooter();

        VerticalLayout shell = new VerticalLayout(viewContainer, footer);
        shell.addClassName("app-shell");
        shell.setPadding(false);
        shell.setSpacing(false);
        shell.setSizeFull();
        shell.expand(viewContainer);
        return shell;
    }

    private Footer buildFooter() {
        Span author = new Span("Tekijä: Omistaja");
        Span copyright = new Span("Copyright © 2026 Campus SPA");
        Anchor vaadinLink = new Anchor("https://vaadin.com", "Vaadin");
        vaadinLink.setTarget("_blank");
        Anchor springLink = new Anchor("https://spring.io/projects/spring-boot", "Spring Boot");
        springLink.setTarget("_blank");

        HorizontalLayout links = new HorizontalLayout(vaadinLink, springLink);
        links.addClassName("footer-links");

        Footer footer = new Footer(author, copyright, links);
        footer.addClassName("app-footer");
        return footer;
    }

    private RouterLink createLink(String text, Class<? extends Component> navigationTarget, VaadinIcon iconType) {
        Icon icon = iconType.create();
        icon.addClassName("nav-icon");

        Span label = new Span(text);
        label.addClassName("nav-label");

        RouterLink link = new RouterLink();
        link.setRoute(navigationTarget);
        link.add(icon, label);
        link.setHighlightCondition(HighlightConditions.sameLocation());
        link.addClassName("nav-link");
        return link;
    }

    private String buildUserLabel() {
        if (!authenticationContext.isAuthenticated()) {
            return "Käyttäjä: vieras";
        }

        Collection<String> roles = authenticationContext.getGrantedRoles();
        String roleText = roles.isEmpty() ? "ei rooleja" : String.join(", ", roles);
        return "Käyttäjä: " + authenticationContext.getPrincipalName().orElse("tuntematon") + " (" + roleText + ")";
    }

    private Select<Locale> buildLocaleSelect() {
        Select<Locale> localeSelect = new Select<>();
        localeSelect.setLabel("Kieli");
        localeSelect.setItems(FINNISH, ENGLISH);
        localeSelect.setItemLabelGenerator(locale -> locale.getLanguage().equals(FINNISH.getLanguage()) ? "Suomi" : "English");
        localeSelect.setValue(normalizeLocale(UI.getCurrent().getLocale()));
        localeSelect.addClassName("locale-select");
        localeSelect.addValueChangeListener(event -> {
            Locale selectedLocale = event.getValue();
            if (selectedLocale == null) {
                return;
            }
            UI ui = UI.getCurrent();
            ui.setLocale(selectedLocale);
            ui.getPage().reload();
        });
        return localeSelect;
    }

    private Locale normalizeLocale(Locale locale) {
        if (locale != null && FINNISH.getLanguage().equals(locale.getLanguage())) {
            return FINNISH;
        }
        return ENGLISH;
    }
}
