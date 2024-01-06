package com.example.incubator.ui.form;

import com.example.incubator.backend.entity.user.Role;
import com.example.incubator.backend.service.IncubatorProjectService;
import com.example.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import com.example.incubator.ui.form.dto.EditInvestmentDto;
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

public class InvestmentForm extends FormLayout {
    ComboBox<IncubatorProjectDto> project = new ComboBox<>("Incubator project");
    IntegerField investorsCount = new IntegerField("Investors count");
    IntegerField percentageOfInvestedClients = new IntegerField("Percentage of invested clients");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<EditInvestmentDto> binder = new BeanValidationBinder<>(EditInvestmentDto.class);
    Label errorMessageLabel = new Label();

    public InvestmentForm(UserDetails userDetails, IncubatorProjectService incubatorProjectService) {
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

        investorsCount.setMin(0);
        investorsCount.setMax(100);
        investorsCount.setReadOnly(false);
        percentageOfInvestedClients.setMin(0);
        percentageOfInvestedClients.setReadOnly(false);
        errorMessageLabel.getStyle().set("color", "Red");
        add(project, investorsCount, percentageOfInvestedClients, errorMessageLabel, getButtonsLayout());
    }

    public Label getErrorMessageLabel() {
        return errorMessageLabel;
    }

    public void setEditInvestmentDto(EditInvestmentDto editInvestmentDto) {
        binder.setBean(editInvestmentDto);
    }

    private HorizontalLayout getButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(e -> validateAndSave());
        delete.addClickListener(e -> fireEvent(new InvestmentForm.DeleteEvent(this, binder.getBean())));
        close.addClickListener(e -> fireEvent(new InvestmentForm.CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new InvestmentForm.SaveEvent(this, binder.getBean()));
        }
    }

    public void addSaveListener(ComponentEventListener<InvestmentForm.SaveEvent> listener) {
        addListener(InvestmentForm.SaveEvent.class, listener);
    }

    public void addDeleteListener(ComponentEventListener<InvestmentForm.DeleteEvent> listener) {
        addListener(InvestmentForm.DeleteEvent.class, listener);
    }

    public void addCloseEditorListener(ComponentEventListener<InvestmentForm.CloseEvent> listener) {
        addListener(InvestmentForm.CloseEvent.class, listener);
    }

    // Event classes
    public static abstract class InvestmentFormEvent extends ComponentEvent<InvestmentForm> {
        private EditInvestmentDto editInvestmentDto;

        public InvestmentFormEvent(InvestmentForm investmentForm, EditInvestmentDto editInvestmentDto) {
            super(investmentForm, false);
            this.editInvestmentDto = editInvestmentDto;
        }

        public EditInvestmentDto getEditInvestmentDto() {
            return editInvestmentDto;
        }
    }

    public static class SaveEvent extends InvestmentForm.InvestmentFormEvent {
        public SaveEvent(InvestmentForm investmentForm, EditInvestmentDto editInvestmentDto) {
            super(investmentForm, editInvestmentDto);
        }
    }

    public static class DeleteEvent extends InvestmentForm.InvestmentFormEvent {
        public DeleteEvent(InvestmentForm investmentForm, EditInvestmentDto editInvestmentDto) {
            super(investmentForm, editInvestmentDto);
        }
    }

    public static class CloseEvent extends InvestmentForm.InvestmentFormEvent {
        public CloseEvent(InvestmentForm investmentForm) {
            super(investmentForm, null);
        }
    }
}
