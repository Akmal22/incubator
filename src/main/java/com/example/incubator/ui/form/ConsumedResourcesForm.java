package com.example.incubator.ui.form;

import com.example.incubator.backend.entity.user.Role;
import com.example.incubator.backend.service.IncubatorProjectService;
import com.example.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import com.example.incubator.ui.form.dto.EditConsumedResourcesDto;
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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class ConsumedResourcesForm extends FormLayout {
    ComboBox<IncubatorProjectDto> project = new ComboBox<>("Incubator project");
    IntegerField involvedManagers = new IntegerField("Involved managers");
    IntegerField involvedCoaches = new IntegerField("Involved coaches");
    IntegerField involvedMentors = new IntegerField("Involved mentors");
    IntegerField usedServices = new IntegerField("Used services");
    NumberField rentSpace = new NumberField("Rent space");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<EditConsumedResourcesDto> binder = new BeanValidationBinder<>(EditConsumedResourcesDto.class);
    Label errorMessageLabel = new Label();

    public ConsumedResourcesForm(UserDetails userDetails, IncubatorProjectService incubatorProjectService) {
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

        involvedManagers.setReadOnly(false);
        involvedMentors.setReadOnly(false);
        involvedCoaches.setReadOnly(false);
        usedServices.setReadOnly(false);
        errorMessageLabel.getStyle().set("color", "Red");
        add(project, involvedManagers, involvedMentors, involvedCoaches, usedServices, rentSpace, errorMessageLabel, getButtonsLayout());
    }

    public Label getErrorMessageLabel() {
        return errorMessageLabel;
    }

    public void setEditConsumedResourcesDto(EditConsumedResourcesDto editConsumedResourcesDto) {
        binder.setBean(editConsumedResourcesDto);
    }

    private HorizontalLayout getButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(e -> validateAndSave());
        delete.addClickListener(e -> fireEvent(new ConsumedResourcesForm.DeleteEvent(this, binder.getBean())));
        close.addClickListener(e -> fireEvent(new ConsumedResourcesForm.CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new ConsumedResourcesForm.SaveEvent(this, binder.getBean()));
        }
    }

    public void addSaveListener(ComponentEventListener<ConsumedResourcesForm.SaveEvent> listener) {
        addListener(ConsumedResourcesForm.SaveEvent.class, listener);
    }

    public void addDeleteListener(ComponentEventListener<ConsumedResourcesForm.DeleteEvent> listener) {
        addListener(ConsumedResourcesForm.DeleteEvent.class, listener);
    }

    public void addCloseEditorListener(ComponentEventListener<ConsumedResourcesForm.CloseEvent> listener) {
        addListener(ConsumedResourcesForm.CloseEvent.class, listener);
    }

    // Event classes
    public static abstract class ConsumedResourcesEvent extends ComponentEvent<ConsumedResourcesForm> {
        private EditConsumedResourcesDto editConsumedResourcesDto;

        public ConsumedResourcesEvent(ConsumedResourcesForm consumedResourcesForm, EditConsumedResourcesDto editConsumedResourcesDto) {
            super(consumedResourcesForm, false);
            this.editConsumedResourcesDto = editConsumedResourcesDto;
        }

        public EditConsumedResourcesDto getEditConsumedResourcesEvent() {
            return editConsumedResourcesDto;
        }
    }

    public static class SaveEvent extends ConsumedResourcesForm.ConsumedResourcesEvent {
        public SaveEvent(ConsumedResourcesForm consumedResourcesForm, EditConsumedResourcesDto editConsumedResourcesDto) {
            super(consumedResourcesForm, editConsumedResourcesDto);
        }
    }

    public static class DeleteEvent extends ConsumedResourcesForm.ConsumedResourcesEvent {
        public DeleteEvent(ConsumedResourcesForm consumedResourcesForm, EditConsumedResourcesDto editConsumedResourcesDto) {
            super(consumedResourcesForm, editConsumedResourcesDto);
        }
    }

    public static class CloseEvent extends ConsumedResourcesForm.ConsumedResourcesEvent {
        public CloseEvent(ConsumedResourcesForm consumedResourcesForm) {
            super(consumedResourcesForm, null);
        }
    }
}
