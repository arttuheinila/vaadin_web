package com.example.application.views.clubs;

import org.springframework.dao.DataIntegrityViolationException;

import com.example.application.data.Club;
import com.example.application.data.service.ClubService;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "clubs", layout = MainLayout.class)
@PageTitle("Kerhot")
@RolesAllowed({ "SUPER", "USER" })
public class ClubView extends Div {

    private final ClubService clubService;
    private final Grid<Club> grid = new Grid<>(Club.class, false);
    private final TextField filter = new TextField();
    private final ClubForm form = new ClubForm();

    public ClubView(ClubService clubService) {
        this.clubService = clubService;

        addClassName("crud-view");
        setSizeFull();

        configureGrid();
        form.addSaveListener(event -> saveClub(event.getClub()));
        form.addDeleteListener(event -> deleteClub(event.getClub()));
        form.addCloseListener(event -> closeEditor());

        add(buildToolbar(), buildContent());
        refreshGrid();
        closeEditor();
    }

    private HorizontalLayout buildToolbar() {
        filter.setPlaceholder("Suodata kerhoja");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(event -> refreshGrid());

        Button addClub = new Button("Lisää kerho", event -> editClub(new Club()));
        addClub.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return new HorizontalLayout(filter, addClub);
    }

    private Component buildContent() {
        SplitLayout content = new SplitLayout(grid, form);
        content.setSizeFull();
        return content;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addColumn(Club::getName).setHeader("Kerho").setAutoWidth(true);
        grid.addColumn(Club::getCategory).setHeader("Kategoria").setAutoWidth(true);
        grid.addColumn(Club::getMeetingDay).setHeader("Päivä").setAutoWidth(true);
        grid.addColumn(Club::getMeetingTime).setHeader("Aika").setAutoWidth(true);
        grid.addColumn(Club::getRoom).setHeader("Tila").setAutoWidth(true);
        grid.addColumn(club -> club.getStudents().stream().map(student -> student.getFullName()).sorted()
                .reduce((left, right) -> left + ", " + right).orElse("Ei jäseniä")).setHeader("M:N Jäsenet")
                .setFlexGrow(1);
        grid.asSingleSelect().addValueChangeListener(event -> editClub(event.getValue()));
    }

    private void refreshGrid() {
        grid.setItems(clubService.findAll(filter.getValue()));
    }

    private void editClub(Club club) {
        if (club == null) {
            closeEditor();
            return;
        }
        form.setClub(club);
        form.setVisible(true);
    }

    private void closeEditor() {
        form.setClub(null);
        form.setVisible(false);
        grid.asSingleSelect().clear();
    }

    private void saveClub(Club club) {
        try {
            clubService.save(club);
            Notification.show("Kerho tallennettu");
            refreshGrid();
            closeEditor();
        } catch (DataIntegrityViolationException exception) {
            Notification.show("Tallennus epäonnistui. Kerhon nimen tulee olla uniikki.");
        }
    }

    private void deleteClub(Club club) {
        clubService.delete(club);
        Notification.show("Kerho poistettu");
        refreshGrid();
        closeEditor();
    }

    public static class ClubForm extends VerticalLayout {

        private final BeanValidationBinder<Club> binder = new BeanValidationBinder<>(Club.class);

        private final TextField name = new TextField("Kerhon nimi");
        private final TextField category = new TextField("Kategoria");
        private final TextArea description = new TextArea("Kuvaus");
        private final TextField meetingDay = new TextField("Kokoontumispäivä");
        private final TimePicker meetingTime = new TimePicker("Kokoontumisaika");
        private final TextField room = new TextField("Tila");

        private final Button save = new Button("Tallenna");
        private final Button delete = new Button("Poista");
        private final Button cancel = new Button("Sulje");

        private Club club;

        public ClubForm() {
            addClassName("editor-form");
            setWidth("420px");

            description.setMaxLength(250);

            binder.forField(name).bind(Club::getName, Club::setName);
            binder.forField(category).bind(Club::getCategory, Club::setCategory);
            binder.forField(description).bind(Club::getDescription, Club::setDescription);
            binder.forField(meetingDay).bind(Club::getMeetingDay, Club::setMeetingDay);
            binder.forField(meetingTime).bind(Club::getMeetingTime, Club::setMeetingTime);
            binder.forField(room).bind(Club::getRoom, Club::setRoom);

            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);

            save.addClickListener(event -> validateAndSave());
            delete.addClickListener(event -> fireEvent(new DeleteEvent(this, club)));
            cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));

            add(new Span("Kerhon tiedot"), name, category, description, meetingDay, meetingTime, room,
                    new HorizontalLayout(save, delete, cancel));
        }

        public void setClub(Club club) {
            this.club = club;
            if (club == null) {
                binder.readBean(new Club());
                return;
            }
            binder.readBean(club);
            delete.setVisible(club.getId() != null);
        }

        private void validateAndSave() {
            if (club == null) {
                club = new Club();
            }
            try {
                binder.writeBean(club);
                fireEvent(new SaveEvent(this, club));
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

    public abstract static class ClubFormEvent extends com.vaadin.flow.component.ComponentEvent<ClubForm> {

        private final Club club;

        protected ClubFormEvent(ClubForm source, Club club) {
            super(source, false);
            this.club = club;
        }

        public Club getClub() {
            return club;
        }
    }

    public static class SaveEvent extends ClubFormEvent {
        SaveEvent(ClubForm source, Club club) {
            super(source, club);
        }
    }

    public static class DeleteEvent extends ClubFormEvent {
        DeleteEvent(ClubForm source, Club club) {
            super(source, club);
        }
    }

    public static class CloseEvent extends ClubFormEvent {
        CloseEvent(ClubForm source) {
            super(source, null);
        }
    }

}
