package com.example.incubator.ui.form;

import com.example.incubator.backend.entity.user.Role;
import com.example.incubator.backend.service.CountriesService;
import com.example.incubator.backend.service.UserService;
import com.example.incubator.backend.service.dto.CountryDto;
import com.example.incubator.backend.service.dto.user.UserDto;
import com.example.incubator.ui.form.dto.EditIncubatorDto;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;


public class IncubatorForm extends FormLayout {
    TextField name = new TextField("Incubator name");
    ComboBox<CountryDto> country = new ComboBox<>("Country");
    ComboBox<UserDto> manager = new ComboBox<>("Manger");
    DatePicker founded = new DatePicker("Founded");
    TextField founder = new TextField("Founder");


    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<EditIncubatorDto> binder = new BeanValidationBinder<>(EditIncubatorDto.class);
    Label errorMessageLabel = new Label();

    public IncubatorForm(CountriesService countriesService, UserService userService, UserDetails userDetails) {
        addClassName("contact-form");
        binder.bindInstanceFields(this);

        country.setItemsWithFilterConverter(query -> countriesService.pageSearch(
                query.getFilter().orElse(""), PageRequest.of(query.getPage(), query.getLimit())).stream(), country -> country);
        country.setItemLabelGenerator(CountryDto::getCountryName);

        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_BI_MANAGER.name()))) {
            manager.setItems(List.of(userService.getManager(userDetails.getUsername())));
        } else {
            manager.setItemsWithFilterConverter(
                    query -> userService.getAllManagersPageable(PageRequest.of(query.getPage(), query.getLimit())).stream(),
                    manager -> manager);
        }
        manager.setItemLabelGenerator(UserDto::getUsername);

        errorMessageLabel.getStyle().set("color", "Red");
        add(name, country, manager, founded, founder, errorMessageLabel, getButtonsLayout());
    }

    private HorizontalLayout getButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(e -> validateAndSave());
        delete.addClickListener(e -> fireEvent(new IncubatorForm.DeleteEvent(this, binder.getBean())));
        close.addClickListener(e -> fireEvent(new IncubatorForm.CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    public Label getErrorMessageLabel() {
        return errorMessageLabel;
    }

    public void setEditIncubatorDto(EditIncubatorDto editIncubatorDto) {
        binder.setBean(editIncubatorDto);
    }

    // Listeners
    public void addSaveListener(ComponentEventListener<IncubatorForm.SaveEvent> listener) {
        addListener(IncubatorForm.SaveEvent.class, listener);
    }

    public void addDeleteListener(ComponentEventListener<IncubatorForm.DeleteEvent> listener) {
        addListener(IncubatorForm.DeleteEvent.class, listener);
    }

    public void addCloseEditorListener(ComponentEventListener<IncubatorForm.CloseEvent> listener) {
        addListener(IncubatorForm.CloseEvent.class, listener);
    }

    // Event classes
    public static abstract class IncubatorFormEvent extends ComponentEvent<IncubatorForm> {
        private EditIncubatorDto editIncubatorDto;

        public IncubatorFormEvent(IncubatorForm incubatorForm, EditIncubatorDto editIncubatorDto) {
            super(incubatorForm, false);
            this.editIncubatorDto = editIncubatorDto;
        }

        public EditIncubatorDto getEditIncubatorDto() {
            return editIncubatorDto;
        }
    }

    public static class SaveEvent extends IncubatorForm.IncubatorFormEvent {
        public SaveEvent(IncubatorForm incubatorForm, EditIncubatorDto editIncubatorDto) {
            super(incubatorForm, editIncubatorDto);
        }
    }

    public static class DeleteEvent extends IncubatorForm.IncubatorFormEvent {
        public DeleteEvent(IncubatorForm incubatorForm, EditIncubatorDto editIncubatorDto) {
            super(incubatorForm, editIncubatorDto);
        }
    }

    public static class CloseEvent extends IncubatorForm.IncubatorFormEvent {
        public CloseEvent(IncubatorForm incubatorForm) {
            super(incubatorForm, null);
        }
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new IncubatorForm.SaveEvent(this, binder.getBean()));
        }
    }
}
