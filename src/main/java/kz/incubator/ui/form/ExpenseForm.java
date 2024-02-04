package kz.incubator.ui.form;

import kz.incubator.backend.entity.user.Role;
import kz.incubator.backend.service.IncubatorProjectService;
import kz.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import kz.incubator.ui.form.dto.EditExpenseDto;
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

public class ExpenseForm extends FormLayout {
    ComboBox<IncubatorProjectDto> project = new ComboBox<>("Incubator project");
    NumberField marketing = new NumberField("Marketing expenses");
    NumberField payroll = new NumberField("Payroll expenses");
    NumberField equipment = new NumberField("Equipment expenses");
    NumberField utilities = new NumberField("Utilities expenses");
    NumberField material = new NumberField("Material expenses");
    NumberField insurance = new NumberField("Insurance expenses");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<EditExpenseDto> binder = new BeanValidationBinder<>(EditExpenseDto.class);
    Label errorMessageLabel = new Label();

    public ExpenseForm(UserDetails userDetails, IncubatorProjectService incubatorProjectService) {
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
        add(project, marketing, payroll, equipment, utilities, material, insurance, errorMessageLabel, getButtonsLayout());
    }

    public Label getErrorMessageLabel() {
        return errorMessageLabel;
    }

    public void setEditExpenseDto(EditExpenseDto editExpenseDto) {
        binder.setBean(editExpenseDto);
    }

    private HorizontalLayout getButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(e -> validateAndSave());
        delete.addClickListener(e -> fireEvent(new ExpenseForm.DeleteEvent(this, binder.getBean())));
        close.addClickListener(e -> fireEvent(new ExpenseForm.CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new ExpenseForm.SaveEvent(this, binder.getBean()));
        }
    }

    // Event classes
    public static abstract class ExpenseFormEvent extends ComponentEvent<ExpenseForm> {
        private EditExpenseDto editExpenseDto;

        public ExpenseFormEvent(ExpenseForm expenseForm, EditExpenseDto editExpenseDto) {
            super(expenseForm, false);
            this.editExpenseDto = editExpenseDto;
        }

        public EditExpenseDto getEditExpenseDto() {
            return editExpenseDto;
        }
    }

    public static class SaveEvent extends ExpenseForm.ExpenseFormEvent {
        public SaveEvent(ExpenseForm expenseForm, EditExpenseDto editExpenseDto) {
            super(expenseForm, editExpenseDto);
        }
    }

    public static class DeleteEvent extends ExpenseForm.ExpenseFormEvent {
        public DeleteEvent(ExpenseForm expenseForm, EditExpenseDto editExpenseDto) {
            super(expenseForm, editExpenseDto);
        }
    }

    public static class CloseEvent extends ExpenseForm.ExpenseFormEvent {
        public CloseEvent(ExpenseForm expenseForm) {
            super(expenseForm, null);
        }
    }

    public void addSaveListener(ComponentEventListener<ExpenseForm.SaveEvent> listener) {
        addListener(ExpenseForm.SaveEvent.class, listener);
    }

    public void addDeleteListener(ComponentEventListener<ExpenseForm.DeleteEvent> listener) {
        addListener(ExpenseForm.DeleteEvent.class, listener);
    }

    public void addCloseEditorListener(ComponentEventListener<ExpenseForm.CloseEvent> listener) {
        addListener(ExpenseForm.CloseEvent.class, listener);
    }
}
