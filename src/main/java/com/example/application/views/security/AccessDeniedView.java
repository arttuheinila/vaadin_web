package com.example.application.views.security;

import com.example.application.views.MainLayout;
import com.example.application.views.LoginView;
import com.example.application.views.home.HomeView;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AccessDeniedException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.server.HttpStatusCode;
import com.vaadin.flow.server.auth.AccessDeniedErrorRouter;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;

@Tag("section")
@AnonymousAllowed
@ParentLayout(MainLayout.class)
@AccessDeniedErrorRouter
public class AccessDeniedView extends VerticalLayout implements HasErrorParameter<AccessDeniedException> {

    private final AuthenticationContext authenticationContext;

    public AccessDeniedView(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
        addClassName("access-denied-view");
        setWidthFull();
        setSpacing(true);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<AccessDeniedException> parameter) {
        removeAll();

        String username = authenticationContext.getPrincipalName().orElse("vieras");
        String path = "/" + event.getLocation().getPath();

        H2 title = new H2("Käyttöoikeudet eivät riitä");
        Paragraph description = new Paragraph(
                "Käyttäjä " + username + " ei voi avata sivua " + path
                        + ". Tarkista roolisi tai kirjaudu toisella tunnuksella.");

        Button homeButton = new Button("Palaa etusivulle", click -> UI.getCurrent().navigate(HomeView.class));
        homeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button loginButton = new Button(authenticationContext.isAuthenticated() ? "Vaihda käyttäjää"
                : "Siirry kirjautumaan", click -> UI.getCurrent().navigate(LoginView.class));

        HorizontalLayout actions = new HorizontalLayout(homeButton, loginButton);
        actions.addClassName("access-denied-actions");

        VerticalLayout card = new VerticalLayout(title, description, actions);
        card.addClassName("access-denied-card");
        card.setPadding(true);
        card.setSpacing(true);

        add(card);
        return HttpStatusCode.FORBIDDEN.getCode();
    }
}
