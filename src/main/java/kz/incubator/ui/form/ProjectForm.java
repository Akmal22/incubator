package kz.incubator.ui.form;

import kz.incubator.backend.entity.user.Role;
import kz.incubator.backend.service.IncubatorService;
import kz.incubator.backend.service.dto.incubator.IncubatorDto;
import kz.incubator.ui.form.dto.EditProjectDto;
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

public class ProjectForm extends FormLayout {
    TextField name = new TextField("Incubator project name");
    ComboBox<IncubatorDto> incubator = new ComboBox<>("Incubator");
    DatePicker startDate = new DatePicker("Started date");
    DatePicker endDate = new DatePicker("End date");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<EditProjectDto> binder = new BeanValidationBinder<>(EditProjectDto.class);
    Label errorMessageLabel = new Label();

    public ProjectForm(UserDetails userDetails, IncubatorService incubatorService) {
        addClassName("contact-form");
        binder.bindInstanceFields(this);

        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_BI_MANAGER.name()))) {
            incubator.setItemsWithFilterConverter(
                    query -> incubatorService.getManagerIncubatorsPageable(userDetails.getUsername(), PageRequest.of(query.getPage(), query.getLimit())).stream(),
                    incubator -> incubator);
        } else {
            incubator.setItemsWithFilterConverter(
                    query -> incubatorService.getAllIncubators(PageRequest.of(query.getPage(), query.getLimit())).stream(),
                    incubator -> incubator);
        }
        incubator.setItemLabelGenerator(IncubatorDto::getIncubatorName);

        errorMessageLabel.getStyle().set("color", "Red");
        add(name, incubator, startDate, endDate, errorMessageLabel, getButtonsLayout());
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
