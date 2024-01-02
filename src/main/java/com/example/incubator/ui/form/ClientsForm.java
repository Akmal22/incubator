package com.example.incubator.ui.form;

import com.example.incubator.backend.entity.user.Role;
import com.example.incubator.backend.service.IncubatorProjectService;
import com.example.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import com.example.incubator.ui.form.dto.EditClientsDto;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class ClientsForm extends FormLayout {
    ComboBox<IncubatorProjectDto> project = new ComboBox<>("Incubator project");
    IntegerField applications = new IntegerField("Applications for participation");
    IntegerField accepted = new IntegerField("Accepted applications");
    IntegerField graduated = new IntegerField("Graduated participants");
    IntegerField failed = new IntegerField("Failed participants");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<EditClientsDto> binder = new BeanValidationBinder<>(EditClientsDto.class);
    Label errorMessageLabel = new Label();

    public ClientsForm(UserDetails userDetails, IncubatorProjectService incubatorProjectService) {
        addClassName("contact-form");
        binder.bindInstanceFields(this);

        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_BI_MANAGER.name()))) {
            project.setItemsWithFilterConverter(
                    query -> incubatorProjectService.getManagerAllProjects(userDetails.getUsername(),
                            query.getFilter().orElse(""),
                            PageRequest.of(query.getPage(), query.getLimit())).stream(),
                    project -> project);
        } else {
            project.setItemsWithFilterConverter(
                    query -> incubatorProjectService.getAllProjects(query.getFilter().orElse(""),
                            PageRequest.of(query.getPage(), query.getLimit())).stream(),
                    incubator -> incubator);
        }
        project.setItemLabelGenerator(IncubatorProjectDto::getName);

        errorMessageLabel.getStyle().set("color", "Red");
        applications.setReadOnly(false);
        accepted.setReadOnly(false);
        graduated.setReadOnly(false);
        failed.setReadOnly(false);
        add(project, applications, accepted, graduated, failed, getButtonsLayout());
    }

    public Label getErrorMessageLabel() {
        return errorMessageLabel;
    }

    public void setEditClientsDto(EditClientsDto editClientsDto) {
        binder.setBean(editClientsDto);
    }

    // Event classes
    public static abstract class ClientsFormEvent extends ComponentEvent<ClientsForm> {
        private EditClientsDto editClientsDto;

        public ClientsFormEvent(ClientsForm clientsForm, EditClientsDto editClientsDto) {
            super(clientsForm, false);
            this.editClientsDto = editClientsDto;
        }

        public EditClientsDto getEditClientsDto() {
            return editClientsDto;
        }
    }

    public static class SaveEvent extends ClientsForm.ClientsFormEvent {
        public SaveEvent(ClientsForm clientsForm, EditClientsDto editClientsDto) {
            super(clientsForm, editClientsDto);
        }
    }

    public static class DeleteEvent extends ClientsForm.ClientsFormEvent {
        public DeleteEvent(ClientsForm clientsForm, EditClientsDto editClientsDto) {
            super(clientsForm, editClientsDto);
        }
    }

    public static class CloseEvent extends ClientsForm.ClientsFormEvent {
        public CloseEvent(ClientsForm clientsForm) {
            super(clientsForm, null);
        }
    }

    public void addSaveListener(ComponentEventListener<ClientsForm.SaveEvent> listener) {
        addListener(ClientsForm.SaveEvent.class, listener);
    }

    public void addDeleteListener(ComponentEventListener<ClientsForm.DeleteEvent> listener) {
        addListener(ClientsForm.DeleteEvent.class, listener);
    }

    public void addCloseEditorListener(ComponentEventListener<ClientsForm.CloseEvent> listener) {
        addListener(ClientsForm.CloseEvent.class, listener);
    }

    private HorizontalLayout getButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(e -> validateAndSave());
        delete.addClickListener(e -> fireEvent(new ClientsForm.DeleteEvent(this, binder.getBean())));
        close.addClickListener(e -> fireEvent(new ClientsForm.CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new ClientsForm.SaveEvent(this, binder.getBean()));
        }
    }
}
