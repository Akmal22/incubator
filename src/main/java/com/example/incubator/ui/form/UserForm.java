package com.example.incubator.ui.form;

import com.example.incubator.back.entity.user.Role;
import com.example.incubator.back.service.dto.form.user.EditUserDto;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

public class UserForm extends FormLayout {
    TextField username = new TextField("Username");
    PasswordField password = new PasswordField("Password");
    EmailField email = new EmailField("Email");
    ComboBox<Role> role = new ComboBox<>("Role");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<EditUserDto> binder = new BeanValidationBinder<>(EditUserDto.class);
    Label errorMessageLabel = new Label();

    public UserForm() {
        addClassName("contact-form");
        binder.bindInstanceFields(this);

        role.setItems(Role.ROLE_USER, Role.ROLE_BI_MANAGER);
        role.setItemLabelGenerator(Role::getName);

        errorMessageLabel.getStyle().set("color", "Red");

        add(username, password, email, role, errorMessageLabel, getButtonsLayout());
    }

    private HorizontalLayout getButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(e -> validateAndSave());
        delete.addClickListener(e -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(e -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    public Label getErrorMessageLabel() {
        return errorMessageLabel;
    }

    public void setUser(EditUserDto user) {
        binder.setBean(user);
    }

    public void setUsernameReadOnly(boolean usernameReadOnly) {
        username.setReadOnly(usernameReadOnly);
    }

    // Listeners
    public void addSaveListener(ComponentEventListener<SaveEvent> listener) {
        addListener(SaveEvent.class, listener);
    }

    public void addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        addListener(DeleteEvent.class, listener);
    }

    public void addCloseEditorListener(ComponentEventListener<CloseEvent> listener) {
        addListener(CloseEvent.class, listener);
    }

    // Event classes
    public static abstract class UserFormEvent extends ComponentEvent<UserForm> {
        private EditUserDto editUserDto;

        public UserFormEvent(UserForm userForm, EditUserDto editUserDto) {
            super(userForm, false);
            this.editUserDto = editUserDto;
        }

        public EditUserDto getUserDto() {
            return editUserDto;
        }
    }

    public static class SaveEvent extends UserFormEvent {
        public SaveEvent(UserForm userForm, EditUserDto editUserDto) {
            super(userForm, editUserDto);
        }
    }

    public static class DeleteEvent extends UserFormEvent {
        public DeleteEvent(UserForm userForm, EditUserDto editUserDto) {
            super(userForm, editUserDto);
        }
    }

    public static class CloseEvent extends UserFormEvent {
        public CloseEvent(UserForm userForm) {
            super(userForm, null);
        }
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }
}
