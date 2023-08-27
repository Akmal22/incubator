package com.example.incubator.ui.form;

import com.example.incubator.back.service.dto.incubator.IncubatorDto;
import com.example.incubator.ui.dto.EditProjectDto;
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
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;

public class ProjectForm extends FormLayout {
    TextField name = new TextField("Incubator project name");
    ComboBox<IncubatorDto> incubator = new ComboBox<>("Incubator");
    NumberField income = new NumberField("Income");
    NumberField expenses = new NumberField("Expenses");
    IntegerField residentApplications = new IntegerField("Applications");
    IntegerField acceptedResidentApplications = new IntegerField("Accepted applications");
    IntegerField graduatedResidentsCount = new IntegerField("Graduated residents");
    DatePicker startDate = new DatePicker("Started date");
    DatePicker endDate = new DatePicker("End date");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<EditProjectDto> binder = new BeanValidationBinder<>(EditProjectDto.class);
    Label errorMessageLabel = new Label();

    public ProjectForm(List<IncubatorDto> incubators) {
        addClassName("contact-form");
        binder.bindInstanceFields(this);

        incubator.setItems(incubators);
        incubator.setItemLabelGenerator(IncubatorDto::getIncubatorName);

        errorMessageLabel.getStyle().set("color", "Red");
        add(name, incubator, income, expenses, residentApplications, acceptedResidentApplications,
                graduatedResidentsCount, startDate, endDate, getButtonsLayout());
    }

    public Label getErrorMessageLabel() {
        return errorMessageLabel;
    }

    public void setEditProjectDto(EditProjectDto editProjectDto) {
        binder.setBean(editProjectDto);
    }

    // Event classes
    public static abstract class ProjectFormEvent extends ComponentEvent<ProjectForm> {
        private EditProjectDto editProjectDto;

        public ProjectFormEvent(ProjectForm projectForm, EditProjectDto editProjectDto) {
            super(projectForm, false);
            this.editProjectDto = editProjectDto;
        }

        public EditProjectDto getEditProjectDto() {
            return editProjectDto;
        }
    }

    public static class SaveEvent extends ProjectForm.ProjectFormEvent {
        public SaveEvent(ProjectForm projectForm, EditProjectDto editProjectDto) {
            super(projectForm, editProjectDto);
        }
    }

    public static class DeleteEvent extends ProjectForm.ProjectFormEvent {
        public DeleteEvent(ProjectForm projectForm, EditProjectDto editProjectDto) {
            super(projectForm, editProjectDto);
        }
    }

    public static class CloseEvent extends ProjectForm.ProjectFormEvent {
        public CloseEvent(ProjectForm projectForm) {
            super(projectForm, null);
        }
    }

    public void addSaveListener(ComponentEventListener<ProjectForm.SaveEvent> listener) {
        addListener(ProjectForm.SaveEvent.class, listener);
    }

    public void addDeleteListener(ComponentEventListener<ProjectForm.DeleteEvent> listener) {
        addListener(ProjectForm.DeleteEvent.class, listener);
    }

    public void addCloseEditorListener(ComponentEventListener<ProjectForm.CloseEvent> listener) {
        addListener(ProjectForm.CloseEvent.class, listener);
    }

    private HorizontalLayout getButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(e -> validateAndSave());
        delete.addClickListener(e -> fireEvent(new ProjectForm.DeleteEvent(this, binder.getBean())));
        close.addClickListener(e -> fireEvent(new ProjectForm.CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new ProjectForm.SaveEvent(this, binder.getBean()));
        }
    }
}
