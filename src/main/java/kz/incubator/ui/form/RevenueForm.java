package kz.incubator.ui.form;

import kz.incubator.backend.entity.user.Role;
import kz.incubator.backend.service.IncubatorProjectService;
import kz.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import kz.incubator.ui.form.dto.EditRevenueDto;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class RevenueForm extends FormLayout {
    ComboBox<IncubatorProjectDto> project = new ComboBox<>("Incubator project");
    NumberField leaseRevenue = new NumberField("Lease revenue");
    NumberField serviceRevenue = new NumberField("Service revenue");
    NumberField sponsorshipRevenue = new NumberField("Sponsorship revenue");
    NumberField grantRevenue = new NumberField("Grant revenue");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<EditRevenueDto> binder = new BeanValidationBinder<>(EditRevenueDto.class);
    Label errorMessageLabel = new Label();

    public RevenueForm(UserDetails userDetails, IncubatorProjectService incubatorProjectService) {
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
        add(project, leaseRevenue, serviceRevenue, sponsorshipRevenue, grantRevenue, errorMessageLabel, getButtonsLayout());
    }

    public Label getErrorMessageLabel() {
        return errorMessageLabel;
    }

    public void setEditRevenueDto(EditRevenueDto editRevenueDto) {
        binder.setBean(editRevenueDto);
    }

    private HorizontalLayout getButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(e -> validateAndSave());
        delete.addClickListener(e -> fireEvent(new RevenueForm.DeleteEvent(this, binder.getBean())));
        close.addClickListener(e -> fireEvent(new RevenueForm.CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new RevenueForm.SaveEvent(this, binder.getBean()));
        }
    }

    public void addSaveListener(ComponentEventListener<RevenueForm.SaveEvent> listener) {
        addListener(RevenueForm.SaveEvent.class, listener);
    }

    public void addDeleteListener(ComponentEventListener<RevenueForm.DeleteEvent> listener) {
        addListener(RevenueForm.DeleteEvent.class, listener);
    }

    public void addCloseEditorListener(ComponentEventListener<RevenueForm.CloseEvent> listener) {
        addListener(RevenueForm.CloseEvent.class, listener);
    }

    // Event classes
    public static abstract class RevenueFormEvent extends ComponentEvent<RevenueForm> {
        private EditRevenueDto editRevenueDto;

        public RevenueFormEvent(RevenueForm revenueForm, EditRevenueDto editRevenueDto) {
            super(revenueForm, false);
            this.editRevenueDto = editRevenueDto;
        }

        public EditRevenueDto getEditRevenueDto() {
            return editRevenueDto;
        }
    }

    public static class SaveEvent extends RevenueForm.RevenueFormEvent {
        public SaveEvent(RevenueForm revenueForm, EditRevenueDto editRevenueDto) {
            super(revenueForm, editRevenueDto);
        }
    }

    public static class DeleteEvent extends RevenueForm.RevenueFormEvent {
        public DeleteEvent(RevenueForm revenueForm, EditRevenueDto editRevenueDto) {
            super(revenueForm, editRevenueDto);
        }
    }

    public static class CloseEvent extends RevenueForm.RevenueFormEvent {
        public CloseEvent(RevenueForm revenueForm) {
            super(revenueForm, null);
        }
    }
}
