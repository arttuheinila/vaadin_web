package com.example.application.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Kirjaudu sisään")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);

        UnorderedList credentials = new UnorderedList(
                new ListItem("admin / admin123"),
                new ListItem("super / super123"),
                new ListItem("user / user123"));
        credentials.addClassName("login-credentials");

        VerticalLayout card = new VerticalLayout(new H2("Opiskelijahallinta"),
                new Paragraph("Kirjaudu sisään nähdäksesi suojatut CRUD-näkymät, roolipohjaisen navigaation ja hakutoiminnot."),
                new Paragraph("Testitunnukset:"),
                credentials,
                login);
        card.addClassName("login-card");
        card.setSpacing(true);
        card.setPadding(true);
        card.setMaxWidth("420px");

        add(card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        login.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}
