package com.example.incubator.ui.form;

import com.example.incubator.back.service.dto.form.country.EditCountryDto;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

public class CountryForm extends FormLayout {
    TextField countryName = new TextField("Country name");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<EditCountryDto> binder = new BeanValidationBinder<>(EditCountryDto.class);
    Label errorMessageLabel = new Label();

    public CountryForm() {
        addClassName("contact-form");
        binder.bindInstanceFields(this);

        errorMessageLabel.getStyle().set("color", "Red");

        add(countryName, errorMessageLabel, getButtonsLayout());
    }

    private HorizontalLayout getButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(e -> validateAndSave());
        delete.addClickListener(e -> fireEvent(new CountryForm.DeleteEvent(this, binder.getBean())));
        close.addClickListener(e -> fireEvent(new CountryForm.CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    public Label getErrorMessageLabel() {
        return errorMessageLabel;
    }

    public void setCountryDto(EditCountryDto countryDto) {
        binder.setBean(countryDto);
    }

    public static abstract class CountryFormEvent extends ComponentEvent<CountryForm> {
        private EditCountryDto editCountryDto;

        public CountryFormEvent(CountryForm userForm, EditCountryDto editCountryDto) {
            super(userForm, false);
            this.editCountryDto = editCountryDto;
        }

        public EditCountryDto getUserDto() {
            return editCountryDto;
        }
    }

    public static class SaveEvent extends CountryForm.CountryFormEvent {
        public SaveEvent(CountryForm countryForm,
                         EditCountryDto editCountryDto) {
            super(countryForm, editCountryDto);
        }
    }

    public static class DeleteEvent extends CountryForm.CountryFormEvent {
        public DeleteEvent(CountryForm countryForm, EditCountryDto editCountryDto) {
            super(countryForm, editCountryDto);
        }
    }

    public static class CloseEvent extends CountryForm.CountryFormEvent {
        public CloseEvent(CountryForm countryForm) {
            super(countryForm, null);
        }
    }

    public void addSaveListener(ComponentEventListener<CountryForm.SaveEvent> listener) {
        addListener(CountryForm.SaveEvent.class, listener);
    }

    public void addDeleteListener(ComponentEventListener<CountryForm.DeleteEvent> listener) {
        addListener(CountryForm.DeleteEvent.class, listener);
    }

    public void addCloseEditorListener(ComponentEventListener<CountryForm.CloseEvent> listener) {
        addListener(CountryForm.CloseEvent.class, listener);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }
}
